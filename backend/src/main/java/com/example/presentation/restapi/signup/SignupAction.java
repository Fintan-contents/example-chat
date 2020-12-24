package com.example.presentation.restapi.signup;

import com.example.application.service.account.AccountRegistrationService;
import com.example.domain.model.account.MailAddress;
import com.example.domain.model.account.RawPassword;
import com.example.domain.model.account.TemporaryUserToken;
import com.example.domain.model.account.UserName;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.validation.ee.ValidatorUtil;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@SystemRepositoryComponent
@Path("/signup")
public class SignupAction {

    private final AccountRegistrationService accountRegistrationService;

    public SignupAction(AccountRegistrationService accountRegistrationService) {
        this.accountRegistrationService = accountRegistrationService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void signup(SignupRequest requestBody) {
        ValidatorUtil.validate(requestBody);

        accountRegistrationService.registerTemporaryAccount(requestBody.userName, requestBody.mailAddress,
                requestBody.password);
    }

    @Path("/verify")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void verify(VerificationRequest requestBody) {
        ValidatorUtil.validate(requestBody);

        accountRegistrationService.verifyAccount(requestBody.userToken);
    }

    public static class SignupRequest {

        @NotNull
        public UserName userName;

        @NotNull
        public MailAddress mailAddress;

        @NotNull
        public RawPassword password;
    }

    public static class VerificationRequest {

        @NotNull
        public TemporaryUserToken userToken;
    }
}
