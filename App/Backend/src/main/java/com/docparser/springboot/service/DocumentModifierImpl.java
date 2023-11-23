package com.docparser.springboot.service;


import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DocumentModifierImpl implements DocumentModifier{


    public void modifyDocumentColor(XWPFDocument document, String color) {
        try {
            XWPFSettings  settings = ParsingUtils.getSettings(document);
            java.lang.reflect.Field _ctSettings = XWPFSettings.class.getDeclaredField("ctSettings");
            _ctSettings.setAccessible(true);
            CTSettings ctSettings = (CTSettings) _ctSettings.get(settings);
            CTOnOff onOff = CTOnOff.Factory.newInstance();
            onOff.setVal(STOnOff.ON);
            ctSettings.setDisplayBackgroundShape(onOff);
            CTBackground background = document.getDocument().addNewBackground();
            background.setColor(color);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void modifyEachHeading(String heading, XWPFParagraph paragraph) {
        XWPFRun run = ParsingUtils.createNewRun(paragraph);
        run.setText(heading);
        run.setFontFamily("Open Sans");
        run.setFontSize(16);
        run.setBold(true);
        run.addCarriageReturn();
    }
    private XWPFParagraph createTableOfContents(List<String> headings, XWPFDocument document, DocumentConfig formattingConfig) {
        XWPFParagraph tocParagraph = ParsingUtils.createNewParagraph(document);
        tocParagraph.setPageBreak(true);
        tocParagraph.setAlignment(ParagraphAlignment.CENTER);
        tocParagraph.setStyle("Heading1");
        XWPFRun tocRun = tocParagraph.createRun();
        tocRun.setText("Table of Contents");
        tocRun.setFontFamily("Open Sans");
        tocRun.addBreak();
        tocRun.setFontSize(18);
        tocRun.setBold(true);
        headings.stream().forEach(heading -> modifyEachHeading(heading, tocParagraph));
       // modifyParagraph.accept(tocParagraph, formattingConfig);
        return tocParagraph;
    }
    public void modifyDocumentToc(XWPFDocument oldDocument, DocumentConfig formattingConfig) {
        Optional<List<String>> headings = ParsingUtils.extractHeadings(oldDocument);
        if (headings.isPresent()) {
            XWPFParagraph newParagraph = createTableOfContents(headings.get(), oldDocument, formattingConfig);
            // Move this paragraph to the beginning of the document
            CTBody body = oldDocument.getDocument().getBody();
            CTP newParagraphCtp = newParagraph.getCTP();
            body.insertNewP(0);
            CTP firstParagraph = body.getPArray(0);
            firstParagraph.set(newParagraphCtp);
// Remove the duplicate (the original new paragraph at the end)
            body.removeP(body.sizeOfPArray() - 1);
        }

    }
    @Override
    public void modify(XWPFDocument document, DocumentConfig formattingConfig) {
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getBackgroundColor())) {
            modifyDocumentColor(document, formattingConfig.getBackgroundColor());
        }
        if (ParsingUtils.checkForBooleanFontParameterChange.apply(formattingConfig.getGenerateTOC())) {
            modifyDocumentToc(document, formattingConfig);
        }
    }
}
