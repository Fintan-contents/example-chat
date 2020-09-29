package com.example.domain.model.message;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MessageHistoryFile {

    private static final Logger LOGGER = LoggerManager.get(MessageHistoryFile.class);

    private final Path path;

    public MessageHistoryFile(Path path) {
        if (Files.isDirectory(path)) {
            throw new IllegalArgumentException("not path of file.");
        }
        this.path = path;
    }

    public Path path() {
        return path;
    }

    public void tryDelete() {
        try {
            Files.delete(path);
        } catch (IOException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.logWarn(String.format("Failed to delete file. path=[%s]", path), e);
            }
        }
    }
}
