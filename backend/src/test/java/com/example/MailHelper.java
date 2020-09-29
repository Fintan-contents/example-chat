package com.example;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.mail.internet.MimeUtility;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MailHelper {

    public Mail searchLatestMail() {
        List<Mail> mails = searchMail();
        return mails.get(0);
    }

    public List<Mail> searchMail() {
        try {
            String hostname;
            if (Objects.equals(System.getenv("CI"), "true")) {
                hostname = "mail";
            } else {
                hostname = "localhost";
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + hostname + ":8025/api/v2/messages")).GET().build();
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            try (JsonReader jsonReader = Json.createReader(new StringReader(response.body()))) {
                return jsonReader.readObject().getJsonArray("items").stream().map(this::toMailItem)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clearMail() {
        try {
            String hostname;
            if (Objects.equals(System.getenv("CI"), "true")) {
                hostname = "mail";
            } else {
                hostname = "localhost";
            }
            HttpRequest deleteRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://" + hostname + ":8025/api/v1/messages")).DELETE().build();
            HttpClient httpClient = HttpClient.newBuilder().build();
            httpClient.send(deleteRequest, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Mail toMailItem(JsonValue item) {
        try {
            String id = item.asJsonObject().getString("ID");
            JsonObject contentItem = item.asJsonObject().getJsonObject("Content");
            JsonObject headersItem = contentItem.getJsonObject("Headers");
            String from = headersItem.getJsonArray("From").getString(0);
            String to = headersItem.getJsonArray("To").getString(0);

            String encodedSubject = headersItem.getJsonArray("Subject").getString(0);
            String subject = MimeUtility.decodeText(encodedSubject);

            String encodedBody = contentItem.getString("Body");
            String body = new String(Base64.getMimeDecoder().decode(encodedBody));

            return new Mail(id, from, to, subject, body);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Mail {
        public final String id;
        public final String from;
        public final String to;
        public final String subject;
        public final String body;

        public Mail(String id, String from, String to, String subject, String body) {
            this.id = id;
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.body = body;
        }

        @Override
        public String toString() {
            return "MailItem{" + "id='" + id + '\'' + ", from='" + from + '\'' + ", to='" + to + '\'' + ", subject='"
                    + subject + '\'' + ", body='" + body + '\'' + '}';
        }
    }
}
