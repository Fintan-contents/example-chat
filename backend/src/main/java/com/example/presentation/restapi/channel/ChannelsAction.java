package com.example.presentation.restapi.channel;

import com.example.application.service.channel.*;
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

    private final ChannelRegistrationService channelRegistrationService;

    private final LoginAccountIdSupplier loginAccountIdSupplier;

    private final ChannelReadingStatusQueryService channelReadingStatusQueryService;

    public ChannelsAction(ChannelRegistrationService channelRegistrationService,
            LoginAccountIdSupplier loginAccountIdSupplier,
          ChannelReadingStatusQueryService channelReadingStatusQueryService) {
        this.channelRegistrationService = channelRegistrationService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
        this.channelReadingStatusQueryService = channelReadingStatusQueryService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ChannelResponse> list(HttpRequest request) {
        AccountId accountId = loginAccountIdSupplier.supply();

        List<ChannelReadingStatusDto> list = channelReadingStatusQueryService.find(accountId);

        return list.stream().map(ChannelResponse::new).collect(Collectors.toList());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void create(HttpRequest request, ExecutionContext context, ChannelPostRequest requestBody) {
        ValidatorUtil.validate(requestBody);

        ChannelName channelName = requestBody.channelName;
        AccountId accountId = loginAccountIdSupplier.supply();

        channelRegistrationService.registerChannel(channelName, accountId);
    }

    public static class ChannelPostRequest {

        @NotNull
        public ChannelName channelName;
    }

    public static class ChannelResponse {

        public Long id;

        public String name;

        public String type;

        public boolean allRead;

        public ChannelResponse(ChannelReadingStatusDto dto) {
            this.id = dto.getChannelId();
            this.name = dto.getChannelName();
            this.type = dto.getType();
            this.allRead = dto.getLastReadMessageId().equals(dto.getLatestMessageId());
        }
    }
}
