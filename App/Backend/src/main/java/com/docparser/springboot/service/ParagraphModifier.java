package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;


public interface ParagraphModifier {

    void modify(XWPFParagraph paragraph, DocumentConfig config);
}
