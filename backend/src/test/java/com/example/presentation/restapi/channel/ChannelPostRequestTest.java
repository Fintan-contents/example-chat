package com.example.presentation.restapi.channel;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.example.ValidationExtension;
import com.example.domain.model.channel.ChannelName;
import com.example.presentation.restapi.channel.ChannelsAction.ChannelPostRequest;

public class ChannelPostRequestTest {

    @RegisterExtension
    static ValidationExtension validation = new ValidationExtension();

    private ChannelPostRequest sut = new ChannelPostRequest();

    @Test
    void valid() throws Exception {
        sut.channelName = new ChannelName("testchannel");

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(0, validationResult.size());
    }

    @Test
    void invalid_channelNameがnull() throws Exception {
        sut.channelName = null;

        Map<String, Set<Class<? extends Annotation>>> validationResult = validation.validate(sut);
        assertEquals(1, validationResult.size());
        assertEquals(Set.of(NotNull.class), validationResult.get("channelName"));
    }
}
