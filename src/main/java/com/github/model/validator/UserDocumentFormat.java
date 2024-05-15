package com.github.model.validator;

public class UserDocumentFormat {

    public static String replaceDocument(String document) {
    	return document != null ? document.replaceAll("[^0-9]", "") : "";
    }
   
}
