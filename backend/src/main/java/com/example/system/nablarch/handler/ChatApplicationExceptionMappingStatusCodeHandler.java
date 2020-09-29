package com.example.system.nablarch.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.example.application.ChannelNameConflictException;
import com.example.application.ChatApplicationException;
import com.example.application.InvalidPasswordResetTokenException;
import com.example.application.InvalidSignupTokenException;
import com.example.application.MismatchedCredentialException;
import com.example.application.UserNameConflictException;
import com.example.application.service.authentication.AuthorizationException;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.web.HttpErrorResponse;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.HttpResponse.Status;

public class ChatApplicationExceptionMappingStatusCodeHandler implements Handler<Object, Object> {

    private static final Logger LOGGER = LoggerManager.get(ChatApplicationExceptionMappingStatusCodeHandler.class);

    private final Map<Class<? extends ChatApplicationException>, HttpResponse.Status> map = new LinkedHashMap<>();

    public ChatApplicationExceptionMappingStatusCodeHandler() {
        map.put(MismatchedCredentialException.class, HttpResponse.Status.UNAUTHORIZED);
        map.put(AuthorizationException.class, HttpResponse.Status.FORBIDDEN);
        map.put(InvalidSignupTokenException.class, HttpResponse.Status.NOT_FOUND);
        map.put(InvalidPasswordResetTokenException.class, HttpResponse.Status.NOT_FOUND);
        map.put(ChannelNameConflictException.class, HttpResponse.Status.CONFLICT);
        map.put(UserNameConflictException.class, HttpResponse.Status.CONFLICT);
    }

    @Override
    public Object handle(Object data, ExecutionContext context) {
        try {
            return context.handleNext(data);
        } catch (ChatApplicationException e) {
            for (Entry<Class<? extends ChatApplicationException>, Status> entry : map.entrySet()) {
                if (entry.getKey().isInstance(e)) {
                    LOGGER.logInfo(e.getClass().getName() + " is thrown");
                    throw new HttpErrorResponse(entry.getValue().getStatusCode(), e);
                }
            }
            if (LOGGER.isWarnEnabled()) {
                LOGGER.logWarn(e.getClass().getName() + " is not mapped to HTTP status code");
            }
            throw e;
        }
    }
}
