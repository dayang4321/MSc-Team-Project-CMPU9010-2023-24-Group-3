package com.docparser.springboot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public  class DocumentConfig {
    private String fontType;
    private String fontSize;
    private String fontColor;
    private String backgroundColor;
    private String lineSpacing;
    private String characterSpacing;
    private String alignment;
    private Boolean generateTOC;
    private Boolean removeItalics;
    private Boolean paragraphSplitting;
    private Boolean headerGeneration;

}



