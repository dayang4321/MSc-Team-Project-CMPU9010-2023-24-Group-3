package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import org.apache.poi.xwpf.usermodel.XWPFDocument;


public interface DocumentModifier {

    void modify(XWPFDocument document, DocumentConfig config);
}
