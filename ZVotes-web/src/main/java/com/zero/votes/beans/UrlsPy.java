package com.zero.votes.beans;

public enum UrlsPy { // in memory of the awesome django urls routing

    HOME("index.xhtml"),
    LOGIN("login.xhtml"),
    ACCOUNT("account/index.xhtml"),
    
    ITEM_LIST("account/items/"),
    ITEM_CREATE("account/item/create/"),
    ITEM_EDIT("account/item/edit/"),
    
    ITEM_OPTION_CREATE("account/item-option/create/"),
    ITEM_OPTION_EDIT("account/item-option/edit/"),
    
    ORGANIZER_LIST("account/organizers/"),
    ORGANIZER_ADD("account/organizer/add/"),
    
    POLL_LIST("account/polls/"),
    POLL_CREATE("account/poll/create/"),
    POLL_EDIT("account/poll/edit/"),
    
    PARTICIPANT_CREATE("account/participant/create/"),
    PARTICIPANT_LIST("account/participants/"),
    PARTICIPANT_EDIT("account/participant/edit/"),
    
    PARTICIPANT_IMPORT_LIST("account/participant/import-list/"),
    
    RECIPIENT_LIST("account/recipients/"),
    RECIPIENT_CREATE("account/recipient/create/"),
    RECIPIENT_EDIT("account/recipient/edit/"),
    
    RECIPIENTLIST_LIST("account/recipient-lists/"),
    RECIPIENTLIST_CREATE("account/recipient-list/create/"),
    RECIPIENTLIST_EDIT("account/recipient-list/edit/"),
    
    ADMIN("admin/"),
    ADMIN_LIST("admin/admins/"),
    ADMIN_ADD("admin/admins/add/"),
    
    ADMIN_POLL_LIST("admin/polls/"),
    
    TOKEN("token/"),
    POLL("poll/"),
    
    LOGOUT("logout/");

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
