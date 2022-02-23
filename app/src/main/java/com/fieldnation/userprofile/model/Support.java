package com.fieldnation.userprofile.model;

import java.io.Serializable;

public class Support implements Serializable {

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Support(String url, String text) {
        this.url = url;
        this.text = text;
    }

    private String url;
    private String text;

}
