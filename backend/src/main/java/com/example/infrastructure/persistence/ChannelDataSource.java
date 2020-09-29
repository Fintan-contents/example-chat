package com.example.infrastructure.persistence;

import com.example.application.event.EventPublisher;
import com.example.application.service.LoginAccountIdSupplier;
import com.example.domain.event.ChannelDeleteEvent;
import com.example.domain.event.ChannelJoinEvent;
import com.example.domain.event.ChannelNameChangeEvent;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.account.UserName;
import com.example.domain.model.channel.*;
import com.example.domain.repository.ChannelRepository;
import com.example.infrastructure.persistence.entity.*;
import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SystemRepositoryComponent
public class ChannelDataSource implements ChannelRepository {

    private final EventPublisher eventPublisher;

    private final LoginAccountIdSupplier loginAccountIdSupplier;

    public ChannelDataSource(EventPublisher eventPublisher, LoginAccountIdSupplier loginAccountIdSupplier) {
        this.eventPublisher = eventPublisher;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
    }

    @Override
    public void add(Channel channel) {
        ChannelEntity channelEntity = new ChannelEntity();
        channelEntity.setChannelId(channel.id().value());
        channelEntity.setChannelName(channel.name().value());
        channelEntity.setType(channel.type().name());
        UniversalDao.insert(channelEntity);

        ChannelOwnerEntity channelOwnerEntity = new ChannelOwnerEntity();
        channelOwnerEntity.setChannelId(channel.id().value());
        channelOwnerEntity.setAccountId(channel.owner().accountId().value());
        UniversalDao.insert(channelOwnerEntity);

        channel.members().forEach(m -> {
            ChannelMemberEntity channelMemberEntity = new ChannelMemberEntity();
            channelMemberEntity.setChannelId(channel.id().value());
            channelMemberEntity.setAccountId(m.accountId().value());
            UniversalDao.insert(channelMemberEntity);
        });

        ChannelJoinEvent event = new ChannelJoinEvent(channel);
        eventPublisher.publish(event);
    }

    @Override
    public void update(Channel channel) {
        Channel oldChannel = findById(channel.id());

        ChannelEntity entity = new ChannelEntity();
        entity.setChannelId(oldChannel.id().value());
        entity.setChannelName(channel.name().value());
        entity.setType(oldChannel.type().name());
        UniversalDao.update(entity);

        ChannelNameChangeEvent event = new ChannelNameChangeEvent(oldChannel, channel);
        eventPublisher.publish(event);
    }

    @Override
    public void remove(Channel channel) {
        ChannelId channelId = channel.id();
        EntityList<ChannelMemberEntity> channelMemberEntities = UniversalDao.findAllBySqlFile(ChannelMemberEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId.value()));
        UniversalDao.batchDelete(channelMemberEntities);

        EntityList<ChannelOwnerEntity> channelOwnerEntities = UniversalDao.findAllBySqlFile(ChannelOwnerEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId.value()));
        UniversalDao.batchDelete(channelOwnerEntities);

        EntityList<ReadMessageEntity> readMessageEntities = UniversalDao.findAllBySqlFile(ReadMessageEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId.value()));
        UniversalDao.batchDelete(readMessageEntities);

        EntityList<MessageEntity> messageEntities = UniversalDao.findAllBySqlFile(MessageEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId.value()));
        UniversalDao.batchDelete(messageEntities);

        ChannelEntity channelEntity = UniversalDao.findById(ChannelEntity.class, channelId.value());
        UniversalDao.delete(channelEntity);

        ChannelDeleteEvent channelDeleteEvent = createChannelDeleteEvent(channelEntity, channelOwnerEntities, channelMemberEntities);
        eventPublisher.publish(channelDeleteEvent);
    }

    @Override
    public void removeAll(Channels channels) {
        channels.asList().forEach(this::remove);
    }

    @Override
    public Channel findById(ChannelId channelId) {
        ChannelEntity channelEntity = UniversalDao.findById(ChannelEntity.class, channelId.value());

        ChannelOwnerEntity channelOwnerEntity = UniversalDao.findBySqlFile(ChannelOwnerEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId.value()));
        EntityList<ChannelMemberEntity> channelMemberEntities = UniversalDao.findAllBySqlFile(ChannelMemberEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId.value()));
        List<ChannelMember> channelMembers = channelMemberEntities.stream()
                .map(dto -> new ChannelMember(new AccountId(dto.getAccountId()))).collect(Collectors.toList());

        return new Channel(new ChannelId(channelEntity.getChannelId()), new ChannelName(channelEntity.getChannelName()),
                new ChannelOwner(new AccountId(channelOwnerEntity.getAccountId())), channelMembers,
                ChannelType.valueOf(channelEntity.getType()));
    }

    @Override
    public Channels findMembersChannel(AccountId accountId) {
        EntityList<ChannelEntity> channelEntities = UniversalDao.findAllBySqlFile(ChannelEntity.class, "SELECT_ALL",
                Map.of("accountId", accountId.value()));
        List<Channel> channels = channelEntities.stream().map(c -> findById(new ChannelId(c.getChannelId())))
                .collect(Collectors.toList());
        return new Channels(channels);
    }

    @Override
    public Channels findOwnersChannel(AccountId accountId) {
        EntityList<ChannelEntity> channelEntities = UniversalDao.findAllBySqlFile(ChannelEntity.class,
                "SELECT_OWNERS_CHANNEL_ID", Map.of("accountId", accountId.value()));
        List<Channel> channels = channelEntities.stream().map(c -> findById(new ChannelId(c.getChannelId())))
                .collect(Collectors.toList());
        return new Channels(channels);
    }

    @Override
    public boolean existsById(ChannelId channelId) {
        return UniversalDao.exists(ChannelEntity.class, "SELECT_FOR_EXISTS_BY_ID",
                Map.of("channelId", channelId.value()));
    }

    @Override
    public boolean existsBy(ChannelName channelName) {
        return UniversalDao.exists(ChannelEntity.class, "SELECT_FOR_EXISTS_BY_NAME",
                Map.of("channelName", channelName.value()));
    }

    private ChannelDeleteEvent createChannelDeleteEvent(ChannelEntity channelEntity,
            List<ChannelOwnerEntity> ownerEntities, List<ChannelMemberEntity> memberEntities) {
        AccountId accountId = loginAccountIdSupplier.supply();
        AccountEntity accountEntity = UniversalDao.findById(AccountEntity.class, accountId.value());
        UserName deleteUserName = new UserName(accountEntity.getUserName());

        ChannelOwner owner = new ChannelOwner(new AccountId(ownerEntities.get(0).getAccountId()));
        List<ChannelMember> members = memberEntities.stream()
                .filter(m -> !m.getAccountId().equals(owner.accountId().value()))
                .map(m -> new ChannelMember(new AccountId(m.getAccountId())))
                .collect(Collectors.toList());

        Map<String, Object[]> condition = Map.of("accountIds",
                memberEntities.stream().map(ChannelMemberEntity::getAccountId).toArray());
        EntityList<ChatBotEntity> chatBotEntities = UniversalDao.findAllBySqlFile(ChatBotEntity.class,
                "SELECT_BY_ACCOUNT_IDS", condition);
        List<ChatBot> chatBots = chatBotEntities.stream()
                .map(c -> new ChatBot(new ChannelId(c.getChannelId()), new AccountId(c.getAccountId())))
                .collect(Collectors.toList());

        return new ChannelDeleteEvent(deleteUserName, new ChannelId(channelEntity.getChannelId()),
                new ChannelName(channelEntity.getChannelName()), new ChannelMembers(owner, members), chatBots);
    }
}
