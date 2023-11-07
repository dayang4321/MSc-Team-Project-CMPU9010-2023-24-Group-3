package com.docparser.springboot.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Component
public class FileUtils {
    public File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public String generateFileName(MultipartFile multiPart) {
        return  multiPart.getOriginalFilename();
    }
    public String generateFileName(File file) {
        return  file.getName();
    }
}
