package com.docparser.springboot.model;

// Importing Lombok annotations for automatic getter, setter, no-args constructor, and all-args constructor generation
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Annotating the class to use Lombok's Getter, Setter, NoArgsConstructor, and AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentConfig implements Cloneable {
    // Fields to store various configuration settings for a document
    private String fontType;
    private String fontSize;
    private String fontColor;
    private String backgroundColor;
    private String lineSpacing;
    private String characterSpacing;
    private String alignment;
    private Boolean generateTOC; // Table of Contents generation flag
    private Boolean removeItalics; // Flag to indicate if italics should be removed
    private Boolean paragraphSplitting; // Flag for splitting paragraphs
    private Boolean headerGeneration; // Flag for generating headers
    private Boolean borderGeneration; // Flag for generating borders
    private Boolean syllableSplitting; // Flag for splitting syllables

    // Override the clone method to allow cloning of DocumentConfig objects
    @Override
    public DocumentConfig clone() {
        try {
            // Creating a clone of the current object.
            DocumentConfig clone = (DocumentConfig) super.clone();
            /*
             * TODO: copy mutable state here, so the clone can't change the internals of the
             * original
             */
            return clone;
        } catch (CloneNotSupportedException e) {
            // If cloning is not supported, throw an AssertionError
            throw new AssertionError();
        }
    }
}
