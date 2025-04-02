package com.translator.translator.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class HTTPRequestHandler {
    final String baseUrl = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=%s&tl=%s&dt=t&q=%s";
    
    public String getRequestForTranslation(String srcLan, String destLang, String text){
        try {
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String fin_q = baseUrl.formatted(srcLan, destLang, encodedText);
            
            System.out.println(fin_q);
            return fin_q;
        } catch (UnsupportedEncodingException e) {
            return "-";
        }
    }
}