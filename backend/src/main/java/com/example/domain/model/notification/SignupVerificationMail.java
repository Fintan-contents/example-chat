package com.example.domain.model.notification;

import com.example.domain.model.account.MailAddress;
import com.example.domain.model.account.TemporaryUserToken;

public class SignupVerificationMail {

    private final MailAddress to;

    private final MailSubject subject;

    private final MailContent content;

    public SignupVerificationMail(MailAddress to, TemporaryUserToken token, String applicationUrl) {
        this.to = to;
        subject = new MailSubject("アカウント登録用URLのお知らせ");
        content = new MailContent(
                "24時間以内に下記のURLからアカウント登録を完了してください。\r\n" +
                applicationUrl + "/signup-complete/" + token.value()
        );
    }

    public void send(Mailer mailer) {
        mailer.send(to, subject, content);
    }
}
