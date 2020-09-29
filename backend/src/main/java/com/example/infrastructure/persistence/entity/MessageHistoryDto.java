package com.example.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.sql.Timestamp;

@JsonPropertyOrder({ "チャンネル名", "ユーザー名", "メッセージ", "送信日時" })
public class MessageHistoryDto {

    @JsonProperty("チャンネル名")
    private String channelName;

    @JsonProperty("ユーザー名")
    private String userName;

    @JsonProperty("メッセージ")
    private String text;

    @JsonProperty("送信日時（UTC）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
    private Timestamp sendDateTime;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getSendDateTime() {
        return sendDateTime;
    }

    public void setSendDateTime(Timestamp sendDateTime) {
        this.sendDateTime = sendDateTime;
    }
}
