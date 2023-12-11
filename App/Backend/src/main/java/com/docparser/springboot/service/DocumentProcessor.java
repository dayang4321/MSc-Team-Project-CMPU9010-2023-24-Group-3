package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;


// Mark this class as a Spring Component
@Component
public class DocumentProcessor {
    // Dependencies for modifying various parts of a document
    private final DocumentModifierImpl documentModifier;
    private final ParagraphModifierImpl paragraphModifier;
    private final RunModifierImpl runModifier;

    // Constructor for injecting dependencies
    public DocumentProcessor(DocumentModifierImpl docMod, ParagraphModifierImpl paraMod, RunModifierImpl runMod) {
        this.documentModifier = docMod;
        this.paragraphModifier = paraMod;
        this.runModifier = runMod;
    }



    // Main method to process the document
    public XWPFDocument process(XWPFDocument document, DocumentConfig config) {
        // Iterate over paragraphs for modification
        document.getParagraphs().stream().forEach(paragraph -> {
            paragraphModifier.modify(paragraph, config);
            if (ParsingUtils.checkIfHeadingStylePresent(paragraph)) {
                // Apply different modifications based on the number of runs in the paragraph
                if (paragraph.getRuns().size() > 1) {
                    paragraph.getRuns().stream().findFirst().ifPresent(run -> runModifier.modify(run, config, true));
                    paragraph.getRuns().stream().skip(1).forEach(run -> runModifier.modify(run, config, false));
                } else {
                    paragraph.getRuns().stream().findFirst().ifPresent(run -> runModifier.modify(run, config, true));
                }

            } else {
                paragraph.getRuns().forEach(run -> runModifier.modify(run, config, false));
            }
        });
        // Apply modifications to the document as a whole
        documentModifier.modify(document, config);
        return document;
    }
}
