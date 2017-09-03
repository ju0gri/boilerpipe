package com.kohlschutter.boilerpipe.demo;

/**
 * Created by juogri on 03/09/2017.
 */
public class Status {
    private boolean success;
    private String description;
    private ExtractedData response;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExtractedData getResponse() {
        return response;
    }

    public void setResponse(ExtractedData response) {
        this.response = response;
    }
}
