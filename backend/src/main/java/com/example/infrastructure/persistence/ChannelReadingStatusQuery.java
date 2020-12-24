package com.example.infrastructure.persistence;

import com.example.application.service.channel.ChannelReadingStatusQueryService;
import com.example.domain.model.account.AccountId;
import com.example.application.service.channel.ChannelReadingStatusDto;
import nablarch.common.dao.EntityList;
import nablarch.common.dao.UniversalDao;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SystemRepositoryComponent
public class ChannelReadingStatusQuery implements ChannelReadingStatusQueryService {

    @Override
    public List<ChannelReadingStatusDto> find(AccountId accountId) {
        EntityList<ChannelReadingStatusDto> select = UniversalDao.findAllBySqlFile(ChannelReadingStatusDto.class,
                "SELECT_BY_ACCOUNTID", Map.of("accountId", accountId.value()));
        return select.stream().collect(Collectors.toUnmodifiableList());
    }
}
