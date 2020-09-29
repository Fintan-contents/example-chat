package com.example.presentation.restapi.channel;

import com.example.application.service.channel.ChannelRegistrationService;
import com.example.application.service.channel.ChannelSearchService;

import com.example.domain.model.channel.Channel;
import com.example.domain.model.channel.ChannelId;
import com.example.system.nablarch.interceptor.CheckPermission;
import com.example.system.nablarch.interceptor.CheckPermission.Permission;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/channels/{channelId:\\d+}")
@SystemRepositoryComponent
public class ChannelAction {

    private final ChannelSearchService channelSearchService;

    private final ChannelRegistrationService channelRegistrationService;

    public ChannelAction(ChannelSearchService channelSearchService,
            ChannelRegistrationService channelRegistrationService) {
        this.channelSearchService = channelSearchService;
        this.channelRegistrationService = channelRegistrationService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public ChannelResponse get(HttpRequest request, ExecutionContext context) {
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        Channel channel = channelSearchService.findBy(channelId);

        return new ChannelResponse(channel, false);
    }

    @DELETE
    @CheckPermission(Permission.CHANNEL_OWNER)
    public void delete(HttpRequest request) {
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        Channel removeChannel = channelSearchService.findBy(channelId);
        channelRegistrationService.removeChannel(removeChannel);
    }

    public static class ChannelResponse {

        public Long id;

        public String name;

        public Long ownerId;

        public String type;

        public boolean allRead;

        public ChannelResponse(Channel channel, boolean allRead) {
            this.id = channel.id().value();
            this.name = channel.name().value();
            this.ownerId = channel.owner().accountId().value();
            this.type = channel.type().name();
            this.allRead = allRead;
        }
    }
}
