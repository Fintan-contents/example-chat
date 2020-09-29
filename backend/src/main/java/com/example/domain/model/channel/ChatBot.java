package com.example.domain.model.channel;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.account.UserName;
import com.example.domain.model.channel.ChannelId;

public class ChatBot {

    private final ChannelId channelId;

    private final AccountId accountId;

    public ChatBot(ChannelId channelId, AccountId accountId) {
        this.channelId = channelId;
        this.accountId = accountId;
    }

    public ChannelId channelId() {
        return channelId;
    }

    public AccountId accountId() {
        return accountId;
    }

    public static class SystemUser {
        public static final AccountId ID = new AccountId(0L);

        public static final UserName NAME = new UserName("chatbot");
    }

}
