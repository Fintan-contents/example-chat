package com.example.application.service.authentication;

import com.example.domain.model.authentication.WebSocketAuthenticationToken;

public interface WebSocketAuthenticationTokenProvider {

    WebSocketAuthenticationToken provide();
}
