package com.docparser.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {

    private String documentID;
    private String documentKey;
    private Instant createdDate;
    private Instant expirationTime;

}
