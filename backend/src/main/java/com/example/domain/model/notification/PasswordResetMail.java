package com.example.domain.model.notification;

import com.example.domain.model.account.MailAddress;
import com.example.domain.model.account.TemporaryUserToken;

public class PasswordResetMail {

    private final MailAddress to;

    private final MailSubject subject;

    private final MailContent content;

    public PasswordResetMail(MailAddress to, TemporaryUserToken token, String applicationUrl) {
        this.to = to;
        subject = new MailSubject("パスワードリセット用URLのお知らせ");
        content = new MailContent(
                "24時間以内に下記のURLからパスワードリセットを完了してください。\r\n" +
                applicationUrl + "/password-reset/" + token.value()
        );
    }

    public void send(Mailer mailer) {
        mailer.send(to, subject, content);
    }
}
