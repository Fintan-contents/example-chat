package com.example.presentation.restapi.message;

import com.example.application.service.LoginAccountIdSupplier;
import com.example.application.service.message.MessagePostingService;
import com.example.application.service.message.MessageSearchService;
import com.example.domain.model.account.AccountId;
import com.example.domain.model.channel.ChannelId;
import com.example.domain.model.message.ImageData;
import com.example.domain.model.message.ImageFile;
import com.example.domain.model.message.ImageKey;
import com.example.system.nablarch.interceptor.CheckPermission;
import com.example.system.nablarch.interceptor.CheckPermission.Permission;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.upload.PartInfo;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;

@SystemRepositoryComponent
@Path("/channels/{channelId:\\d+}/files")
public class FileMessagesAction {

    private static final Set<String> ALLOW_CONTENT_TYPE = Set.of("image/jpeg", "image/png", "image/gif");

    private final MessagePostingService messagePostingService;

    private final LoginAccountIdSupplier loginAccountIdSupplier;

    private final MessageSearchService messageSearchService;

    public FileMessagesAction(MessagePostingService messagePostingService,
            LoginAccountIdSupplier loginAccountIdSupplier, MessageSearchService messageSearchService) {
        this.messagePostingService = messagePostingService;
        this.loginAccountIdSupplier = loginAccountIdSupplier;
        this.messageSearchService = messageSearchService;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public void post(HttpRequest request, ExecutionContext context) {
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));

        List<PartInfo> partInfoList = request.getPart("image");
        if (partInfoList.isEmpty()) {
            throw new HttpErrorResponse(HttpResponse.Status.BAD_REQUEST.getStatusCode());
        }

        PartInfo partInfo = partInfoList.get(0);
        if (!ALLOW_CONTENT_TYPE.contains(partInfo.getContentType())) {
            throw new HttpErrorResponse(HttpResponse.Status.BAD_REQUEST.getStatusCode());
        }

        ImageFile imageFile = new ImageFile(partInfo.getSavedFile().toPath(), partInfo.getContentType());
        AccountId accountId = loginAccountIdSupplier.supply();
        messagePostingService.postFile(channelId, accountId, imageFile);
    }

    @Path("{fileKey:.+}")
    @GET
    @CheckPermission(Permission.CHANNEL_MEMBER)
    public HttpResponse showImage(HttpRequest request) {
        ChannelId channelId = new ChannelId(Long.valueOf(request.getParam("channelId")[0]));
        ImageKey imageKey = new ImageKey(request.getParam("fileKey")[0]);
        ImageData imageData = messageSearchService.findFileBy(channelId, imageKey);

        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setContentType(imageData.contentType());
        httpResponse.write(imageData.data());
        return httpResponse;
    }
}
