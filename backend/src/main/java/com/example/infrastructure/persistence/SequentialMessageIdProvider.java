package com.example.infrastructure.persistence;

import com.example.infrastructure.persistence.entity.MessageIdSequence;
import com.example.application.service.message.MessageIdProvider;
import com.example.domain.model.message.MessageId;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.dao.UniversalDao;

@SystemRepositoryComponent
public class SequentialMessageIdProvider implements MessageIdProvider {

    @Override
    public MessageId generate() {
        MessageIdSequence messageIdSequence = UniversalDao.findBySqlFile(MessageIdSequence.class, "NEXT_MESSAGE_ID",
                new Object[0]);
        return new MessageId(messageIdSequence.getMessageId());
    }
}
