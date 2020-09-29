package com.example.application.service.channel;

import com.example.domain.repository.ChatBotRepository;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChatBot;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChatBotSearchService {

    private final ChatBotRepository chatBotRepository;

    public ChatBotSearchService(ChatBotRepository chatBotRepository) {
        this.chatBotRepository = chatBotRepository;
    }

    public ChatBot findBy(AccountId accountId) {
        return chatBotRepository.findBy(accountId);
    }

}
