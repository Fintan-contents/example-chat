package com.example.presentation.restapi.authentication;

import com.example.domain.model.authentication.AuthenticationStatus;
import com.example.domain.model.account.MailAddress;
import com.example.domain.model.account.RawPassword;
import com.example.application.service.authentication.AuthenticationService;
import com.example.presentation.restapi.LoginContext;
import nablarch.common.web.session.SessionUtil;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.fw.ExecutionContext;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@SystemRepositoryComponent
@Path("/")
public class LoginAction {

    private final AuthenticationService authenticationService;

    public LoginAction(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public LoginResponse login(ExecutionContext context, LoginRequest requestBody) {
        ValidatorUtil.validate(requestBody);

        LoginContext loginContext = new LoginContext(context);
        AuthenticationStatus status = authenticationService.authenticate(loginContext, requestBody.mailAddress,
                requestBody.password);
        LoginResponse response = new LoginResponse();
        response.status = status.name();

        return response;
    }

    @Path("/logout")
    @POST
    public void logout(ExecutionContext context) {
        SessionUtil.invalidate(context);
    }

    static class LoginRequest {

        @NotNull
        public MailAddress mailAddress;

        @NotNull
        public RawPassword password;
    }

    static class LoginResponse {

        public String status;
    }
}
