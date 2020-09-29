package com.example.system.nablarch.handler;

import com.example.domain.model.Payload;

public interface Notifier {

    void notify(Payload payload);
}