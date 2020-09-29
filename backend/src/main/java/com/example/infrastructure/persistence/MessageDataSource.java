package com.example.infrastructure.persistence;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.*;
import com.example.infrastructure.persistence.entity.MessageEntity;
import com.example.domain.repository.MessageRepository;
import nablarch.core.repository.di.config.externalize.annotation.ConfigValue;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SystemRepositoryComponent
public class MessageDataSource implements MessageRepository {

    private final Long limit;

    public MessageDataSource(@ConfigValue("${paging.per.count}") Long limit) {
        this.limit = limit;
    }

    @Override
    public Messages findBy(ChannelId channelId, MessageId nextMessageId) {
        // REST APIにおけるページネーション
        // 本アプリケーションにおけるページネーションはチャットを過去に遡って辿ることである。
        // そのため、ダイレクトにページ番号を指定することはしない。
        // 上記に加えて性能面でも優れているため、ある特定のレコードを基準として検索を行うカーソルベースのページネーションを採用する。
        Map<String, Object> conditions = Map.of("channelId", channelId.value(), "messageId", nextMessageId.value(),
                "limit", limit + 1);
        EntityList<MessageEntity> messageEntities = UniversalDao.findAllBySqlFile(MessageEntity.class,
                "SELECT_BY_CHANNEL_ID_AND_MESSAGE_ID", conditions);

        // 次に取得するメッセージ一覧の基準となるメッセージIDを取得しておく
        MessageId newNextMessageId;
        if (messageEntities.size() == limit + 1) {
            MessageEntity nextMessageEntity = messageEntities.get(messageEntities.size() - 1);
            newNextMessageId = new MessageId(nextMessageEntity.getMessageId());
        } else {
            // メッセージを全て読み込んだ場合
            newNextMessageId = null;
        }

        List<Message> messages = messageEntities.stream()
                .map(m -> new Message(new MessageId(m.getMessageId()), new ChannelId(m.getChannelId()),
                        new AccountId(m.getAccountId()), new MessageText(m.getText()),
                        MessageType.valueOf(m.getType()), new SendDateTime(m.getSendDateTime().toLocalDateTime())))
                .limit(limit)
                .collect(Collectors.toList());
        return new Messages(messages, newNextMessageId);
    }

    @Override
    public MessageId findLatestMessageId(ChannelId channelId) {
        MessageEntity entity = UniversalDao.findBySqlFile(MessageEntity.class, "SELECT_LATEST_MESSAGE_ID",
                Map.of("channelId", channelId.value()));
        return new MessageId(entity.getMessageId());
    }

    @Override
    public void add(Message message) {
        MessageEntity entity = new MessageEntity();
        entity.setMessageId(message.messageId().value());
        entity.setChannelId(message.channelId().value());
        entity.setAccountId(message.accountId().value());
        entity.setText(message.text().value());
        entity.setType(message.type().name());
        entity.setSendDateTime(Timestamp.valueOf(message.sendDateTime().value()));
        UniversalDao.insert(entity);
    }

    @Override
    public void remove(ChannelId channelId, AccountId accountId) {
        EntityList<MessageEntity> messageEntities = UniversalDao.findAllBySqlFile(MessageEntity.class,
                "SELECT_BY_CHANNEL_ID_AND_ACCOUNT_ID", Map.of("channelId", channelId.value(), "accountId", accountId.value()));
        UniversalDao.batchDelete(messageEntities);
    }
}
