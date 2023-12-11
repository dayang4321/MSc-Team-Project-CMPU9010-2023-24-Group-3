package com.docparser.springboot.model;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDocumentResponse {

    // Unique identifier for the document.
    private String documentID;
    // A key associated with the document, possibly for retrieval or identification.
    private String documentKey;

    private String version;

    private Instant createdDate;

}
