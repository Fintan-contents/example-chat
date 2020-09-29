package com.example.presentation.restapi.message;

import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.message.ReadMessageRegistrationService;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.MessageId;
import com.example.system.nablarch.interceptor.CheckPermission;
import com.example.system.nablarch.interceptor.CheckPermission.Permission;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@SystemRepositoryComponent
@Path("/read/{channelId:\\d+}")
public class MessageReadAction {

    private final ReadMessageRegistrationService readMessageRegistrationService;

    private final LoginAccountIdSupplier loginAccountIdSupplier;

    public MessageReadAction(ReadMessageRegistrationService readMessageRegistrationService,
            LoginAccountIdSupplier loginAccountIdSupplier) {
        this.readMessageRegistrationService = readMessageRegistrationService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public void read(HttpRequest request, ExecutionContext context, ReadRequest requestBody) {
        ValidatorUtil.validate(requestBody);

        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        AccountId accountId = loginAccountIdSupplier.supply();
        readMessageRegistrationService.updateReadMessage(requestBody.messageId, channelId, accountId);
    }

    public static class ReadRequest {

        @NotNull
        public MessageId messageId;
    }
}
