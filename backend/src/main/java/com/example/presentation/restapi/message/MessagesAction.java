package com.example.presentation.restapi.message;

import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.account.AccountSearchService;
import com.example.application.service.message.MessagePostingService;
import com.example.application.service.message.MessageSearchService;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.account.Accounts;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.Message;
import com.example.domain.model.message.MessageId;
import com.example.domain.model.message.MessageText;
import com.example.domain.model.message.Messages;
import com.example.system.nablarch.interceptor.CheckPermission;
import com.example.system.nablarch.interceptor.CheckPermission.Permission;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.core.validation.ee.ValidatorUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SystemRepositoryComponent
@Path("/channels/{channelId:\\d+}/messages")
public class MessagesAction {

    private final MessageSearchService messageSearchService;

    private final MessagePostingService messagePostingService;

    private final LoginAccountIdSupplier loginAccountIdSupplier;

    private final AccountSearchService accountSearchService;

    public MessagesAction(MessageSearchService messageSearchService, MessagePostingService messagePostingService,
            LoginAccountIdSupplier loginAccountIdSupplier, AccountSearchService accountSearchService) {
        this.messageSearchService = messageSearchService;
        this.messagePostingService = messagePostingService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
        this.accountSearchService = accountSearchService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public MessagesResponse list(HttpRequest request, ExecutionContext context) {
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        MessageId nextMessageId = request.getParam("nextMessageId") != null
                ? new MessageId(Long.valueOf(request.getParam("nextMessageId")[0]))
                : new MessageId(Long.MAX_VALUE);
        Messages messages = messageSearchService.findBy(channelId, nextMessageId);

        AccountId[] accountIds = messages.messages().stream().map(Message::accountId).distinct()
                .toArray(AccountId[]::new);
        Accounts accounts = accountSearchService.findBy(accountIds);
        Map<Long, String> accountMap = accounts.asList().stream()
                .collect(Collectors.toMap(a -> a.accountId().value(), a -> a.userName().value()));

        return new MessagesResponse(messages, accountMap);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public void post(HttpRequest request, ExecutionContext context, TextPostRequest requestBody) {
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));

        ValidatorUtil.validate(requestBody);

        AccountId accountId = loginAccountIdSupplier.supply();

        messagePostingService.postText(channelId, accountId, requestBody.text);
    }

    public static class TextPostRequest {

        @NotNull
        public MessageText text;
    }

    public static class MessagesResponse {

        public List<MessageResponse> messages;

        public Long nextMessageId;

        public MessagesResponse(Messages messages, Map<Long, String> accountMap) {
            this.messages = messages.messages().stream().map(message -> new MessageResponse(message, accountMap))
                    .collect(Collectors.toList());
            this.nextMessageId = messages.nextMessageId() != null ? messages.nextMessageId().value() : null;
        }
    }

    public static class MessageResponse {

        public Long messageId;

        public Long channelId;

        public Long accountId;

        public String text;

        public String type;

        public String userName;

        public LocalDateTime sendDateTime;

        public MessageResponse(Message message, Map<Long, String> accountMap) {
            this.messageId = message.messageId().value();
            this.channelId = message.channelId().value();
            this.accountId = message.accountId().value();
            this.text = message.text().value();
            this.type = message.type().name();
            this.userName = accountMap.get(message.accountId().value());
            this.sendDateTime = message.sendDateTime().value();
        }
    }
}
