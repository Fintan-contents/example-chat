package com.example.presentation.restapi.authentication;

import com.example.domain.model.authentication.AuthenticationCode;
import com.example.application.service.authentication.AuthenticationService;
import com.example.presentation.restapi.LoginContext;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.fw.ExecutionContext;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@SystemRepositoryComponent
@Path("/2fa")
public class TwoFactorAuthenticationAction {

    private final AuthenticationService authenticationService;

    public TwoFactorAuthenticationAction(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void authenticate(ExecutionContext executionContext, VerifyRequest requestBody) {
        LoginContext loginContext = new LoginContext(executionContext);
        authenticationService.verifyAuthenticationCode(loginContext, requestBody.code);
    }

    public static class VerifyRequest {

        @NotNull
        public AuthenticationCode code;
    }

}
