package com.docparser.springboot.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FileUtils {
    public  static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public  static String generateFileName(MultipartFile multiPart) {
        return  removeSpecialCharacters(multiPart.getOriginalFilename());
    }
    public static String generateFileName(File file) {
        return  file.getName();
    }

    private static String removeSpecialCharacters(String fileName) {
        // Define a regular expression to match special characters
        String regex = "[^a-zA-Z0-9\\.\\s\\-_]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileName);
        return matcher.replaceAll("");
    }
}
