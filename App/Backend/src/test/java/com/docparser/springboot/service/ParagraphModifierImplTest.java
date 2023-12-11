/*
package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import com.docparser.springboot.utils.ParsingUtils;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;

import static org.mockito.Mockito.*;

class ParagraphModifierImplTest {

    @InjectMocks
    private NLPService nlpService;

    @InjectMocks
    private ParagraphModifierImpl paragraphModifier;

    @Test
    void testModifyAlignment() {
        XWPFParagraph paragraph = mock(XWPFParagraph.class);
        DocumentConfig config = new DocumentConfig();
        config.setAlignment("center");
        ParagraphModifierImpl paragraphModifier1 = new ParagraphModifierImpl(nlpService);
        paragraphModifier1.modify(paragraph, config);

        verify(paragraph, times(1)).setAlignment(ParsingUtils.mapStringToAlignment("center"));
    }

    @Test
    void testModifyLineSpacing() {
        XWPFParagraph paragraph = mock(XWPFParagraph.class);
        CTPPr ctpPr = mock(CTPPr.class);
        when(ParsingUtils.getCTPPr(paragraph)).thenReturn(ctpPr);
        DocumentConfig config = new DocumentConfig();
        config.setLineSpacing("1.5");
        ParagraphModifierImpl paragraphModifier1 = new ParagraphModifierImpl(nlpService);
        paragraphModifier1.modify(paragraph, config);

        verify(ctpPr, times(1)).addNewSpacing();
        verify(ctpPr, times(1)).getSpacing();
    }

    @Test
    void testModifyColorShading() {
        XWPFParagraph paragraph = mock(XWPFParagraph.class);
        DocumentConfig config = new DocumentConfig();
        config.setBackgroundColor("FF0000");

        ParagraphModifierImpl paragraphModifier1 = new ParagraphModifierImpl(nlpService);
        paragraphModifier1.modify(paragraph, config);

        // Verify the modifications you expect
    }

    @Test
    void testModifyTextToAddSyllableStyling() {
        XWPFParagraph paragraph = mock(XWPFParagraph.class);
        DocumentConfig config = new DocumentConfig();
        config.setSyllableSplitting(true);

        when(nlpService.hyphenateText(anyString())).thenReturn("formatted text");

        ParagraphModifierImpl paragraphModifier1 = new ParagraphModifierImpl(nlpService);
        paragraphModifier1.modify(paragraph, config);

        verify(paragraph, times(1)).createRun();
        //verify(paragraph, times(1)).setText("formatted text");
    }

    @Test
    void testModifyText() {
        XWPFParagraph paragraph = mock(XWPFParagraph.class);
        paragraph.createRun().setText("text");
        DocumentConfig config = new DocumentConfig();
        config.setParagraphSplitting(true);

        // Mocking behavior for ParsingUtils.countLines and ParsingUtils.divideParagraph
        ParagraphModifierImpl paragraphModifier1 = new ParagraphModifierImpl(nlpService);
        paragraphModifier1.modify(paragraph, config);

        // Verify the modifications you expect
    }

    @Test
    void testAddParagraphBorder() {
        XWPFParagraph paragraph = mock(XWPFParagraph.class);
        Borders borders = mock(Borders.class);
        when(paragraph.getRuns()).thenReturn(null);
        when(paragraph.getParagraphText()).thenReturn("");
        DocumentConfig config = new DocumentConfig();
        config.setBorderGeneration(true);

        ParagraphModifierImpl paragraphModifier1 = new ParagraphModifierImpl(nlpService);
        paragraphModifier1.modify(paragraph, config);

        verify(paragraph, times(1)).setBorderBottom(Borders.BASIC_BLACK_DOTS);
        verify(paragraph, times(1)).setBorderLeft(Borders.BASIC_BLACK_DOTS);
        verify(paragraph, times(1)).setBorderRight(Borders.BASIC_BLACK_DOTS);
        verify(paragraph, times(1)).setBorderTop(Borders.BASIC_BLACK_DOTS);
    }

    // Add more test cases as needed for other methods and scenarios

}
*/
