package com.docparser.springboot.utils;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void testConvertMultiPartToFile() throws IOException {
        // Create a MockMultipartFile for testing
        MockMultipartFile multipartFile = new MockMultipartFile(
                "testFile.txt", "Hello, World!".getBytes());

        // Convert MockMultipartFile to File
        File convertedFile = FileUtils.convertMultiPartToFile(multipartFile);

        // Check if the converted file exists
        assertTrue(convertedFile.exists());

        // Cleanup: Delete the created file
        assertTrue(convertedFile.delete());
    }

    @Test
    void testGenerateFileNameFromMultiPart() {
        // Create a MockMultipartFile for testing
        MockMultipartFile multipartFile = new MockMultipartFile(
                "test file.txt", "Hello, World!".getBytes());

        // Generate a file name from the MockMultipartFile
        String fileName = FileUtils.generateFileName(multipartFile);

        // Check if the generated file name is as expected (no special characters)
        assertEquals("test file.txt", fileName);
    }

    @Test
    void testGenerateFileNameFromFile() {
        // Create a temporary test file
        File testFile = new File("test_file.txt");

        // Generate a file name from the File
        String fileName = FileUtils.generateFileName(testFile);

        // Check if the generated file name is as expected (no special characters)
        assertEquals("test_file.txt", fileName);
    }

    @Test
    void testRemoveSpecialCharacters() {
        // Test a file name with special characters
        String fileNameWithSpecialChars = "file@name!%^.txt";

        // Remove special characters from the file name
        String sanitizedFileName = FileUtils.removeSpecialCharacters(fileNameWithSpecialChars);

        // Check if special characters are removed
        assertEquals("filename.txt", sanitizedFileName);
    }
}
