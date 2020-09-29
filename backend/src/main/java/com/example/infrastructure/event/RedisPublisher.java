package com.example.infrastructure.event;

import com.example.application.service.account.AccountSearchService;
import com.example.application.service.channel.ChannelMemberSearchService;
import com.example.domain.model.account.Account;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelMember;
import com.example.domain.model.channel.ChannelMembers;
import com.example.domain.model.channel.ChannelName;
import com.example.domain.model.message.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import nablarch.core.repository.di.config.externalize.annotation.ConfigValue;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SystemRepositoryComponent
public class RedisPublisher {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new JavaTimeModule());

    private final AccountSearchService accountSearchService;

    private final ChannelMemberSearchService channelMemberSearchService;

    private final RedisPubSubCommands<String, String> commands;

    public RedisPublisher(AccountSearchService accountSearchService,
            ChannelMemberSearchService channelMemberSearchService,
            @ConfigValue("${nablarch.lettuce.simple.uri}") String uri) {
        this.accountSearchService = accountSearchService;
        this.channelMemberSearchService = channelMemberSearchService;
        RedisClient redisClient = RedisClient.create(uri);
        StatefulRedisPubSubConnection<String, String> connection = redisClient.connectPubSub();
        this.commands = connection.sync();
    }

    private void publish(String type, ChannelId channelId, Object payload) {
        ChannelMembers channelMembers = channelMemberSearchService.findBy(channelId);
        publish(type, channelMembers, payload);
    }

    private void publish(String type, ChannelMembers channelMembers, Object payload) {
        List<Long> destinations = Stream
                .concat(channelMembers.members().stream().map(ChannelMember::accountId),
                        Stream.of(channelMembers.owner().accountId()))
                .map(AccountId::value).collect(Collectors.toList());
        Object data = Map.of("destinations", destinations, "payload", Map.of("type", type, "payload", payload));
        String json;
        try {
            json = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        commands.publish("notification", json);
    }

    public void sendMessage(ChannelId channelId, Message message) {
        Account sendAccount = accountSearchService.findById(message.accountId());
        WebSocketMessage payload = new WebSocketMessage(message, sendAccount);
        publish("message", channelId, payload);
    }

    public void notifyChannelNameChange(ChannelId channelId, ChannelName oldChannelName, ChannelName newChannelName) {
        Object payload = Map.of("oldChannelName", oldChannelName.value(), "newChannelName", newChannelName.value());
        publish("channelNameChanged", channelId, payload);
    }

    public void notifyChannelInvite(ChannelId channelId, ChannelName channelName) {
        Object payload = Map.of("channelId", channelId.value(), "channelName", channelName.value());
        publish("channelInvited", channelId, payload);
    }

    public void notifyChannelDelete(ChannelId channelId, ChannelMembers channelMembers) {
        Object payload = Map.of("deleteChannelId", channelId.value());
        publish("channelDeleted", channelMembers, payload);
    }

    public static class WebSocketMessage {

        public Long messageId;

        public Long channelId;

        public Long accountId;

        public String userName;

        public String text;

        public String type;

        public LocalDateTime sendDateTime;

        public WebSocketMessage(Message message, Account sendAccount) {
            // 画面表示時のMessageResponseと同じ形である必要がある。
            this.messageId = message.messageId().value();
            this.channelId = message.channelId().value();
            this.accountId = message.accountId().value();
            this.userName = sendAccount.userName().value();
            this.text = message.text().value();
            this.type = message.type().name();
            this.sendDateTime = message.sendDateTime().value();
        }
    }
}
