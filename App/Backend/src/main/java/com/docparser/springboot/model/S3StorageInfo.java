package com.docparser.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing the storage information of a document in AWS S3.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class S3StorageInfo {
    // Variables to store various S3 document attributes
    private String documentID;
    private String url;
    private String key;
    private String versionID;

}
