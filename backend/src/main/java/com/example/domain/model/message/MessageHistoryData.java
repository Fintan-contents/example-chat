package com.example.domain.model.message;

public class MessageHistoryData {

    private final byte[] data;
    private final String contentType;

    public MessageHistoryData(byte[] data, String contentType) {
        this.data = data;
        this.contentType = contentType;
    }

    public byte[] data() {
        return data;
    }

    public String contentType() {
        return contentType;
    }
}
