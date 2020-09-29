package com.example.domain.model;

import java.util.List;

public class Destinations {

    private final List<AccountId> value;

    public Destinations(List<AccountId> value) {
        this.value = value;
    }

    public List<AccountId> asList() {
        return value;
    }
}
