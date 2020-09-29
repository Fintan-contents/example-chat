package com.example.presentation.restapi.account;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.account.AccountSearchService;
import com.example.domain.model.account.Account;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

@SystemRepositoryComponent
@Path("/accounts/me")
public class MyAccountAction {

    private final LoginAccountIdSupplier loginAccountIdSupplier;
    private final AccountSearchService accountSearchService;

    public MyAccountAction(LoginAccountIdSupplier loginAccountIdSupplier, AccountSearchService accountSearchService) {
        this.loginAccountIdSupplier = loginAccountIdSupplier;
        this.accountSearchService = accountSearchService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AccountResponse get() {

        Account account = accountSearchService.findById(loginAccountIdSupplier.supply());

        return new AccountResponse(account);
    }

    public static class AccountResponse {

        public Long accountId;
        public String userName;
        public String mailAddress;

        public AccountResponse(Account account) {
            this.accountId = account.accountId().value();
            this.userName = account.userName().value();
            this.mailAddress = account.mailAddress().value();
        }
    }
}
