package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@AllArgsConstructor
public class DocumentModifierImpl implements DocumentModifier {

    private final NLPService nlpService;

    // Modifies the background color of a document
    public void modifyDocumentColor(XWPFDocument document, String color) {
        try {
            // Accessing private field ctSettings using reflection
            XWPFSettings settings = ParsingUtils.getSettings(document);
            java.lang.reflect.Field _ctSettings = XWPFSettings.class.getDeclaredField("ctSettings");
            _ctSettings.setAccessible(true);
            CTSettings ctSettings = (CTSettings) _ctSettings.get(settings);

            // Enabling background shape display
            CTOnOff onOff = CTOnOff.Factory.newInstance();
            onOff.setVal(STOnOff.ON);
            ctSettings.setDisplayBackgroundShape(onOff);

            // Setting the background color
            CTBackground background = document.getDocument().addNewBackground();
            background.setColor(color);
        } catch (Exception e) {
            // Throwing an exception in case of failure
            throw new RuntimeException(e);
        }
    }

    // Modifies headings in the document
    private void modifyEachHeading(String heading, XWPFParagraph paragraph) {
        XWPFRun run = ParsingUtils.createNewRun(paragraph);
        run.setText(heading);
        run.setFontFamily("Open Sans");
        run.setFontSize(16);
        run.setBold(true);
        run.addCarriageReturn();
    }

    // Adds a label to an image in the document
    private void modifyImage(XWPFDocument document) {
        XWPFParagraph targetParagraph = null;
        XWPFRun imageRun = null;
        int runIndex = 0;

        // Looping through paragraphs to find embedded images
        for (XWPFParagraph p : document.getParagraphs()) {
            for (XWPFRun run : p.getRuns()) {
                runIndex = p.getRuns().indexOf(run);

                // If an embedded image is found, add a label
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

        // Adding label to the image
        if (targetParagraph != null && imageRun != null) {
            XWPFRun labelRun = targetParagraph.createRun();
            labelRun.setText("Figure 1: This is an image label.");
        }
    }

    // Adds a header to the document
    private void addHeader(XWPFDocument document) {
        List<String> docHeadings = new ArrayList<>();
        Set<String> stopWords = ParsingUtils.stopWords();

        // Looping through paragraphs to add headers
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            if (!paragraph.getRuns().isEmpty() && !paragraph.getParagraphText().isEmpty()) {
                if (ParsingUtils.checkIfHeadingStylePresent(paragraph)) {
                    docHeadings = new ArrayList<>();
                    docHeadings.add(paragraph.getParagraphText());
                    continue;
                }
                if (docHeadings.isEmpty()) {
                    XWPFRun run = paragraph.insertNewRun(0);
                    String headingText = nlpService.findMostCommonWord(paragraph.getParagraphText(), stopWords);
                    run.setText(headingText.toUpperCase());
                    run.addCarriageReturn();
                    run.setFontSize(16); // Set font size as needed
                    run.setBold(true);
                }
            }
        }

    }

    // Creates a Table of Contents for the document
    private XWPFParagraph createTableOfContents(List<String> headings, XWPFDocument document) {
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

        // Adding each heading to the Table of Contents
        headings.stream().forEach(heading -> modifyEachHeading(heading, tocParagraph));
        return tocParagraph;
    }

    // Modifies the document to include a Table of Contents
    public void modifyDocumentToc(XWPFDocument document) {
        Optional<List<String>> headings = ParsingUtils.extractHeadings(document);
        if (headings.isPresent()) {
            XWPFParagraph newParagraph = createTableOfContents(headings.get(), document);
            // Move this paragraph to the beginning of the document
            CTBody body = document.getDocument().getBody();
            CTP newParagraphCtp = newParagraph.getCTP();
            body.insertNewP(0);
            CTP firstParagraph = body.getPArray(0);
            firstParagraph.set(newParagraphCtp);
            // Remove the duplicate (the original new paragraph at the end)
            body.removeP(body.sizeOfPArray() - 1);
        }
    }

    // Adds new text to a run
    private void addNewText(XWPFRun run, String para) {
        run.setText(para);
        run.addCarriageReturn();
    }

    // Modifies the text within the document
    private XWPFDocument modifyText(XWPFDocument document, XWPFDocument finalDoc) {
        finalDoc = ParsingUtils.copyStylesAndContent(document, finalDoc);
        int j = 0;

        // Looping through paragraphs to modify text
        for (int i = 0; i < document.getParagraphs().size(); i++) {
            XWPFParagraph paragraph = document.getParagraphs().get(i);
            String text = paragraph.getParagraphText();
            if (ParsingUtils.countLines(text).length >= 3) {
                String[] paras = ParsingUtils.divideParagraph(text, 3);
                if (j != 0)
                    j = j + 1;
                XWPFParagraph existingPara = finalDoc.getParagraphs().get(j);
                ParsingUtils.removeRuns(existingPara);
                XmlCursor cursor = existingPara.getCTP().newCursor();
                int noOfParas = paras.length;
                j = j + noOfParas;
                for (String para : paras) {
                    XWPFParagraph newParagraph = finalDoc.insertNewParagraph(cursor);
                    addNewText(newParagraph.createRun(), para);
                    cursor = newParagraph.getCTP().newCursor();
                }
            }
        }
        return finalDoc;
    }

    // Main method to modify the document
    @Override
    public XWPFDocument modify(XWPFDocument document, DocumentConfig formattingConfig) {
        XWPFDocument finalDoc = null;
        boolean image = true;

        // Applying various modifications based on formattingConfig
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getBackgroundColor())) {
            modifyDocumentColor(document, formattingConfig.getBackgroundColor());
        }
        if (ParsingUtils.checkForBooleanFontParameterChange.apply(formattingConfig.getHeaderGeneration())
                && formattingConfig.getHeaderGeneration())
            addHeader(document);
        if (ParsingUtils.checkForBooleanFontParameterChange.apply(formattingConfig.getGenerateTOC())
                && formattingConfig.getGenerateTOC()) {
            modifyDocumentToc(document);
        }

        return finalDoc == null ? document : finalDoc;
    }
}
