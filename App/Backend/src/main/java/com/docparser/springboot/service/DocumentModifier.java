package com.docparser.springboot.service;

// Importing essential packages for the DocumentModifier Interface
import com.docparser.springboot.model.DocumentConfig;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

// Interface for DocumentModifier which uses Apache POI to modify the document's metadata
public interface DocumentModifier {

    XWPFDocument modify(XWPFDocument document, DocumentConfig config);
}
