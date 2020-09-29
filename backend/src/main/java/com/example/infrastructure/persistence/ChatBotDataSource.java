package com.example.infrastructure.persistence;

import com.example.domain.event.ChatBotCreateEvent;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelType;
import com.example.domain.model.channel.ChatBot;
import com.example.infrastructure.persistence.entity.*;
import com.example.domain.repository.ChatBotRepository;
import com.example.application.event.EventPublisher;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;

import java.util.Arrays;
import java.util.Map;

@SystemRepositoryComponent
public class ChatBotDataSource implements ChatBotRepository {

    private final EventPublisher eventPublisher;

    public ChatBotDataSource(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void add(ChatBot chatBot) {
        ChannelEntity channelEntity = new ChannelEntity();
        channelEntity.setChannelId(chatBot.channelId().value());
        channelEntity.setChannelName(ChatBot.SystemUser.NAME.value());
        channelEntity.setType(ChannelType.SYSTEM.name());
        UniversalDao.insert(channelEntity);

        ChatBotEntity chatBotEntity = new ChatBotEntity();
        chatBotEntity.setChannelId(chatBot.channelId().value());
        chatBotEntity.setAccountId(chatBot.accountId().value());
        UniversalDao.insert(chatBotEntity);

        ChannelOwnerEntity channelOwnerEntity = new ChannelOwnerEntity();
        channelOwnerEntity.setChannelId(chatBot.channelId().value());
        channelOwnerEntity.setAccountId(ChatBot.SystemUser.ID.value());
        UniversalDao.insert(channelOwnerEntity);

        Arrays.asList(ChatBot.SystemUser.ID.value(), chatBot.accountId().value()).forEach(id -> {
            ChannelMemberEntity channelMemberEntity = new ChannelMemberEntity();
            channelMemberEntity.setChannelId(chatBot.channelId().value());
            channelMemberEntity.setAccountId(id);
            UniversalDao.insert(channelMemberEntity);
        });

        ChatBotCreateEvent event = new ChatBotCreateEvent(chatBot);
        eventPublisher.publish(event);
    }

    @Override
    public void remove(AccountId accountId) {
        ChatBotEntity chatBotEntity = UniversalDao.findBySqlFile(ChatBotEntity.class, "SELECT_BY_ACCOUNT_ID",
                Map.of("accountId", accountId.value()));

        UniversalDao.delete(chatBotEntity);

        deleteMessage(chatBotEntity.getChannelId());
        deleteChannelMember(chatBotEntity.getChannelId());
        deleteChanelOwner(chatBotEntity.getChannelId());
        deleteChannel(chatBotEntity.getChannelId());
    }

    @Override
    public ChatBot findBy(AccountId accountId) {
        ChatBotEntity chatBotEntity = UniversalDao.findById(ChatBotEntity.class, accountId.value());
        return new ChatBot(new ChannelId(chatBotEntity.getChannelId()), accountId);
    }

    private void deleteChannel(Long channelId) {
        ChannelEntity entity = new ChannelEntity();
        entity.setChannelId(channelId);
        UniversalDao.delete(entity);
    }

    private void deleteChanelOwner(Long channelId) {
        EntityList<ChannelOwnerEntity> channelOwnerEntities = UniversalDao.findAllBySqlFile(ChannelOwnerEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId));
        UniversalDao.batchDelete(channelOwnerEntities);
    }

    private void deleteChannelMember(Long channelId) {
        EntityList<ChannelMemberEntity> channelMemberEntities = UniversalDao.findAllBySqlFile(ChannelMemberEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId));
        UniversalDao.batchDelete(channelMemberEntities);
    }

    private void deleteMessage(Long channelId) {
        EntityList<ReadMessageEntity> readMessageEntities = UniversalDao.findAllBySqlFile(ReadMessageEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId));
        UniversalDao.batchDelete(readMessageEntities);

        EntityList<MessageEntity> messageEntities = UniversalDao.findAllBySqlFile(MessageEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId));
        UniversalDao.batchDelete(messageEntities);
    }
}
