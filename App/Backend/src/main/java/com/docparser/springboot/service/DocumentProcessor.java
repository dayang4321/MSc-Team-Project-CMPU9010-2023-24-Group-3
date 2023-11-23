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

    public void process(XWPFDocument document, DocumentConfig config) {
        document.getParagraphs().stream().forEach(paragraph -> {
            paragraphModifier.modify(paragraph, config);
            if (ParsingUtils.checkForFontParameterChange.apply(config.getFontSize()) && ParsingUtils.checkIfHeadingStylePresent(paragraph)) {
                paragraph.getRuns().stream().findFirst().ifPresent(run -> runModifier.modify(run, config, true));
            } else {
                paragraph.getRuns().stream().forEach(run -> runModifier.modify(run, config, false));
            }
        });
        documentModifier.modify(document, config);

    }
}


