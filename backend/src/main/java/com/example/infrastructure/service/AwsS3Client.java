package com.example.infrastructure.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.example.application.service.message.PublishableFileStorage;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.ImageData;
import com.example.domain.model.message.ImageFile;
import com.example.domain.model.message.ImageKey;
import com.example.domain.model.message.MessageHistoryData;
import com.example.domain.model.message.MessageHistoryFile;
import com.example.domain.model.message.MessageHistoryKey;

import nablarch.core.repository.di.config.externalize.annotation.ConfigValue;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@SystemRepositoryComponent
public class AwsS3Client implements PublishableFileStorage {

    private final URI endpointOverride;
    private final String accessKeyId;
    private final String secretAccessKey;
    private final Region region;
    private final String bucketName;

    public AwsS3Client(@ConfigValue("${aws.s3.endpointOverride}") String endpointOverride,
            @ConfigValue("${aws.s3.accessKeyId}") String accessKeyId,
            @ConfigValue("${aws.s3.secretAccessKey}") String secretAccessKey,
            @ConfigValue("${aws.s3.region}") String region,
            @ConfigValue("${aws.s3.bucketName}") String bucketName) {
        this.endpointOverride = endpointOverride != null ? URI.create(endpointOverride) : null;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.region = Region.of(region);
        this.bucketName = bucketName;
    }

    private AwsCredentialsProvider buildAwsCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey));
    }

    @Override
    public ImageKey save(ChannelId channelId, ImageFile imageFile) {
        String key = putObject(channelId, imageFile.path(), imageFile.contentType());
        return new ImageKey(key);
    }

    @Override
    public MessageHistoryKey save(ChannelId channelId, MessageHistoryFile messageHistoryFile) {
        String key = putObject(channelId, messageHistoryFile.path(), "text/csv");
        return new MessageHistoryKey(key);
    }

    @Override
    public ImageData findImageData(ChannelId channelId, ImageKey imageKey) {
        return findFileData(channelId, imageKey.value(), ImageData::new);
    }

    @Override
    public MessageHistoryData findMessageHistoryData(ChannelId channelId, MessageHistoryKey messageHistoryKey) {
        return findFileData(channelId, messageHistoryKey.value(), MessageHistoryData::new);
    }

    private <T> T findFileData(ChannelId channelId, String fileKey, BiFunction<byte[], String, T> fn) {
        return applyS3(s3 -> {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(fileKey).build();
            try (ResponseInputStream<GetObjectResponse> in = s3.getObject(getObjectRequest)) {
                String channelIdAssociatedObject = in.response().metadata().get("channel-id");
                if (Objects.equals(channelIdAssociatedObject, channelId.value().toString()) == false) {
                    throw new RuntimeException();
                }
                byte[] data = in.readAllBytes();
                String contentType = in.response().contentType();
                return fn.apply(data, contentType);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private String putObject(ChannelId channelId, Path path, String contentType) {
        String key = UUID.randomUUID().toString();
        applyS3(s3 -> {
            createBucketIfNotExists(s3);

            PutObjectRequest putObjectRequest = PutObjectRequest
                    .builder().bucket(bucketName)
                    .key(key).contentType(contentType)
                    .metadata(Map.of("channel-id", channelId.value().toString())).build();
            s3.putObject(putObjectRequest, RequestBody.fromFile(path));
            return null;
        });
        return key;
    }

    private void createBucketIfNotExists(S3Client s3) {
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
        boolean isBucketExists = listBucketsResponse
                .buckets().stream()
                .anyMatch(bucket -> bucket.name().equals(bucketName));
        if (!isBucketExists) {
            createBucket(s3);
        }
    }

    private void createBucket(S3Client s3) {
        CreateBucketRequest createBucketRequest = CreateBucketRequest
                .builder().bucket(bucketName)
                .createBucketConfiguration(CreateBucketConfiguration.builder().locationConstraint(region.id()).build())
                .build();
        s3.createBucket(createBucketRequest);
    }

    private <T> T applyS3(Function<S3Client, T> fn) {
        S3ClientBuilder builder = S3Client.builder();
        if (endpointOverride != null) {
            // ローカルで検証するためにエンドポイントを設定可能にしている
            builder.endpointOverride(endpointOverride);
        }
        try (S3Client s3 = builder.credentialsProvider(buildAwsCredentialsProvider()).region(region).build()) {
            return fn.apply(s3);
        }
    }
}
