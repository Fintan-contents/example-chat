package com.example.domain.model.channel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ChannelNameTest {

    @ParameterizedTest
    @ValueSource(strings = { "testchannel" })
    @MethodSource("justMaxlength")
    void validWith(String value) {
        assertDoesNotThrow(() -> new ChannelName(value));
    }

    @Test
    void invalidWithNull() {
        assertThrows(NullPointerException.class, () -> new ChannelName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " " })
    @MethodSource("overMaxlength")
    void invalidWith(String value) {
        assertThrows(IllegalArgumentException.class, () -> new ChannelName(value));
    }

    static Stream<String> justMaxlength() {
        String text = IntStream.range(0, 50).mapToObj(i -> "x").collect(Collectors.joining());
        String surrogatePair = IntStream.range(0, 49).mapToObj(i -> "x").collect(Collectors.joining()) + "𩸽";
        return Stream.of(text, surrogatePair);
    }

    static Stream<String> overMaxlength() {
        String text = IntStream.range(0, 51).mapToObj(i -> "x").collect(Collectors.joining());
        return Stream.of(text);
    }
}
