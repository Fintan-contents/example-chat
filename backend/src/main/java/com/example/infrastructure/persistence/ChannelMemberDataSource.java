package com.example.infrastructure.persistence;

import com.example.domain.event.ChannelInviteEvent;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.*;
import com.example.infrastructure.persistence.entity.*;
import com.example.domain.repository.ChannelMemberRepository;
import com.example.application.event.EventPublisher;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SystemRepositoryComponent
public class ChannelMemberDataSource implements ChannelMemberRepository {

    private final EventPublisher eventPublisher;

    public ChannelMemberDataSource(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ChannelMembers findBy(ChannelId channelId) {
        ChannelOwnerEntity channelOwnerEntity = UniversalDao.findBySqlFile(ChannelOwnerEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId.value()));
        ChannelOwner owner = new ChannelOwner(new AccountId(channelOwnerEntity.getAccountId()));

        EntityList<ChannelMemberEntity> channelMemberEntities = UniversalDao.findAllBySqlFile(ChannelMemberEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId.value()));
        List<ChannelMember> members = channelMemberEntities.stream()
                .filter(m -> !m.getAccountId().equals(owner.accountId().value()))
                .map(m -> new ChannelMember(new AccountId(m.getAccountId())))
                .collect(Collectors.toList());

        return new ChannelMembers(owner, members);
    }

    @Override
    public void add(ChannelId channelId, ChannelMember channelMember) {
        ChannelMemberEntity entity = new ChannelMemberEntity();
        entity.setChannelId(channelId.value());
        entity.setAccountId(channelMember.accountId().value());
        UniversalDao.insert(entity);

        ChannelEntity channelEntity = UniversalDao.findById(ChannelEntity.class, channelId.value());
        ChatBotEntity chatBotEntity = UniversalDao.findById(ChatBotEntity.class, channelMember.accountId().value());
        ChannelInviteEvent inviteEvent = new ChannelInviteEvent(channelId,
                new ChannelName(channelEntity.getChannelName()),
                new ChatBot(new ChannelId(chatBotEntity.getChannelId()), channelMember.accountId()));
        eventPublisher.publish(inviteEvent);
    }

    @Override
    public void remove(ChannelId channelId, ChannelMember channelMember) {
        ChannelMemberEntity entity = new ChannelMemberEntity();
        entity.setChannelId(channelId.value());
        entity.setAccountId(channelMember.accountId().value());
        UniversalDao.delete(entity);
    }

}
