package com.docparser.springboot.model;

public class ParagraphStyleInfo {

    private String fontStyle;
    private String fontSize;
    private String fontColor;
    private String paragraphAlignment;

    public ParagraphStyleInfo(String fontStyle, String fontSize, String fontColor, String paragraphAlignment) {
        this.fontStyle = fontStyle;
        this.fontSize = fontSize;
        this.fontColor = fontColor;
        this.paragraphAlignment = paragraphAlignment;
    }
    public ParagraphStyleInfo() {

    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
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

    public String getParagraphAlignment() {
        return paragraphAlignment;
    }

    public void setParagraphAlignment(String paragraphAlignment) {
        this.paragraphAlignment = paragraphAlignment;
    }


}
