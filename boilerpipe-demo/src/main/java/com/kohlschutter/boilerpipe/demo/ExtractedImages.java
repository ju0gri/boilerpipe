package com.kohlschutter.boilerpipe.demo;

import java.util.List;

/**
 * Created by juogri on 03/09/2017.
 */
public class ExtractedImages extends ExtractedData {
    private List<String> content;

    ExtractedImages(String title, String url, List<String> content) {
        super(title, url);
        this.content = content;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }
}
