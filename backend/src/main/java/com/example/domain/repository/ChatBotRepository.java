package com.example.domain.repository;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChatBot;

public interface ChatBotRepository {

    void add(ChatBot chatBot);

    void remove(AccountId accountId);

    ChatBot findBy(AccountId accountId);
}
