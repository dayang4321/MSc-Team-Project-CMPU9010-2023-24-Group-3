package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;


@Component
public class DocumentProcessor {
    private final DocumentModifierImpl documentModifier;
    private final ParagraphModifierImpl paragraphModifier;
    private final RunModifierImpl runModifier;

    public DocumentProcessor(DocumentModifierImpl docMod, ParagraphModifierImpl paraMod, RunModifierImpl runMod) {
        this.documentModifier = docMod;
        this.paragraphModifier = paraMod;
        this.runModifier = runMod;
    }

    public XWPFDocument process(XWPFDocument document, DocumentConfig config) {
        XWPFDocument finalDoc = documentModifier.modify(document, config);
        finalDoc.getParagraphs().stream().forEach(paragraph -> {
            paragraphModifier.modify(paragraph, config);
            if ( ParsingUtils.checkIfHeadingStylePresent(paragraph)) {
                // Check if the paragraph has more than one run
                if (paragraph.getRuns().size() > 1) {
                    // Apply heading modifications to the first run
                    paragraph.getRuns().stream().findFirst().ifPresent(run -> runModifier.modify(run, config, true));
                    // Apply run modifications to the rest of the runs
                    paragraph.getRuns().stream().skip(1).forEach(run -> runModifier.modify(run, config, false));
                } else {
                    // Apply heading modifications to the only run in the paragraph
                    paragraph.getRuns().stream().findFirst().ifPresent(run -> runModifier.modify(run, config, true));
                }

            } else {
                // Apply heading modifications to the only run in the paragraph
                paragraph.getRuns().forEach(run -> runModifier.modify(run, config, false));
            }
        });
        //apply toc generation after all the modifications
        if (ParsingUtils.checkForBooleanFontParameterChange.apply(config.getGenerateTOC())) {
            documentModifier.modifyDocumentToc(finalDoc);
        }
        return finalDoc;
    }
}


