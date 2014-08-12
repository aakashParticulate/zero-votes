/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.beans;

/**
 *
 * @author stephan
 */
public enum UrlsPy { // in memory of the awesome django urls routing
    HOME ("index.xhtml"),
    LOGIN ("login.xhtml"),
    ACCOUNT ("account/index.xhtml");
    
    private final String url;
    private final String url_prefix;
    
    UrlsPy(String url) {
        this.url = url;
        this.url_prefix = "/faces/";
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
