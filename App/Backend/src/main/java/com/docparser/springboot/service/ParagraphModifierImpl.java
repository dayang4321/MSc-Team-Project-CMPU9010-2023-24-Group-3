package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiConsumer;

@Component
@AllArgsConstructor
public class ParagraphModifierImpl implements ParagraphModifier {
    private  final NLPService nlpService;

    private void addNewText(XWPFRun run, String para) {
        run.setText(para);
        run.addCarriageReturn();
        run.addCarriageReturn();
    }

    private void modifyTextToAddSyllableStyling(XWPFParagraph paragraph, DocumentConfig formattingConfig) {
        String text = paragraph.getParagraphText();
       String formattedText= nlpService.hyphenateText(text);
       ParsingUtils.removeRuns(paragraph);
       paragraph.createRun().setText(formattedText);

    }
    private void modifyText(XWPFParagraph paragraph, DocumentConfig formattingConfig) {
        String text = paragraph.getParagraphText();
        String contentHash = Integer.toHexString(paragraph.getText().hashCode());
        String []lines = ParsingUtils.countLines(text);
        if (lines!=null && lines.length >= 2) {
            String[] paras = ParsingUtils.divideParagraph(text, 2);
            ParsingUtils.removeRuns(paragraph);
            for (String para : paras) {
                addNewText(paragraph.createRun(), para);
            }
        }
    }


    private void modifyAlignment(XWPFParagraph paragraph, String alignment) {
        paragraph.setAlignment(ParsingUtils.mapStringToAlignment(alignment));
    }

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

private  void addParagraphBorder(XWPFParagraph paragraph) {
if(paragraph.getRuns().size()>1){
    paragraph.setBorderBottom(Borders.BASIC_BLACK_DASHES);
    paragraph.setBorderLeft(Borders.BASIC_BLACK_DASHES);
    paragraph.setBorderRight(Borders.BASIC_BLACK_DASHES);
    paragraph.setBorderTop(Borders.BASIC_BLACK_DASHES);
}

}
    private void modifyColorShading(XWPFParagraph paragraph, String colorShading) {
        CTPPr ctpPr = ParsingUtils.getCTPPr(paragraph);
        CTParaRPr ll = ctpPr.getRPr() == null ? ctpPr.addNewRPr() : ctpPr.getRPr();
        CTColor val = ll.isSetColor() ? ctpPr.getRPr().getColor() : ctpPr.getRPr().addNewColor();
        val.setVal(colorShading);
        Optional.ofNullable(ctpPr.getShd()).ifPresentOrElse(shd -> shd.setFill(colorShading), () -> ctpPr.addNewShd().setFill(colorShading));
    }

    private final BiConsumer<XWPFParagraph, DocumentConfig> modifyParagraph = (paragraph, formattingConfig) -> {
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getAlignment()))
            modifyAlignment(paragraph, formattingConfig.getAlignment());
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getLineSpacing()))
            modifyLineSpacing(paragraph, formattingConfig.getLineSpacing());
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getBackgroundColor()))
            modifyColorShading(paragraph, formattingConfig.getBackgroundColor());
        if (ParsingUtils.checkForBooleanFontParameterChange.apply(formattingConfig.getSyllableSplitting()) && formattingConfig.getSyllableSplitting())
            modifyTextToAddSyllableStyling(paragraph, formattingConfig);
        if (ParsingUtils.checkForBooleanFontParameterChange.apply(formattingConfig.getParagraphSplitting()) && formattingConfig.getParagraphSplitting())
            modifyText(paragraph, formattingConfig);
        if (ParsingUtils.checkForBooleanFontParameterChange.apply(formattingConfig.getBorderGeneration()) && formattingConfig.getBorderGeneration())
            addParagraphBorder(paragraph);


    };

    @Override
    public void modify(XWPFParagraph paragraph, DocumentConfig config) {
        modifyParagraph.accept(paragraph, config);
    }

}
