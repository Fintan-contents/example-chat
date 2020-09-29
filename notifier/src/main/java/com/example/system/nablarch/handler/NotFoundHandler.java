package com.example.system.nablarch.handler;

import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpRequestHandler;
import nablarch.fw.web.HttpResponse;

public class NotFoundHandler implements HttpRequestHandler {

    @Override
    public HttpResponse handle(HttpRequest request, ExecutionContext context) {
        return HttpResponse.Status.NOT_FOUND.handle(request, context);
    }
}
