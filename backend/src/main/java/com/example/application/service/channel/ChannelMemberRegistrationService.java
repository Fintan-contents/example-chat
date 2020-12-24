package com.example.application.service.channel;

import com.example.application.AccountNotFoundException;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.Channel;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelMember;
import com.example.domain.model.channel.Channels;
import com.example.domain.model.message.LastReadMessage;
import com.example.domain.repository.*;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelMemberRegistrationService {

    private final ChannelRepository channelRepository;

    private final ChannelMemberRepository channelMemberRepository;

    private final MessageRepository messageRepository;

    private final ReadMessageRepository readMessageRepository;

    private final AccountRepository accountRepository;

    public ChannelMemberRegistrationService(ChannelRepository channelRepository,
            ChannelMemberRepository channelMemberRepository,
            MessageRepository messageRepository,
            ReadMessageRepository readMessageRepository,
            AccountRepository accountRepository) {
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
        this.messageRepository = messageRepository;
        this.readMessageRepository = readMessageRepository;
        this.accountRepository = accountRepository;
    }

    public void insert(ChannelId channelId, AccountId accountId) {
        if (!accountRepository.existsBy(accountId)) {
            throw new AccountNotFoundException();
        }
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