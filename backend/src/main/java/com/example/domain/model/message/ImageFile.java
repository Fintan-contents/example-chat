package com.example.domain.model.message;

import java.nio.file.Files;
import java.nio.file.Path;

public class ImageFile {

    private final Path path;
    private final String contentType;

    public ImageFile(Path path, String contentType) {
        if (Files.isDirectory(path)) {
            throw new IllegalArgumentException("not path of file.");
        }
        this.path = path;
        this.contentType = contentType;
    }

    public Path path() {
        return path;
    }

    public String contentType() {
        return contentType;
    }
}
