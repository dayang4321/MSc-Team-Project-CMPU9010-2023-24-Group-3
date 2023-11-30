package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;

import java.util.List;


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

    private void modifyImage(XWPFDocument document) {
        List<XWPFPictureData> val = document.getAllPictures();
        POIXMLDocumentPart gg = val.get(0).getParent();
        XWPFParagraph targetParagraph = null;
        XWPFRun imageRun = null;
        int runIndex = 0;
        for (XWPFParagraph p : document.getParagraphs()) {
            for (XWPFRun run : p.getRuns()) {

                if (!run.getEmbeddedPictures().isEmpty()) {
                    targetParagraph = p;
                    imageRun = run;
                    break;
                }
            }
            if (targetParagraph != null) {
                break;
            }
        }
        if (targetParagraph != null && imageRun != null) {
            XWPFRun labelRun = targetParagraph.createRun();
            labelRun.setText("Figure 1: This is an image label.");
        }
    }

    public XWPFDocument process(XWPFDocument document, DocumentConfig config) {
        document.getParagraphs().stream().forEach(paragraph -> {
            paragraphModifier.modify(paragraph, config);
            if (ParsingUtils.checkIfHeadingStylePresent(paragraph)) {
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
        documentModifier.modify(document, config);
        return document;
    }
}


