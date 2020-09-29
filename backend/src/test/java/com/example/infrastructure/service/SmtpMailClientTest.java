package com.example.infrastructure.service;

import static org.junit.jupiter.api.Assertions.*;

import javax.mail.internet.MimeMessage;

import com.example.domain.model.account.MailAddress;
import com.example.domain.model.notification.MailSubject;
import com.example.domain.model.notification.MailContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

public class SmtpMailClientTest {

    @RegisterExtension
    GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @BeforeEach
    void initSMTPUsers() {
        greenMail.setUser("smtpuser", "secret");
    }

    @Test
    void sendMessage() throws Exception {
        SmtpMailClient sut = new SmtpMailClient("localhost", greenMail.getSmtp().getPort(), "smtpuser", "secret", "return@example.com", "from@example.com");
        MailAddress toMailAddress = new MailAddress("to@example.com");
        MailSubject subject = new MailSubject("件名");
        MailContent text = new MailContent("本文");
        sut.send(toMailAddress, subject, text);

        assertEquals(1, greenMail.getReceivedMessages().length);

        MimeMessage msg = greenMail.getReceivedMessages()[0];

        assertEquals("件名", msg.getSubject());
        assertArrayEquals(new String[] { "from@example.com" }, msg.getHeader("From"));
        assertArrayEquals(new String[] { "to@example.com" }, msg.getHeader("To"));
        assertArrayEquals(new String[] { "<return@example.com>" }, msg.getHeader("Return-Path"));
        assertEquals("件名", msg.getSubject());
        assertEquals("本文", msg.getContent());
    }
}
