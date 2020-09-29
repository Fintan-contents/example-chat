package com.example.presentation.restapi.message;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.example.ValidationExtension;
import com.example.domain.model.message.MessageId;
import com.example.presentation.restapi.message.MessageReadAction.ReadRequest;

public class ReadRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private ReadRequest sut = new ReadRequest();

    @Test
    void valid() throws Exception {
        sut.messageId = new MessageId(123L);

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(0, validationResult.size());
    }

    @Test
    void invalid_messageIdがnull() throws Exception {
        sut.messageId = null;

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("messageId"));
    }
}
