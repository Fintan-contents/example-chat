package com.example.application.service.channel;

import com.example.domain.repository.ChannelRepository;
import com.example.domain.repository.MessageRepository;
import com.example.domain.repository.ReadMessageRepository;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.Channel;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.Channels;
import com.example.domain.model.message.LastReadMessage;
import com.example.domain.model.message.MessageId;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelSearchService {

    private final ChannelRepository channelRepository;

    private final MessageRepository messageRepository;

    private final ReadMessageRepository readMessageRepository;

    public ChannelSearchService(ChannelRepository channelRepository, MessageRepository messageRepository,
            ReadMessageRepository readMessageRepository) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.readMessageRepository = readMessageRepository;
    }

    public Channel findBy(ChannelId channelId) {
        return channelRepository.findById(channelId);
    }

    public Channels findAll(AccountId accountId) {
        return channelRepository.findMembersChannel(accountId);
    }

    public boolean isReadAllMessages(ChannelId channelId, AccountId accountId) {
        LastReadMessage lastReadMessage = readMessageRepository.findBy(channelId, accountId);
        MessageId latestMessageId = messageRepository.findLatestMessageId(channelId);
        return lastReadMessage.isLastMessageId(latestMessageId);
    }
}
