package com.example.application.service.channel;

import com.example.domain.model.account.Accounts;
import com.example.domain.model.channel.ChannelId;

public interface InvitableAccountQuery {

    Accounts findBy(ChannelId channelId);

}
