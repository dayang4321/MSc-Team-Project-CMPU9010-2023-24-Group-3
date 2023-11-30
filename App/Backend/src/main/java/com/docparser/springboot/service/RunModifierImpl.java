package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
public class RunModifierImpl implements RunModifier {

    private void modifyLineFontSize(XWPFRun run, String fontSize) {
        run.setFontSize(Integer.parseInt(fontSize));
    }

    private void modifyLineFontColor(XWPFRun run, String fontColor) {
        run.setColor(fontColor);
    }

    private void modifyLineBackgroundColor(XWPFRun run, String fontColor) {
        CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
        CTShd cTShd = rpr.isSetShd() ? rpr.getShd() : rpr.addNewShd();
        run.getCTR().getRPr().addNewColor().setThemeColor(STThemeColor.NONE);
        cTShd.setVal(STShd.CLEAR);
        cTShd.setColor("auto");
        cTShd.setFill(fontColor);
    }

    private void modifyFontFamily(XWPFRun run, String fontType) {
        run.setFontFamily(ParsingUtils.mapStringToFontStyle(fontType));
    }

    public void modifyCharSpacing(XWPFRun run, String charSpacing) {
        CTRPr rpr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
        CTSignedTwipsMeasure charSpacingNew = rpr.addNewSpacing();
        charSpacingNew.setVal(ParsingUtils.mapStringToCharacterSpacingValueInBigInt(charSpacing));
    }

    public void modifyToRemoveItalics(XWPFRun run) {
        run.setItalic(false);
    }

    private void modifyHeadingFontSize(XWPFRun run, String fontSize) {
        run.setFontSize(ParsingUtils.getHeadingSize(Integer.parseInt(fontSize)));
    }

    private void addImageLabelling(XWPFRun run, String fontSize) {
        if (!run.getEmbeddedPictures().isEmpty()) {
            run.getEmbeddedPictures().removeAll(run.getEmbeddedPictures());
        }
    }

    BiConsumer<XWPFRun, DocumentConfig> modifyRun = (run, formattingConfig) -> {
        boolean images = true;
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getFontSize()))
            modifyLineFontSize(run, formattingConfig.getFontSize());
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getFontColor()))
            modifyLineFontColor(run, formattingConfig.getFontColor());
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getBackgroundColor()))
            modifyLineBackgroundColor(run, formattingConfig.getBackgroundColor());
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getFontType()))
            modifyFontFamily(run, formattingConfig.getFontType());
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getCharacterSpacing()))
            modifyCharSpacing(run, formattingConfig.getCharacterSpacing());
        if (ParsingUtils.checkForBooleanFontParameterChange.apply(formattingConfig.getRemoveItalics()))
            modifyToRemoveItalics(run);
//if(images)
        //   addImageLabelling(run,formattingConfig.getFontSize());
        // Define the behavior of modifyRun here
    };
    private final BiConsumer<XWPFRun, DocumentConfig> modifyHeadingRun = (run, formattingConfig) -> {
        run.setBold(true);
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getFontSize()))
            modifyHeadingFontSize(run, formattingConfig.getFontSize());
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getFontColor()))
            modifyLineFontColor(run, formattingConfig.getFontColor());
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getFontType()))
            modifyFontFamily(run, formattingConfig.getFontType());
        if (ParsingUtils.checkForFontParameterChange.apply(formattingConfig.getCharacterSpacing()))
            modifyCharSpacing(run, formattingConfig.getCharacterSpacing());
    };

    @Override
    public void modify(XWPFRun run, DocumentConfig config, Boolean headingRun) {
        if (headingRun)
            modifyHeadingRun.accept(run, config);
        else
            modifyRun.accept(run, config);
    }
}
