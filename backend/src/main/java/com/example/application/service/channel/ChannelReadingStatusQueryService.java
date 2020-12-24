package com.example.application.service.channel;

import com.example.domain.model.account.AccountId;

import java.util.List;

public interface ChannelReadingStatusQueryService {

    List<ChannelReadingStatusDto> find(AccountId accountId);

}
