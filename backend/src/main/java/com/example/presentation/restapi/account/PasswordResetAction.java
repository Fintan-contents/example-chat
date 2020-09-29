package com.example.presentation.restapi.account;

import com.example.domain.model.account.MailAddress;
import com.example.application.service.account.PasswordResetService;
import com.example.domain.model.account.RawPassword;
import com.example.domain.model.account.TemporaryUserToken;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.fw.web.HttpRequest;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@SystemRepositoryComponent
@Path("/reset_password")
public class PasswordResetAction {

    private final PasswordResetService passwordResetService;

    public PasswordResetAction(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void resetPassword(PasswordResetRequest requestBody) {
        ValidatorUtil.validate(requestBody);
        passwordResetService.issueToken(requestBody.mailAddress);
    }

    @Path("/verify")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void verifyToken(HttpRequest request, VerifyTokenRequest requestBody) {
        ValidatorUtil.validate(requestBody);
        passwordResetService.verifyToken(requestBody.token);
    }

    @Path("/new")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void newPassword(HttpRequest request, NewPasswordRequest requestBody) {
        ValidatorUtil.validate(requestBody);
        passwordResetService.resetPassword(requestBody.token, requestBody.newPassword);
    }

    static class PasswordResetRequest {

        @NotNull
        public MailAddress mailAddress;
    }

    static class VerifyTokenRequest {

        @NotNull
        public TemporaryUserToken token;
    }

    static class NewPasswordRequest {

        @NotNull
        public TemporaryUserToken token;

        @NotNull
        public RawPassword newPassword;
    }
}
