package com.example.infrastructure.persistence;

import com.example.infrastructure.persistence.entity.ChannelIdSequence;
import com.example.domain.model.channel.ChannelId;
import com.example.application.service.channel.ChannelIdProvider;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.common.dao.UniversalDao;

@SystemRepositoryComponent
public class SequentialChannelIdProvider implements ChannelIdProvider {

    @Override
    public ChannelId generate() {
        ChannelIdSequence channelIdSequence = UniversalDao.findBySqlFile(ChannelIdSequence.class, "NEXT_CHANNEL_ID",
                new Object[0]);
        return new ChannelId(channelIdSequence.getChannelId());
    }
}
