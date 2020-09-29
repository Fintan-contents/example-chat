package com.example.presentation.websocket;

import javax.websocket.server.ServerEndpointConfig;

import nablarch.core.repository.SystemRepository;

public class SystemRepositoryWebSocketConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return SystemRepository.get(endpointClass.getName());
    }
}
