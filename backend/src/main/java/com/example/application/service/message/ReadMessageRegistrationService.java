package com.example.application.service.message;

import com.example.domain.repository.ReadMessageRepository;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.LastReadMessage;
import com.example.domain.model.message.MessageId;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ReadMessageRegistrationService {

    private final ReadMessageRepository readMessageRepository;

    public ReadMessageRegistrationService(ReadMessageRepository readMessageRepository) {
        this.readMessageRepository = readMessageRepository;
    }

    public void updateReadMessage(MessageId messageId, ChannelId channelId, AccountId accountId) {
        LastReadMessage currentLastReadMessage = readMessageRepository.findBy(channelId, accountId);
        if (currentLastReadMessage == null)
            return;
        if (messageId.value().equals(currentLastReadMessage.messageId().value()))
            return;

        LastReadMessage lastReadMessage = new LastReadMessage(messageId, channelId, accountId);
        readMessageRepository.update(lastReadMessage);
    }
}
