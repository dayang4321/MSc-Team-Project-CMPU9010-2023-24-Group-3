package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * The RunModifier interface defines a single method to modify runs in a
 * document.
 * A run in Apache POI represents a set of contiguous text with the same
 * properties,
 * like a piece of text within a paragraph.
 */
public interface RunModifier {

    /**
     * Modifies a given XWPFRun according to the provided DocumentConfig.
     * This method can be implemented to apply specific styles, formatting, or any
     * other
     * modifications to the text run.
     * 
     * @param run        The XWPFRun object representing a segment of text in the
     *                   document.
     * @param config     The DocumentConfig object containing configuration settings
     *                   that might be applied to the run.
     * @param headingRun A Boolean indicating if the current run is part of a
     *                   heading.
     */
    void modify(XWPFRun run, DocumentConfig config, Boolean headingRun);
}
