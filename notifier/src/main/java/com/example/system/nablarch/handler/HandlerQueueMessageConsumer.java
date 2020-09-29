package com.example.system.nablarch.handler;

import java.util.ArrayList;
import java.util.List;

import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.HandlerQueueManager;

public class HandlerQueueMessageConsumer extends HandlerQueueManager<HandlerQueueMessageConsumer>
        implements MessageConsumer {

    private List<Handler> handlerQueue = new ArrayList<>();

    @Override
    public List<Handler> getHandlerQueue() {
        return handlerQueue;
    }

    @Override
    public void consume(Message message) {
        ExecutionContext context = new ExecutionContext();
        context.setHandlerQueue(handlerQueue).handleNext(message);
    }
}
