package com.example.application.service.channel;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.Channel;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelMember;
import com.example.domain.model.channel.Channels;
import com.example.domain.model.message.LastReadMessage;
import com.example.domain.repository.ChannelMemberRepository;
import com.example.domain.repository.ChannelRepository;
import com.example.domain.repository.MessageRepository;
import com.example.domain.repository.ReadMessageRepository;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelMemberRegistrationService {

    private final ChannelRepository channelRepository;

    private final ChannelMemberRepository channelMemberRepository;

    private final MessageRepository messageRepository;

    private final ReadMessageRepository readMessageRepository;

    public ChannelMemberRegistrationService(ChannelRepository channelRepository,
            ChannelMemberRepository channelMemberRepository,
            MessageRepository messageRepository,
            ReadMessageRepository readMessageRepository) {
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
        this.messageRepository = messageRepository;
        this.readMessageRepository = readMessageRepository;
    }

    public void insert(ChannelId channelId, AccountId accountId) {
        ChannelMember channelMember = new ChannelMember(accountId);
        channelMemberRepository.add(channelId, channelMember);

        LastReadMessage lastReadMessage = LastReadMessage.empty(channelId, accountId);
        readMessageRepository.add(lastReadMessage);
    }

    public void delete(ChannelId channelId, AccountId accountId) {
        ChannelMember channelMember = new ChannelMember(accountId);
        channelMemberRepository.remove(channelId, channelMember);

        messageRepository.remove(channelId, accountId);
        readMessageRepository.remove(channelId, accountId);
    }

    public void deleteMembersChannel(AccountId accountId) {
        Channels membersChannel = channelRepository.findMembersChannel(accountId);
        for (Channel channel : membersChannel.asList()) {
            delete(channel.id(), accountId);
        }
    }
}