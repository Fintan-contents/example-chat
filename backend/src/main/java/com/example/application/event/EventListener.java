package com.example.application.event;

import com.example.domain.event.Event;

public interface EventListener<T extends Event> {

    void handle(T event);
}
