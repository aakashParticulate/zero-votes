package com.zero.votes.beans;

public enum UrlsPy { // in memory of the awesome django urls routing

    HOME("index.xhtml"),
    LOGIN("login.xhtml"),
    ACCOUNT("account/index.xhtml"),
    
    ITEM_CREATE("account/item_create.xhtml"),
    ITEM_LIST("account/item_list.xhtml"),
    ITEM_EDIT("account/item_edit.xhtml"),
    
    ITEM_OPTION_CREATE("account/item_option_create.xhtml"),
    ITEM_OPTION_EDIT("account/item_option_edit.xhtml"),
    
    ORGANIZER_LIST("account/organizer_list.xhtml"),
    ORGANIZER_ADD("account/organizer_add.xhtml"),
    
    POLL_CREATE("account/poll_create.xhtml"),
    POLL_LIST("account/poll_list.xhtml"),
    POLL_EDIT("account/poll_edit.xhtml"),
    
    PARTICIPANT_CREATE("account/participant_create.xhtml"),
    PARTICIPANT_LIST("account/participant_list.xhtml"),
    PARTICIPANT_EDIT("account/participant_edit.xhtml"),
    
    PARTICIPANT_IMPORT_LIST("account/participant_import_list.xhtml"),
    
    RECIPIENT_CREATE("account/recipient_create.xhtml"),
    RECIPIENT_LIST("account/recipient_list.xhtml"),
    RECIPIENT_EDIT("account/recipient_edit.xhtml"),
    
    RECIPIENTLIST_CREATE("account/recipientlist_create.xhtml"),
    RECIPIENTLIST_LIST("account/recipientlist_list.xhtml"),
    RECIPIENTLIST_EDIT("account/recipientlist_edit.xhtml"),
    
    ADMIN("admin/index.xhtml"),
    ADMIN_LIST("admin/admin_list.xhtml"),
    ADMIN_ADD("admin/admin_add.xhtml"),
    
    ADMIN_POLL_LIST("admin/poll_list.xhtml"),
    
    TOKEN("token.xhtml"),
    POLL("poll.xhtml"),
    
    LOGOUT("logout.xhtml");

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
