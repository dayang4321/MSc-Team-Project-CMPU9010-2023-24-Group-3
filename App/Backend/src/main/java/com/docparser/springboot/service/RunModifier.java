package com.docparser.springboot.service;

import com.docparser.springboot.model.DocumentConfig;
import org.apache.poi.xwpf.usermodel.XWPFRun;


public interface RunModifier {

    void modify(XWPFRun run, DocumentConfig config, Boolean headingRun);
}
