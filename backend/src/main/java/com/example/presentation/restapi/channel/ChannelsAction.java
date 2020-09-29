package com.example.presentation.restapi.channel;

import com.example.application.service.channel.ChannelRegistrationService;
import com.example.application.service.channel.ChannelSearchService;
import com.example.application.service.channel.ChatBotSearchService;
import com.example.application.service.LoginAccountIdSupplier;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.*;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@SystemRepositoryComponent
@Path("/channels")
public class ChannelsAction {

    private final ChannelSearchService channelSearchService;

    private final ChannelRegistrationService channelRegistrationService;

    private final ChatBotSearchService chatBotSearchService;

    private final LoginAccountIdSupplier loginAccountIdSupplier;

    public ChannelsAction(ChannelSearchService channelSearchService,
            ChannelRegistrationService channelRegistrationService, ChatBotSearchService chatBotSearchService,
            LoginAccountIdSupplier loginAccountIdSupplier) {
        this.channelSearchService = channelSearchService;
        this.channelRegistrationService = channelRegistrationService;
        this.chatBotSearchService = chatBotSearchService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ChannelResponse> list(HttpRequest request, ExecutionContext context) {
        AccountId accountId = loginAccountIdSupplier.supply();
        Channels channels = channelSearchService.findAll(accountId);

        ChatBot chatBot = chatBotSearchService.findBy(accountId);
        Channel chatBotChannel = new Channel(chatBot.channelId(), new ChannelName(ChatBot.SystemUser.NAME.value()),
                new ChannelOwner(ChatBot.SystemUser.ID),
                List.of(new ChannelMember(ChatBot.SystemUser.ID), new ChannelMember(accountId)), ChannelType.SYSTEM);

        List<ChannelResponse> response = channels.asList().stream().map(c -> createChannelResponse(c, accountId))
                .collect(Collectors.toList());

        boolean isReadAllChatBotMessages = channelSearchService.isReadAllMessages(chatBot.channelId(), accountId);
        response.add(new ChannelResponse(chatBotChannel, isReadAllChatBotMessages));
        return response;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void create(HttpRequest request, ExecutionContext context, ChannelPostRequest requestBody) {
        ValidatorUtil.validate(requestBody);

        ChannelName channelName = requestBody.channelName;
        AccountId accountId = loginAccountIdSupplier.supply();

        channelRegistrationService.registerChannel(channelName, accountId);
    }

    private ChannelResponse createChannelResponse(Channel channel, AccountId accountId) {
        boolean allRead = channelSearchService.isReadAllMessages(channel.id(), accountId);
        return new ChannelResponse(channel, allRead);
    }

    public static class ChannelPostRequest {

        @NotNull
        public ChannelName channelName;
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
