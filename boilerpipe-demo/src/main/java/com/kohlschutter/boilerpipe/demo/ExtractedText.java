package com.kohlschutter.boilerpipe.demo;

/**
 * Created by juogri on 03/09/2017.
 */
public class ExtractedText extends  ExtractedData{
    private String content;

    ExtractedText(String title, String url, String content){
        super(title, url);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
