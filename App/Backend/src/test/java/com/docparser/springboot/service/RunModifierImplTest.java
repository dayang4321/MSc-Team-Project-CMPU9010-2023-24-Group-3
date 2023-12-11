package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

import static org.mockito.Mockito.*;

class RunModifierImplTest {


    @Test
    void testModifyLineFontSize() {
        XWPFRun run = mock(XWPFRun.class);
        RunModifierImpl runModifier = new RunModifierImpl();
        DocumentConfig config = new DocumentConfig();
        config.setFontSize("12");
        runModifier.modify(run, config, false);
        verify(run, times(1)).setFontSize(12);
    }

    @Test
    void testModifyLineFontColor() {
        XWPFRun run = mock(XWPFRun.class);
         RunModifierImpl runModifier = new RunModifierImpl();
        DocumentConfig config = new DocumentConfig();
        config.setFontColor("FF0000");
        runModifier.modify(run, config, false);

        verify(run, times(1)).setColor("FF0000");
    }

    @Test
    void testModifyLineBackgroundColor() {
        XWPFRun run = mock(XWPFRun.class);
        DocumentConfig config = new DocumentConfig();
        RunModifierImpl runModifier = new RunModifierImpl();
        config.setCharacterSpacing("1.5");
        runModifier.modify(run, config, false);
        verify(run, times(1)).getCTR();
    }

    @Test
    void testModifyHeadingFontSize() {
        XWPFRun run = mock(XWPFRun.class);
        DocumentConfig config = new DocumentConfig();
        RunModifierImpl runModifier = new RunModifierImpl();
        config.setFontSize("12");
        runModifier.modify(run, config, true);
        verify(run, times(1)).setFontSize(14);
    }



}
