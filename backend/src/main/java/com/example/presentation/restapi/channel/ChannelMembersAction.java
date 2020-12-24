package com.example.presentation.restapi.channel;

import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.account.AccountSearchService;
import com.example.application.service.channel.ChannelMemberRegistrationService;
import com.example.application.service.channel.ChannelMemberSearchService;
import com.example.application.service.channel.ChatBotSearchService;
import com.example.domain.model.account.Account;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelMembers;
import com.example.domain.model.channel.ChatBot;
import com.example.presentation.restapi.RestApiException;
import com.example.system.nablarch.interceptor.CheckPermission;
import com.example.system.nablarch.interceptor.CheckPermission.Permission;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@SystemRepositoryComponent
@Path("/channels/{channelId:\\d+}/members")
public class ChannelMembersAction {

    private final AccountSearchService accountSearchService;

    private final ChannelMemberSearchService channelMemberSearchService;

    private final ChannelMemberRegistrationService channelMemberRegistrationService;

    private final ChatBotSearchService chatBotSearchService;

    private final LoginAccountIdSupplier loginAccountIdSupplier;

    public ChannelMembersAction(AccountSearchService accountSearchService,
            ChannelMemberSearchService channelMemberSearchService,
            ChannelMemberRegistrationService channelMemberRegistrationService,
            ChatBotSearchService chatBotSearchService,
            LoginAccountIdSupplier loginAccountIdSupplier) {
        this.accountSearchService = accountSearchService;
        this.channelMemberSearchService = channelMemberSearchService;
        this.channelMemberRegistrationService = channelMemberRegistrationService;
        this.chatBotSearchService = chatBotSearchService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public List<ChannelMemberResponse> list(HttpRequest request, ExecutionContext context) {
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        ChannelMembers channelMembers = channelMemberSearchService.findBy(channelId);
        return createResponse(channelMembers);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public void invite(HttpRequest request, ExecutionContext context, ChannelInviteRequest requestBody) {
        ValidatorUtil.validate(requestBody);
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        AccountId accountId = requestBody.accountId;

        ChatBot chatBot = chatBotSearchService.findBy(loginAccountIdSupplier.supply());
        if (chatBot.channelId().value().equals(channelId.value())) {
            throw new RestApiException(HttpResponse.Status.FORBIDDEN, "access.denied");
        }

        channelMemberRegistrationService.insert(channelId, accountId);
    }

    private List<ChannelMemberResponse> createResponse(ChannelMembers channelMembers) {
        List<ChannelMemberResponse> channelMemberResponses = new ArrayList<>();

        Account ownerAccount = accountSearchService.findById(channelMembers.owner().accountId());
        channelMemberResponses.add(new ChannelMemberResponse(ownerAccount, true));

        channelMembers.members().forEach(m -> {
            Account memberAccount = accountSearchService.findById(m.accountId());
            channelMemberResponses.add(new ChannelMemberResponse(memberAccount, false));
        });
        return channelMemberResponses;
    }

    public static class ChannelMemberResponse {

        public Long id;

        public String name;

        public boolean isOwner;

        public ChannelMemberResponse(Account account, boolean isOwner) {
            this.id = account.accountId().value();
            this.name = account.userName().value();
            this.isOwner = isOwner;
        }
    }

    public static class ChannelInviteRequest {

        @NotNull
        public AccountId accountId;
    }
}
