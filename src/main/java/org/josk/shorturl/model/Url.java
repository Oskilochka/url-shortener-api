package org.josk.shorturl.model;

public class Url {
    private String originalUrl;

    public Url() {}

    public Url(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    @Override
    public String toString() {
        return "Url{originalUrl='" + originalUrl + "'}";
    }
}
