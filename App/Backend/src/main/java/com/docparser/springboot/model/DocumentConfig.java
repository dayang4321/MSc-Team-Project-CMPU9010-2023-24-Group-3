package com.docparser.springboot.model;

public  class DocumentConfig {
    private  String fontType;
    private  String fontSize;
    private  String fontColor;
    private  String backgroundColor;
    private  String lineSpacing;
    private  String characterSpacing;
    private  String alignment;
    private  Boolean generateTOC;
    private  Boolean removeItalics;

    public DocumentConfig() {
    }

    public DocumentConfig(String fontType, String fontSize, String fontColor, String backgroundColor, String lineSpacing, String characterSpacing, String alignment, Boolean generateTOC, Boolean removeItalics) {
        this.fontType = fontType;
        this.fontSize = fontSize;
        this.fontColor = fontColor;
        this.backgroundColor = backgroundColor;
        this.lineSpacing = lineSpacing;
        this.characterSpacing = characterSpacing;
        this.alignment = alignment;
        this.generateTOC = generateTOC;
        this.removeItalics = removeItalics;
    }

    public String getFontType() {
        return fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(String lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public String getCharacterSpacing() {
        return characterSpacing;
    }

    public void setCharacterSpacing(String characterSpacing) {
        this.characterSpacing = characterSpacing;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public Boolean getGenerateTOC() {
        return generateTOC;
    }

    public void setGenerateTOC(Boolean generateTOC) {
        this.generateTOC = generateTOC;
    }

    public Boolean getRemoveItalics() {
        return removeItalics;
    }

    public void setRemoveItalics(Boolean removeItalics) {
        this.removeItalics = removeItalics;
    }

}




