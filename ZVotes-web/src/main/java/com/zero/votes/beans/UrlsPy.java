package com.zero.votes.beans;

public enum UrlsPy { // in memory of the awesome django urls routing

    HOME("index.xhtml"),
    LOGIN("login.xhtml"),
    ACCOUNT("account/index.xhtml");

    private final String url;
    private final String url_prefix;

    UrlsPy(String url) {
        this.url = url;
        this.url_prefix = "/";
    }

    public String getUrl() {
        return this.url_prefix + this.url;
    }

    public String getUrl(boolean redirect) {
        String url = this.url_prefix + this.url;
        url += redirect ? "?faces-redirect=true" : "";
        return url;
    }
}
