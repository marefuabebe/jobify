package com.webapp.jobportal.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloadUtil {

    public Resource getFileAsResourse(String downloadDir, String fileName) throws IOException {
        Path path = Paths.get(downloadDir);
        Path filePath = path.resolve(fileName);

        if (Files.exists(filePath)) {
            return new UrlResource(filePath.toUri());
        }

        return null;
    }
}
