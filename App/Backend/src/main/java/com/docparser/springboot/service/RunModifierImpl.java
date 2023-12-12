package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
public class RunModifierImpl implements RunModifier {

    // Modifies the font size of a line in a document.
    private void modifyLineFontSize(XWPFRun run, String fontSize) {
        run.setFontSize(Integer.parseInt(fontSize));
    }

    // Modifies the font color of a line in a document.
    private void modifyLineFontColor(XWPFRun run, String fontColor) {
        run.setColor(fontColor);
    }

    // Modifies the background color of a line in a document.
    private void modifyLineBackgroundColor(XWPFRun run, String fontColor) {
        CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
        CTShd cTShd = rpr.isSetShd() ? rpr.getShd() : rpr.addNewShd();
        if(rpr.isSetHighlight()){
            rpr.getHighlight().setVal(STHighlightColor.NONE);
        }
        run.getCTR().getRPr().addNewColor().setThemeColor(STThemeColor.NONE);
        cTShd.setVal(STShd.CLEAR);
        cTShd.setColor("auto");
        cTShd.setFill(fontColor);
    }

    // Modifies the font family of a line in a document.
    private void modifyFontFamily(XWPFRun run, String fontType) {
        run.setFontFamily(ParsingUtils.mapStringToFontStyle(fontType));
    }

    // Modifies the character spacing of a line in a document.
    private void modifyCharSpacing(XWPFRun run, String charSpacing) {
        if (run.getCTR() != null) {
            CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
            CTSignedTwipsMeasure charSpacingNew = rpr.addNewSpacing();
            charSpacingNew.setVal(ParsingUtils.mapStringToCharacterSpacingValueInBigInt(charSpacing));
        }
    }

    // Removes italic formatting from a line in a document.
    private void modifyToRemoveItalics(XWPFRun run) {
        run.setItalic(false);
    }

    // Modifies the font size for headings in a document.
    private void modifyHeadingFontSize(XWPFRun run, String fontSize) {
        run.setFontSize(ParsingUtils.getHeadingSize(Integer.parseInt(fontSize)));
    }

    // Placeholder for adding image labelling functionality (currently not
    // implemented).
    private void addImageLabelling(XWPFRun run) {
        if (!run.getEmbeddedPictures().isEmpty()) {
            run.getEmbeddedPictures().removeAll(run.getEmbeddedPictures());
        }
    }

    // Consumer function that applies various formatting changes to a line in a
    // document.
    BiConsumer<XWPFRun, DocumentConfig> modifyRun = (run, formattingConfig) -> {
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getFontSize()))
            modifyLineFontSize(run, formattingConfig.getFontSize());
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getFontColor()))
            modifyLineFontColor(run, formattingConfig.getFontColor());
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getBackgroundColor()))
            modifyLineBackgroundColor(run, formattingConfig.getBackgroundColor());
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getFontType()))
            modifyFontFamily(run, formattingConfig.getFontType());
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getCharacterSpacing()))
            modifyCharSpacing(run, formattingConfig.getCharacterSpacing());
        if (ParsingUtils.checkForBooleanFontParameterChange.test(formattingConfig.getRemoveItalics())
                && formattingConfig.getRemoveItalics().equals(Boolean.TRUE))
            modifyToRemoveItalics(run);
    };

    // Consumer function for applying specific formatting to heading lines in a
    // document.
    private final BiConsumer<XWPFRun, DocumentConfig> modifyHeadingRun = (run, formattingConfig) -> {
        run.setBold(true);
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getFontSize()))
            modifyHeadingFontSize(run, formattingConfig.getFontSize());
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getFontColor()))
            modifyLineFontColor(run, formattingConfig.getFontColor());
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getBackgroundColor()))
            modifyLineBackgroundColor(run, formattingConfig.getBackgroundColor());
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getFontType()))
            modifyFontFamily(run, formattingConfig.getFontType());
        if (ParsingUtils.checkForFontParameterChange.test(formattingConfig.getCharacterSpacing()))
            modifyCharSpacing(run, formattingConfig.getCharacterSpacing());
    };

    // Main method to apply modifications to a line in a document, differentiating
    // between regular and heading lines.
    @Override
    public void modify(XWPFRun run, DocumentConfig config, Boolean headingRun) {
        if (headingRun.equals(Boolean.TRUE))
            modifyHeadingRun.accept(run, config);
        else
            modifyRun.accept(run, config);
    }
}
