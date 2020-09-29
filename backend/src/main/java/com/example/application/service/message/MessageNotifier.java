package com.example.application.service.message;

import com.example.domain.model.message.Message;

public interface MessageNotifier {

    void notify(Message message);
}
