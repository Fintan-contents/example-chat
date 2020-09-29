package com.example.presentation.restapi.account;

import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.account.AccountRegistrationService;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.account.RawPassword;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.fw.ExecutionContext;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@SystemRepositoryComponent
@Path("/settings/password")
public class PasswordSettingAction {

    private final AccountRegistrationService accountRegistrationService;

    private final LoginAccountIdSupplier loginAccountIdSupplier;

    public PasswordSettingAction(AccountRegistrationService accountRegistrationService,
            LoginAccountIdSupplier loginAccountIdSupplier) {
        this.accountRegistrationService = accountRegistrationService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void change(ExecutionContext context, PasswordChangeRequest requestBody) {

        ValidatorUtil.validate(requestBody);

        AccountId accountId = loginAccountIdSupplier.supply();

        accountRegistrationService.changePassword(accountId, requestBody.password, requestBody.newPassword);
    }

    public static class PasswordChangeRequest {

        @NotNull
        public RawPassword password;

        @NotNull
        public RawPassword newPassword;
    }
}
