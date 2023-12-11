package com.docparser.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    // Unique identifier for the document.
    private String documentID;
    // A key associated with the document, possibly for retrieval or identification.
    private String documentKey;

    // Configuration details specific to the document.
    private DocumentConfig documentConfig;

    // A map to hold different versions of the document.
    /*
     * The key is a string (possibly version identifier) and the value is a
     * DocumentVersion object.
     */
    private Map<String, DocumentVersion> versions;

}
