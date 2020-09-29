package com.example.application.service.message;

import com.example.domain.model.message.MessageId;
import com.example.domain.repository.MessageRepository;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.ImageData;
import com.example.domain.model.message.ImageKey;
import com.example.domain.model.message.Messages;

import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class MessageSearchService {

    private final MessageRepository messageRepository;

    private final PublishableFileStorage fileStorage;

    public MessageSearchService(MessageRepository messageRepository, PublishableFileStorage fileStorage) {
        this.messageRepository = messageRepository;
        this.fileStorage = fileStorage;
    }

    public Messages findBy(ChannelId channelId, MessageId nextMessageId) {
        return messageRepository.findBy(channelId, nextMessageId);
    }

    public ImageData findFileBy(ChannelId channelId, ImageKey imageKey) {
        return fileStorage.findImageData(channelId, imageKey);
    }
}
