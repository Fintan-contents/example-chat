package com.example.application.service.message;

import com.example.domain.model.message.MessageId;

public interface MessageIdProvider {

    MessageId generate();
}
