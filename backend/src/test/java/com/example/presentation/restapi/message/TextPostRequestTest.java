package com.example.presentation.restapi.message;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.example.ValidationExtension;
import com.example.domain.model.message.MessageText;
import com.example.presentation.restapi.message.MessagesAction.TextPostRequest;

public class TextPostRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private TextPostRequest sut = new TextPostRequest();

    @Test
    void valid() throws Exception {
        sut.text = new MessageText("テストメッセージ");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(0, validationResult.size());
    }

    @Test
    void invalid_textがnull() throws Exception {
        sut.text = null;

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("text"));
    }
}
