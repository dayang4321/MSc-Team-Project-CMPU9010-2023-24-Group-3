package com.docparser.springboot.model;

public  class FormattingConfig {
    private final String fontType;
    private final String fontSize;
    private final String fontColor;
    private final String backgroundColor;
    private final String lineSpacing;
    private final String characterSpacing;
    private final String alignment;
    private final Boolean generateTOC;
    private final Boolean removeItalics;

    public FormattingConfig(String fontType, String fontSize, String fontColor, String lineSpacing, String characterSpacing, String backgroundColor,String alignment, Boolean generateTOC, Boolean removeItalics) {
        this.fontType = fontType;
        this.fontSize = fontSize;
        this.fontColor = fontColor;
        this.lineSpacing = lineSpacing;
        this.characterSpacing = characterSpacing;
        this.backgroundColor = backgroundColor;
        this.alignment = alignment;
        this.generateTOC = generateTOC;
        this.removeItalics = removeItalics;
    }

    public String getFontType() {
        return fontType;
    }

    public String getFontSize() {
        return fontSize;
    }

    public String getFontColor() {
        return fontColor;
    }

    public String getLineSpacing() {
        return lineSpacing;
    }

    public String getCharacterSpacing() {
        return characterSpacing;
    }
    public String getBackgroundColor() {
        return backgroundColor;
    }
    public String getAlignment() {
        return alignment;
    }

    public Boolean getGenerateTOC() {
        return generateTOC;
    }
    public Boolean getRemoveItalics() {
        return removeItalics;
    }
}




