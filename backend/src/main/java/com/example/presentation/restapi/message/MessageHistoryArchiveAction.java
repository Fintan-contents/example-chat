package com.example.presentation.restapi.message;

import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.message.MessageHistoryService;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.MessageHistoryData;
import com.example.domain.model.message.MessageHistoryKey;
import com.example.system.nablarch.interceptor.CheckPermission;
import com.example.system.nablarch.interceptor.CheckPermission.Permission;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@SystemRepositoryComponent
@Path("/channels/{channelId:\\d+}/history/archive")
public class MessageHistoryArchiveAction {

    private final MessageHistoryService messageHistoryService;

    private final LoginAccountIdSupplier loginAccountIdSupplier;

    public MessageHistoryArchiveAction(MessageHistoryService messageHistoryService,
            LoginAccountIdSupplier loginAccountIdSupplier) {
        this.messageHistoryService = messageHistoryService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public ExportingResponse export(HttpRequest request, ExecutionContext context) {
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        AccountId accountId = loginAccountIdSupplier.supply();

        MessageHistoryKey messageHistoryKey = messageHistoryService.export(accountId, channelId);

        return new ExportingResponse(messageHistoryKey.value());
    }

    @Path("{fileKey:.+}")
    @GET
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public HttpResponse download(HttpRequest request, ExecutionContext context) {
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        MessageHistoryKey messageHistoryKey = new MessageHistoryKey(request.getParam("fileKey")[0]);
        MessageHistoryData messageHistoryData = messageHistoryService.findFileBy(channelId, messageHistoryKey);

        // ファイルデータへのシリアライズをアクション内で行うため戻り値をHttpResponseにする。
        // この場合はHttpResponseに直接コンテンツタイプを指定する。
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setContentType(messageHistoryData.contentType());
        httpResponse.write(messageHistoryData.data());
        return httpResponse;
    }

    public static class ExportingResponse {

        public String fileKey;

        public ExportingResponse(String fileKey) {
            this.fileKey = fileKey;
        }
    }
}
