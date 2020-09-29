package com.example.domain.model.account;

import java.util.concurrent.TimeUnit;

public class ExpireTime {

    private final int duration;

    private final TimeUnit timeUnit;

    public ExpireTime(int duration, TimeUnit timeUnit) {
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    public long toSecond() {
        return timeUnit.toSeconds(duration);
    }
}
