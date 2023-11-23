package com.docparser.springboot.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private String documentID;

    private String documentKey;

    private DocumentConfig documentConfig;

    private HashMap<String, DocumentVersion> versions;



}
