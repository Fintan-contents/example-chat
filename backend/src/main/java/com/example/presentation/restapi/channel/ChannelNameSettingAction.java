package com.example.presentation.restapi.channel;

import com.example.application.service.channel.ChannelRegistrationService;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.channel.ChannelName;
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
@Path("/channels/{channelId:\\d+}/settings/name")
public class ChannelNameSettingAction {

    private final ChannelRegistrationService channelRegistrationService;

    public ChannelNameSettingAction(ChannelRegistrationService channelRegistrationService) {
        this.channelRegistrationService = channelRegistrationService;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public void change(HttpRequest request, ExecutionContext context, ChannelPutRequest requestBody) {
        ValidatorUtil.validate(requestBody);

        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        ChannelName channelName = requestBody.channelName;
        channelRegistrationService.updateChannel(channelId, channelName);
    }

    public static class ChannelPutRequest {

        @NotNull
        public ChannelName channelName;
    }
}
