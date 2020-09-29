package com.example.application.service.channel;

import com.example.application.ChannelNameConflictException;
import com.example.domain.model.message.LastReadMessage;
import com.example.domain.repository.ChannelRepository;
import com.example.domain.repository.ChatBotRepository;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.*;
import com.example.domain.repository.ReadMessageRepository;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

import java.util.List;

@SystemRepositoryComponent
public class ChannelRegistrationService {

    private final ChannelRepository channelRepository;

    private final ChatBotRepository chatBotRepository;

    private final ReadMessageRepository readMessageRepository;

    private final ChannelIdProvider idProvider;

    public ChannelRegistrationService(ChannelRepository channelRepository, ChatBotRepository chatBotRepository,
            ReadMessageRepository readMessageRepository, ChannelIdProvider idProvider) {
        this.channelRepository = channelRepository;
        this.chatBotRepository = chatBotRepository;
        this.readMessageRepository = readMessageRepository;
        this.idProvider = idProvider;
    }

    public void registerChannel(ChannelName channelName, AccountId accountId) {
        if (channelRepository.existsBy(channelName)) {
            throw new ChannelNameConflictException();
        }

        ChannelId channelId = idProvider.generate();

        ChannelOwner channelOwner = new ChannelOwner(accountId);
        List<ChannelMember> members = List.of(new ChannelMember(accountId));
        Channel channel = new Channel(channelId, channelName, channelOwner, members, ChannelType.CHANNEL);
        channelRepository.add(channel);

        LastReadMessage lastReadMessage = LastReadMessage.empty(channelId, accountId);
        readMessageRepository.add(lastReadMessage);
    }

    public void updateChannel(ChannelId channelId, ChannelName newChannelName) {
        if (channelRepository.existsBy(newChannelName)) {
            throw new ChannelNameConflictException();
        }
        Channel channel = channelRepository.findById(channelId);
        Channel changeChannel = channel.changeName(newChannelName);
        channelRepository.update(changeChannel);
    }

    public void removeChannel(Channel channel) {
        channelRepository.remove(channel);
    }

    public void registerChatBot(AccountId accountId) {
        ChannelId channelId = idProvider.generate();
        ChatBot chatBot = new ChatBot(channelId, accountId);
        chatBotRepository.add(chatBot);

        LastReadMessage lastReadMessage = LastReadMessage.empty(channelId, accountId);
        readMessageRepository.add(lastReadMessage);
    }

    public void deleteOwnersChannel(AccountId accountId) {
        Channels channels = channelRepository.findOwnersChannel(accountId);
        channelRepository.removeAll(channels);
    }

    public void deleteChatBot(AccountId accountId) {
        chatBotRepository.remove(accountId);
    }
}
