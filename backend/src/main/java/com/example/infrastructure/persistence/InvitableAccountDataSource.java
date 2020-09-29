package com.example.infrastructure.persistence;

import com.example.domain.model.account.*;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChatBot;
import com.example.infrastructure.persistence.entity.*;
import com.example.application.service.channel.InvitableAccountQuery;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SystemRepositoryComponent
public class InvitableAccountDataSource implements InvitableAccountQuery {

    @Override
    public Accounts findBy(ChannelId channelId) {
        EntityList<ChannelMemberEntity> channelMemberEntities = UniversalDao.findAllBySqlFile(ChannelMemberEntity.class,
                "SELECT_BY_CHANNEL_ID", Map.of("channelId", channelId.value()));

        List<Long> exclusionAccountIds = new ArrayList<>();
        exclusionAccountIds.add(ChatBot.SystemUser.ID.value());
        for (ChannelMemberEntity entity : channelMemberEntities) {
            exclusionAccountIds.add(entity.getAccountId());
        }

        Map<String, Object> condition = Map.of("memberIds", exclusionAccountIds.toArray());
        EntityList<AccountEntity> accountEntities = UniversalDao.findAllBySqlFile(AccountEntity.class,
                "SELECT_INVITE_ACCOUNT", condition);
        List<Account> accountList = accountEntities.stream().map(a -> new Account(new AccountId(a.getAccountId()),
                new UserName(a.getUserName()), new MailAddress(a.getMailAddress()))).collect(Collectors.toList());
        return new Accounts(accountList);
    }
}
