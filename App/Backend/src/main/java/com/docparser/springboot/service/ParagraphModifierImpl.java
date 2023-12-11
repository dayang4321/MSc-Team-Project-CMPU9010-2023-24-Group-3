package com.docparser.springboot.service;

import com.docparser.springboot.controller.DocxController;
import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
public class ParagraphModifierImpl implements ParagraphModifier {
    private final NLPService nlpService;
    Logger logger = LoggerFactory.getLogger(ParagraphModifierImpl.class);

    // Adds new text to a paragraph with carriage returns
    private void addNewText(XWPFRun run, String para) {
        run.setText(para);
        run.addCarriageReturn();
        run.addCarriageReturn();
    }

    // Modifies text in a paragraph to add syllable styling
    private void modifyTextToAddSyllableStyling(XWPFParagraph paragraph) {
        String text = paragraph.getParagraphText();
        String formattedText = nlpService.hyphenateText(text);
        ParsingUtils.removeRuns(paragraph);
        paragraph.createRun().setText(formattedText);

    }

    private void modifyText(XWPFParagraph paragraph) {
        String text = paragraph.getParagraphText();
        String[] lines = ParsingUtils.countLines(text);
        if (lines != null && lines.length >= 2) {
            String[] paras = ParsingUtils.divideParagraph(text, 2);
            ParsingUtils.removeRuns(paragraph);
            for (String para : paras) {
                addNewText(paragraph.createRun(), para);
            }
        }
    }

    // Modifies the alignment of a paragraph
    private void modifyAlignment(XWPFParagraph paragraph, String alignment) {
        paragraph.setAlignment(ParsingUtils.mapStringToAlignment(alignment));
    }

    // Modifies line spacing of a paragraph
    private void modifyLineSpacing(XWPFParagraph paragraph, String lineSpacing) {
        CTPPr ctpPr = ParsingUtils.getCTPPr(paragraph);
        if (ctpPr.isSetSpacing()) {
            ctpPr.getSpacing().setLineRule(STLineSpacingRule.AUTO);
            ctpPr.getSpacing().setLine(ParsingUtils.mapStringToLineSpacingValueInBigInt(lineSpacing));
        } else {
            ctpPr.addNewSpacing().setLineRule(STLineSpacingRule.AUTO);
            ctpPr.getSpacing().setLine(ParsingUtils.mapStringToLineSpacingValueInBigInt(lineSpacing));
        }
    }

    private void addParagraphBorder(XWPFParagraph paragraph) {
        if (paragraph.getRuns().isEmpty() || paragraph.getParagraphText().isEmpty()) {
            return;
        }
        paragraph.setBorderBottom(Borders.BASIC_BLACK_DOTS);
        paragraph.setBorderLeft(Borders.BASIC_BLACK_DOTS);
        paragraph.setBorderRight(Borders.BASIC_BLACK_DOTS);
        paragraph.setBorderTop(Borders.BASIC_BLACK_DOTS);


    }

    private void addHeader(XWPFParagraph paragraph) {
        Set<String> stopWords = ParsingUtils.stopWords();
        if (!paragraph.getRuns().isEmpty() && !paragraph.getParagraphText().isEmpty()
                && (!ParsingUtils.checkIfHeadingStylePresent(paragraph))) {
            XWPFRun run = paragraph.insertNewRun(0);
            String headingText = nlpService.findMostCommonWord(paragraph.getParagraphText(), stopWords);
            run.setText(headingText.toUpperCase());
            run.addCarriageReturn();
            run.setFontSize(16); // Set font size as needed
            run.setBold(true);


        }
    }

    private void modifyColorShading(XWPFParagraph paragraph, String colorShading) {
        CTPPr ctpPr = ParsingUtils.getCTPPr(paragraph);
        CTParaRPr ll = ctpPr.getRPr() == null ? ctpPr.addNewRPr() : ctpPr.getRPr();
        CTColor val = ll.isSetColor() ? ctpPr.getRPr().getColor() : ctpPr.getRPr().addNewColor();
        val.setVal(colorShading);
        Optional.ofNullable(ctpPr.getShd()).ifPresentOrElse(shd -> shd.setFill(colorShading),
                () -> ctpPr.addNewShd().setFill(colorShading));
    }

    // Lambda expression for modifying a paragraph with various styling options
    private final BiConsumer<XWPFParagraph, DocumentConfig> modifyParagraph = (paragraph, formattingConfig) -> {
        // Apply different modifications based on the formatting configuration
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getAlignment()))
            modifyAlignment(paragraph, formattingConfig.getAlignment());
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getLineSpacing()))
            modifyLineSpacing(paragraph, formattingConfig.getLineSpacing());
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getBackgroundColor()))
            modifyColorShading(paragraph, formattingConfig.getBackgroundColor());
        if (ParsingUtils.checkForBooleanFontParameterChange.test(formattingConfig.getSyllableSplitting())
                && formattingConfig.getSyllableSplitting().equals(Boolean.TRUE))
            modifyTextToAddSyllableStyling(paragraph);
        if (ParsingUtils.checkForBooleanFontParameterChange.test(formattingConfig.getParagraphSplitting())
                && formattingConfig.getParagraphSplitting().equals(Boolean.TRUE))
            modifyText(paragraph);
        if (ParsingUtils.checkForBooleanFontParameterChange.test(formattingConfig.getBorderGeneration())
                && formattingConfig.getBorderGeneration().equals(Boolean.TRUE))
            addParagraphBorder(paragraph);
    };

    // Method to apply modifications to a paragraph
    @Override
    public void modify(XWPFParagraph paragraph, DocumentConfig config) {
        modifyParagraph.accept(paragraph, config);
    }

}
