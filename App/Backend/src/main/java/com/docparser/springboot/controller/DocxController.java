package com.docparser.springboot.controller;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.model.DocumentResponse;
import com.docparser.springboot.model.S3StorageInfo;
import com.docparser.springboot.service.DocumentParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

// Enable cross-origin requests for the controller
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class DocxController {
    // Logger for this class
    Logger logger = LoggerFactory.getLogger(DocxController.class);
    private static final String AUTHORISATION = "Authorization";

    private final DocumentParser documentParser;

    // Endpoint for uploading a file
    @PostMapping("/uploadFile")
    public ResponseEntity<S3StorageInfo> fileUploading(@RequestParam("file") MultipartFile file,
                                                       HttpServletRequest request) throws IOException {
        // Extract the authorization token from the request header
        Optional<String> token = Optional.of(request.getHeader(AUTHORISATION));
        // Call the document parser service to upload the file, passing the trimmed
        // token
        S3StorageInfo storageInfo = documentParser.uploadFile(file, token.get().substring(7));

        return ResponseEntity.ok(storageInfo);
    }

    // Endpoint for modifying a file
    @PostMapping("/modifyFile")
    public ResponseEntity<DocumentResponse> modifyDocument(@RequestParam("filename") String fileName,
                                                           @RequestParam("docID") String docID, @RequestParam("versionID") String versionID,
                                                           @RequestBody DocumentConfig documentConfig, HttpServletRequest request) throws IOException {
        // Extract the authorization token from the request header
        Optional<String> token = Optional.of(request.getHeader(AUTHORISATION));
        // Call the document parser service to modify the file, passing necessary
        // parameters
        DocumentResponse storageInfo = documentParser.modifyFile(fileName, docID, versionID, documentConfig,
                token.get().substring(7));

        return ResponseEntity.ok(storageInfo);
    }

    // Endpoint for retrieving a document's information
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentInfo(@PathVariable String id, HttpServletRequest request) {
        // Extract the authorization token from the request header
        Optional<String> token = Optional.of(request.getHeader(AUTHORISATION));
        // Fetch the document info using the document parser service
        return ResponseEntity.ok(documentParser.fetchDocument(id, token.get().substring(7)));
    }

    // Endpoint for deleting all stored documents
    @DeleteMapping
    public ResponseEntity<Object> batchDeleteDocumentsStored() {
        // Log the deletion operation
        logger.info("Deleting all stored documents");
        // Call the document parser service to delete all stored documents
        documentParser.deleteStoredDocuments();

        return ResponseEntity.ok("success");
    }
}
