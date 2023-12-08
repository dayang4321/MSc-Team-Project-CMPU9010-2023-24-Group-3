package com.docparser.springboot.service;

// Importing necessary classes from external libraries
/*
 * Importing the DocumentConfig model class and the XWPFParagraph class from Apache POI library for handling Word files
 */
import com.docparser.springboot.model.DocumentConfig;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 * This interface defines a contract for modifying paragraphs in a document.
 * Implementing this interface allows for customized manipulation of paragraphs
 * based on specific document configurations.
 */
public interface ParagraphModifier {

    /**
     * Method to modify a paragraph in a document.
     * 
     * @param paragraph The paragraph to be modified. This is an XWPFParagraph
     *                  object,
     *                  which represents a paragraph in a Word document.
     * @param config    The configuration settings for modifying the paragraph. This
     *                  is based on the DocumentConfig model which can include
     *                  various
     *                  settings and parameters for the modification.
     */
    void modify(XWPFParagraph paragraph, DocumentConfig config);
}
