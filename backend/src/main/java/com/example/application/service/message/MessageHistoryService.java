package com.example.application.service.message;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.MessageHistoryData;
import com.example.domain.model.message.MessageHistoryFile;
import com.example.domain.model.message.MessageHistoryKey;

import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class MessageHistoryService {

    private final MessageHistoryFileExporter messageHistoryFileExporter;

    private final PublishableFileStorage publishableFileStorage;

    public MessageHistoryService(MessageHistoryFileExporter messageHistoryFileExporter,
            PublishableFileStorage publishableFileStorage) {
        this.messageHistoryFileExporter = messageHistoryFileExporter;
        this.publishableFileStorage = publishableFileStorage;
    }

    public MessageHistoryKey export(AccountId accountId, ChannelId channelId) {
        MessageHistoryFile messageHistoryFile = messageHistoryFileExporter.export(channelId);
        MessageHistoryKey messageHistoryKey = publishableFileStorage.save(channelId, messageHistoryFile);
        messageHistoryFile.tryDelete();
        return messageHistoryKey;
    }

    public MessageHistoryData findFileBy(ChannelId channelId, MessageHistoryKey messageHistoryKey) {
        return publishableFileStorage.findMessageHistoryData(channelId, messageHistoryKey);
    }
}
