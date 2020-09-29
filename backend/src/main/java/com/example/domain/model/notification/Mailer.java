package com.example.domain.model.notification;

import com.example.domain.model.account.MailAddress;

public interface Mailer {

    void send(MailAddress toMailAddress, MailSubject subject, MailContent text);
}
