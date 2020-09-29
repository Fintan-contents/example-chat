package com.example.presentation.restapi.account;

import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.authentication.AuthenticationService;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.account.SecretString;
import com.example.domain.model.authentication.AuthenticationCode;
import com.example.domain.model.account.TwoFactorAuthenticationSettingStatus;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.fw.ExecutionContext;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@SystemRepositoryComponent
@Path("/settings/2fa")
public class TwoFactorAuthenticationSettingAction {

    private final AuthenticationService authenticationService;

    private final LoginAccountIdSupplier loginAccountIdSupplier;

    public TwoFactorAuthenticationSettingAction(AuthenticationService authenticationService,
            LoginAccountIdSupplier loginAccountIdSupplier) {
        this.authenticationService = authenticationService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StatusResponse status(ExecutionContext context) {
        AccountId accountId = loginAccountIdSupplier.supply();

        TwoFactorAuthenticationSettingStatus setting = authenticationService.get2FASettingStatus(accountId);

        return new StatusResponse(setting);
    }

    @Path("/enable")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public EnableResponse enable(ExecutionContext context) {
        SecretString secretString = authenticationService.generate2FASecretString(context);
        return new EnableResponse(secretString);
    }

    @Path("/enable/verify")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void verifyAuthenticationCode(ExecutionContext context, VerifyAuthenticationCodeRequest requestBody) {
        ValidatorUtil.validate(requestBody);
        AccountId accountId = loginAccountIdSupplier.supply();
        authenticationService.enable2FA(context, accountId, requestBody.code);
    }

    @Path("/disable")
    @POST
    public void disable(ExecutionContext context) {
        AccountId accountId = loginAccountIdSupplier.supply();
        authenticationService.disable2FA(accountId);
    }

    public static class StatusResponse {

        public String status;

        public StatusResponse(TwoFactorAuthenticationSettingStatus twoFactorAuthenticationSettingStatus) {
            this.status = twoFactorAuthenticationSettingStatus.name();
        }
    }

    public static class EnableResponse {

        public String secretString;

        public EnableResponse(SecretString secretString) {
            this.secretString = secretString.value();
        }
    }

    public static class VerifyAuthenticationCodeRequest {

        @NotNull
        public AuthenticationCode code;
    }
}
