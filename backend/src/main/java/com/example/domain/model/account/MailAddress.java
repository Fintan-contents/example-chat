package com.example.domain.model.account;

import java.util.Objects;
import java.util.regex.Pattern;

public class MailAddress {

    private final String value;

    private static final Pattern FORMAT_PATTERN = Pattern.compile("^.+@.+$");

    private final static int MAX_LENGTH = 50;

    public MailAddress(String value) {
        Objects.requireNonNull(value);
        validateFormat(value);
        validateLength(value);

        this.value = value;
    }

    public String value() {
        return value;
    }

    private static void validateFormat(String value) {
        // ここでは簡易な形式チェックしかしていないため、実際には要件に合わせて適切な形式チェックを実装する必要がある
        if (!FORMAT_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(String.format("Invalid email address format. value=[%s]", value));
        }
    }

    private static void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("MailAddress length is too long. value=[%s]", value));
        }
    }
}
