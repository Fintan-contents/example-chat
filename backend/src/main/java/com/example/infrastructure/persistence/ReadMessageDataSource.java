package com.example.infrastructure.persistence;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.LastReadMessage;
import com.example.domain.model.message.MessageId;
import com.example.infrastructure.persistence.entity.ReadMessageEntity;
import com.example.domain.repository.ReadMessageRepository;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.dao.NoDataException;
import nablarch.common.dao.UniversalDao;

import java.util.Map;

@SystemRepositoryComponent
public class ReadMessageDataSource implements ReadMessageRepository {

    @Override
    public LastReadMessage findBy(ChannelId channelId, AccountId accountId) {
        Map<String, Object> condition = Map.of("channelId", channelId.value(), "accountId", accountId.value());
        try {
            ReadMessageEntity entity = UniversalDao.findBySqlFile(ReadMessageEntity.class, "SELECT_READ_MESSAGE",
                    condition);
            return new LastReadMessage(new MessageId(entity.getLastReadMessageId()),
                    new ChannelId(entity.getChannelId()), new AccountId(entity.getAccountId()));
        } catch (NoDataException e) {
            return null;
        }
    }

    @Override
    public void add(LastReadMessage lastReadMessage) {
        UniversalDao.insert(toEntity(lastReadMessage));
    }

    @Override
    public void update(LastReadMessage lastReadMessage) {
        UniversalDao.update(toEntity(lastReadMessage));
    }

    @Override
    public void remove(ChannelId channelId, AccountId accountId) {
        ReadMessageEntity entity = new ReadMessageEntity();
        entity.setAccountId(accountId.value());
        entity.setChannelId(channelId.value());
        UniversalDao.delete(entity);
    }

    private ReadMessageEntity toEntity(LastReadMessage lastReadMessage) {
        ReadMessageEntity entity = new ReadMessageEntity();
        entity.setAccountId(lastReadMessage.accountId().value());
        entity.setChannelId(lastReadMessage.channelId().value());
        entity.setLastReadMessageId(lastReadMessage.messageId().value());
        return entity;
    }
}
