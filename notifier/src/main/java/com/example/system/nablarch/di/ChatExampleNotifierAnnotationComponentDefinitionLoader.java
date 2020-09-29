package com.example.system.nablarch.di;

import nablarch.core.repository.di.config.externalize.AnnotationComponentDefinitionLoader;

public class ChatExampleNotifierAnnotationComponentDefinitionLoader extends AnnotationComponentDefinitionLoader {

    @Override
    protected String getBasePackage() {
        return "com.example";
    }
}
