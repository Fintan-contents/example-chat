package com.example.application.event;

import com.example.domain.model.account.AccountId;
import com.example.domain.event.ChannelJoinEvent;
import com.example.domain.model.message.MessageText;
import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.message.MessagePostingService;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelJoinMessageNotifier implements EventListener<ChannelJoinEvent> {

    private final MessagePostingService messagePostingService;
    private final LoginAccountIdSupplier loginAccountIdSupplier;

    public ChannelJoinMessageNotifier(MessagePostingService messagePostingService,
            LoginAccountIdSupplier loginAccountIdSupplier) {
        this.messagePostingService = messagePostingService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
    }

    @Override
    public void handle(ChannelJoinEvent event) {
        AccountId accountId = loginAccountIdSupplier.supply();
        String message = String.format("#%sに参加しました。", event.channel().name().value());
        MessageText messageText = new MessageText(message);
        messagePostingService.postText(event.channel().id(), accountId, messageText);
    }
}
