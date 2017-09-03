package com.kohlschutter.boilerpipe.demo;

/**
 * Created by juogri on 03/09/2017.
 */
public class ExtractedData {
    private String title;
    private String url;

    ExtractedData(String title, String url){
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
