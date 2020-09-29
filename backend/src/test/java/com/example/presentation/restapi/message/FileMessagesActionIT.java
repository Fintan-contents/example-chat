package com.example.presentation.restapi.message;

import com.example.presentation.restapi.ExampleChatRestTestBase;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.RestMockHttpRequest;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class FileMessagesActionIT extends ExampleChatRestTestBase {

    private static Path uploadFile;

    private static Path errorExtensionFile;


    @BeforeClass
    public static void beforeClass() throws Exception {
        Path fileOutputDir = Path.of("target/tmp");
        if (Files.notExists(fileOutputDir)) {
            Files.createDirectory(fileOutputDir);
        }
        uploadFile = fileOutputDir.resolve("test.jpg");
        if (Files.notExists(uploadFile)) {
            Files.createFile(uploadFile);
        }
        errorExtensionFile = fileOutputDir.resolve("test.txt");
        if (Files.notExists(errorExtensionFile)) {
            Files.createFile(errorExtensionFile);
        }
    }

    @Test
    public void メッセージとしてファイルを投稿できる() throws Exception {
        login("user1@example.com", "pass123-");

        String boundary = new BigInteger(256, new Random()).toString();
        String formData = createFormData(uploadFile, boundary);

        Integer channelId = 1;
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/files")
                .setContentType(MediaType.MULTIPART_FORM_DATA + ";boundary=" + boundary)
                .setBody(formData);
        HttpResponse response = sendRequest(request);
        assertEquals(204, response.getStatusCode());

        validateByOpenAPI("post-channels-channelId-files", request, response);
    }

    @Test
    public void 許可された拡張子以外のファイルは投稿できない() throws Exception {
        login("user1@example.com", "pass123-");

        String boundary = new BigInteger(256, new Random()).toString();
        String formData = createFormData(errorExtensionFile, boundary);

        Integer channelId = 1;
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/files")
                .setContentType(MediaType.MULTIPART_FORM_DATA + ";boundary=" + boundary)
                .setBody(formData);
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void ファイルを送信しないとエラーになること() throws Exception {
        login("user1@example.com", "pass123-");
        String boundary = new BigInteger(256, new Random()).toString();

        Integer channelId = 1;
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/files")
                .setContentType(MediaType.MULTIPART_FORM_DATA + ";boundary=" + boundary);
        HttpResponse response = sendRequest(request);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void ログインしていない場合はファイルを投稿できない() throws Exception {
        loadCsrfToken();

        String boundary = new BigInteger(256, new Random()).toString();
        String formData = createFormData(uploadFile, boundary);

        Integer channelId = 1;
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/files")
                .setContentType(MediaType.MULTIPART_FORM_DATA + ";boundary=" + boundary)
                .setBody(formData);
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 参加していないチャンネルにはファイルを投稿できない() throws Exception {
        login("user1@example.com", "pass123-");

        String boundary = new BigInteger(256, new Random()).toString();
        String formData = createFormData(uploadFile, boundary);

        Integer channelId = 20001;
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/files")
                .setContentType(MediaType.MULTIPART_FORM_DATA + ";boundary=" + boundary)
                .setBody(formData);
        HttpResponse response = sendRequest(request);
        assertEquals(403, response.getStatusCode());
    }

    @Test
    public void 存在しないチャンネルにはファイルを投稿できない() throws Exception {
        login("user1@example.com", "pass123-");

        String boundary = new BigInteger(256, new Random()).toString();
        String formData = createFormData(uploadFile, boundary);

        Integer channelId = Integer.MAX_VALUE;
        RestMockHttpRequest request = post("/api/channels/" + channelId + "/files")
                .setContentType(MediaType.MULTIPART_FORM_DATA + ";boundary=" + boundary)
                .setBody(formData);
        HttpResponse response = sendRequest(request);
        assertEquals(404, response.getStatusCode());
    }

    private String createFormData(Path filePath, String boundary) throws IOException {
        return "--" + boundary + "\r\n" +
                "Content-Disposition: form-data" +
                "; name=\"image\"" +
                "; filename=\"" + filePath.getFileName() + "\";" + "\r\n" +
                "Content-Type: " + Files.probeContentType(filePath) +  "\r\n" + "\r\n"
                + Files.readString(filePath) + "\r\n" +
                "--" + boundary + "--";
    }
}