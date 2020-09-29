package com.example.application.event;

import com.example.domain.model.account.AccountId;
import com.example.domain.event.ChannelNameChangeEvent;
import com.example.domain.model.message.MessageText;
import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.message.MessagePostingService;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
public class ChannelNameChangeMessageNotifier implements EventListener<ChannelNameChangeEvent> {

    private final MessagePostingService messagePostingService;
    private final LoginAccountIdSupplier loginAccountIdSupplier;

    public ChannelNameChangeMessageNotifier(MessagePostingService messagePostingService,
            LoginAccountIdSupplier loginAccountIdSupplier) {
        this.messagePostingService = messagePostingService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
    }

    @Override
    public void handle(ChannelNameChangeEvent event) {
        AccountId accountId = loginAccountIdSupplier.supply();
        String message = String.format("チャンネルの名前を「%s」から「%s」に変更しました。", event.oldChannel().name().value(),
                event.newChannel().name().value());
        MessageText messageText = new MessageText(message);
        messagePostingService.postText(event.newChannel().id(), accountId, messageText);
    }
}
