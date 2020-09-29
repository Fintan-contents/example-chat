package com.example.application.service.message;

import com.example.application.ApplicationConfig;
import com.example.domain.model.message.*;
import com.example.domain.repository.MessageRepository;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class MessagePostingService {

    private final MessageRepository messageRepository;

    private final PublishableFileStorage fileStorage;

    private final MessageIdProvider idProvider;

    private final MessageNotifier messageNotifier;

    private final ApplicationConfig applicationConfig;

    public MessagePostingService(MessageRepository messageRepository, PublishableFileStorage fileStorage,
                                 MessageIdProvider idProvider, MessageNotifier messageNotifier,
                                 ApplicationConfig applicationConfig) {
        this.messageRepository = messageRepository;
        this.fileStorage = fileStorage;
        this.idProvider = idProvider;
        this.messageNotifier = messageNotifier;
        this.applicationConfig = applicationConfig;
    }

    public void postText(ChannelId channelId, AccountId accountId, MessageText text) {
        postMessage(channelId, accountId, text, MessageType.TEXT);
    }

    public void postFile(ChannelId channelId, AccountId accountId, ImageFile imageFile) {
        ImageKey imageKey = fileStorage.save(channelId, imageFile);

        MessageText text = new MessageText(applicationConfig.backendBaseUrl() +
                "/api/channels/" + channelId.value() + "/files/" + imageKey.value());

        postMessage(channelId, accountId, text, MessageType.IMAGE);
    }

    private void postMessage(ChannelId channelId, AccountId accountId, MessageText text, MessageType type) {
        MessageId messageId = idProvider.generate();
        Message message = new Message(messageId, channelId, accountId, text, type, SendDateTime.now());
        messageRepository.add(message);
        messageNotifier.notify(message);
    }
}
