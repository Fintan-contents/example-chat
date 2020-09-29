package com.example.domain.model.message;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

public class SendDateTime {

    private final LocalDateTime value;

    public SendDateTime(LocalDateTime value) {
        this.value = Objects.requireNonNull(value);
    }

    public LocalDateTime value() {
        return value;
    }

    public static SendDateTime now() {
        // バックエンドではjava.time.LocalDateTimeで日時を扱う。
        // java.time.LocalDateTimeはローカルタイムを表すためタイムゾーンやオフセットを持たないが、
        // 本アプリケーションでは原則UTCで日時を扱うためjava.time.LocalDateTimeもUTCのローカルタイムとして扱う。
        //
        // UTCのローカルタイムを取得するには次のように行う。
        // LocalDateTime dateTime = LocalDateTime.now(Clock.systemUTC());
        // タイムゾーンの変換はフロントエンドでの表示時で行われるが、それ以外では発生しない。
        // そのためバックエンドにおけるタイムゾーンの変換については特に定めない。
        return new SendDateTime(LocalDateTime.now(Clock.systemUTC()));
    }
}
