package com.example.presentation.restapi.channel;

import com.example.application.service.account.AccountSearchService;
import com.example.domain.model.account.Account;
import com.example.domain.model.account.Accounts;
import com.example.domain.model.channel.ChannelId;
import com.example.system.nablarch.interceptor.CheckPermission;
import com.example.system.nablarch.interceptor.CheckPermission.Permission;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@SystemRepositoryComponent
@Path("/channels/{channelId:\\d+}/invitable_accounts")
public class ChannelInvitableAccountsAction {

    private final AccountSearchService accountSearchService;

    public ChannelInvitableAccountsAction(AccountSearchService accountSearchService) {
        this.accountSearchService = accountSearchService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public List<InvitableAccountResponse> list(HttpRequest request, ExecutionContext context) {
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        Accounts accounts = accountSearchService.findInvitableAccounts(channelId);
        return accounts.asList().stream().map(InvitableAccountResponse::new).collect(Collectors.toList());
    }

    public static class InvitableAccountResponse {

        public Long id;

        public String name;

        public InvitableAccountResponse(Account account) {
            this.id = account.accountId().value();
            this.name = account.userName().value();
        }
    }
}
