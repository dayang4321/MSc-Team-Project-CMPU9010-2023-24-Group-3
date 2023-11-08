package com.docparser.springboot.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ParsingUtils {


    private ParsingUtils() {

    }
    public  List<XWPFParagraph> getParagraphsInTheDocument(XWPFDocument document) {
        return document.getParagraphs();
    }
    public  String getTextFromParagraph(XWPFParagraph paragraph) {
        return paragraph.getParagraphText();
    }


}
