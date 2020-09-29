package com.example.presentation.restapi.system;

import com.example.application.service.authentication.WebSocketAuthenticationService;
import com.example.domain.model.authentication.WebSocketAuthenticationToken;
import nablarch.core.repository.di.config.externalize.annotation.ConfigValue;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@SystemRepositoryComponent
@Path("/systeminfo/notification")
public class SystemInfoNotificationAction {

    private final String websocketUri;
    private final WebSocketAuthenticationService webSocketAuthenticationService;

    public SystemInfoNotificationAction(@ConfigValue("${websocket.uri}") String websocketUri,
            WebSocketAuthenticationService webSocketAuthenticationService) {
        this.websocketUri = websocketUri;
        this.webSocketAuthenticationService = webSocketAuthenticationService;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public NotificationResponse buildWebSocketUri() {
        WebSocketAuthenticationToken webSocketAuthenticationToken = webSocketAuthenticationService.prepareToken();
        String uri = websocketUri + "?token=" + webSocketAuthenticationToken.value();
        return new NotificationResponse(uri);
    }

    public static class NotificationResponse {

        public String websocketUri;

        public NotificationResponse(String websocketUri) {
            this.websocketUri = websocketUri;
        }
    }
}
