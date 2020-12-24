package com.example.presentation.restapi.channel;

import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.channel.ChannelMemberRegistrationService;
import com.example.application.service.channel.ChatBotSearchService;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChatBot;
import com.example.presentation.restapi.RestApiException;
import com.example.system.nablarch.interceptor.CheckPermission;
import com.example.system.nablarch.interceptor.CheckPermission.Permission;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;

@SystemRepositoryComponent
@Path("/channels/{channelId:\\d+}/members/me")
public class JoiningChannelAction {

    private final ChannelMemberRegistrationService channelMemberRegistrationService;

    private final ChatBotSearchService chatBotSearchService;

    private final LoginAccountIdSupplier loginAccountIdSupplier;

    public JoiningChannelAction(ChannelMemberRegistrationService channelMemberRegistrationService,
            ChatBotSearchService chatBotSearchService,
            LoginAccountIdSupplier loginAccountIdSupplier) {
        this.channelMemberRegistrationService = channelMemberRegistrationService;
        this.chatBotSearchService = chatBotSearchService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
    }

    @DELETE
    @CheckPermission(Permission.CHANNEL_MEMBER_NOT_OWNER)
    public void leave(HttpRequest request) {
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        AccountId accountId = loginAccountIdSupplier.supply();

        ChatBot chatBot = chatBotSearchService.findBy(accountId);
        if (chatBot.channelId().value().equals(channelId.value())) {
            throw new RestApiException(HttpResponse.Status.FORBIDDEN, "access.denied");
        }

        channelMemberRegistrationService.delete(channelId, accountId);
    }
}
