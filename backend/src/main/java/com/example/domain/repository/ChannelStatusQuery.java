package com.example.domain.repository;

import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.Channels;

public interface ChannelStatusQuery {

    Channels find(AccountId accountId);
}
