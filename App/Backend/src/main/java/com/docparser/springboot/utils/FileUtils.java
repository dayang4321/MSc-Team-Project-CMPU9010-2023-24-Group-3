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

    // Converts a MultipartFile to a File
    public static File convertMultiPartToFile(MultipartFile file) throws IOException {
        // Create a File object from the original file name of the MultipartFile
        File convFile = new File(Objects.requireNonNull(getMultipartFileName(file)));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    // Generates a file name from a MultipartFile by removing special characters
    public static String generateFileName(MultipartFile multiPart) {
        /*
         * Calls the helper method to remove special characters and return the cleaned
         * file name
         */
        return removeSpecialCharacters(getMultipartFileName(multiPart));
    }

    // Generates a file name from a File object
    public static String generateFileName(File file) {

        return file.getName();
    }

    public static String getMultipartFileName(MultipartFile file) {
        if (file.getOriginalFilename() != null && !file.getOriginalFilename().equals("")) {
            return file.getOriginalFilename();
        }
        return file.getName();
    }

    // Helper method to remove special characters from a string
    public static String removeSpecialCharacters(String fileName) {
        // Define a regular expression to match special characters
        String regex = "[^a-zA-Z0-9\\.\\s\\-_]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileName);
        // Replace all special characters with an empty string
        return matcher.replaceAll("");
    }
}
