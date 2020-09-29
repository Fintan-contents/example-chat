package com.example.infrastructure.service;

import com.example.domain.model.account.MailAddress;
import com.example.domain.model.notification.MailSubject;
import com.example.domain.model.notification.MailContent;
import com.example.domain.model.notification.Mailer;
import nablarch.core.repository.di.config.externalize.annotation.ConfigValue;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * メール送信はJavaMail（https://javaee.github.io/javamail/）を用いて行う。
 *
 * ただしメール送信を行う各処理でJavaMailのAPIをそのまま使用すると煩雑なプログラムになってしまうため、
 * 共通で使用できるように、本クラスを用意する。
 *
 * 本クラスでは、メールヘッダへ任意の値を設定できないようにする。
 * これはThird-Party Mail Relay（第三者中継）と呼ばれる攻撃への対策である。
 */
@SystemRepositoryComponent
public class SmtpMailClient implements Mailer {

    private final String host;

    private final String user;

    private final String password;

    private final Session session;

    private final InternetAddress adminMailAddress;

    public SmtpMailClient(@ConfigValue("${mail.smtp.host}") String host, @ConfigValue("${mail.smtp.port}") int port,
            @ConfigValue("${mail.smtp.user}") String user, @ConfigValue("${mail.smtp.password}") String password,
            @ConfigValue("${mail.returnPath}") String returnPath,
            @ConfigValue("${mail.from.address}") String fromMailAddress) throws Exception {
        this.host = host;
        this.user = user;
        this.password = password;
        this.adminMailAddress = new InternetAddress(fromMailAddress);

        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        // mail.smtp.fromに設定したアドレスでReturn-Path(エンベロープFrom)ヘッダが設定される。
        // https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html
        // Return-Path(エンベロープFrom)については以下の記事を参照。
        // https://sendgrid.kke.co.jp/blog/?p=9949
        props.put("mail.smtp.from", returnPath);

        this.session = Session.getDefaultInstance(props);
    }

    @Override
    public void send(MailAddress toMailAddress, MailSubject subject, MailContent text) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(adminMailAddress);
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toMailAddress.value()));
            msg.setSubject(subject.value());
            msg.setText(text.value(), "UTF-8");
            msg.setHeader("Content-Transfer-Encoding", "base64");

            try (Transport transport = session.getTransport()) {
                transport.connect(host, user, password);
                transport.sendMessage(msg, msg.getAllRecipients());
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
