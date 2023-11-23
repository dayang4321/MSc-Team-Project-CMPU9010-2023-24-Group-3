package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiConsumer;
@Component
public class ParagraphModifierImpl implements ParagraphModifier{


    private void addNewText(XWPFRun run, String para) {
        run.setText(para);
        run.addCarriageReturn();
    }

    private void modifyText(XWPFParagraph paragraph) {
        String text = paragraph.getParagraphText();
        if (ParsingUtils.countLines(text).length >= 10) {
            String[] paras = ParsingUtils.divideParagraph(text, 5);
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

    };
    @Override
    public void modify(XWPFParagraph paragraph, DocumentConfig config) {
        modifyParagraph.accept(paragraph, config);
    }

}
