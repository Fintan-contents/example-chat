package com.example.application;

import nablarch.core.repository.di.config.externalize.annotation.ConfigValue;
import nablarch.core.repository.di.config.externalize.annotation.SystemRepositoryComponent;

import java.net.MalformedURLException;
import java.net.URL;

@SystemRepositoryComponent
public class ApplicationConfig {

    private final String applicationExternalUrl;

    private final String backendBaseUrl;

    public ApplicationConfig(@ConfigValue("${application.external.url}") String applicationExternalUrl,
                             @ConfigValue("${backend.base.url}") String backendBaseUrl) {
        // URLオブジェクトにするとバックエンドアプリ基準のURLになるため、不正なURLでないかだけチェックする
        try {
            new URL(applicationExternalUrl);
            new URL(backendBaseUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        this.applicationExternalUrl = applicationExternalUrl;
        this.backendBaseUrl = backendBaseUrl;
    }

    public String applicationExternalUrl() {
        return applicationExternalUrl;
    }

    public String backendBaseUrl() {
        return backendBaseUrl;
    }
}
