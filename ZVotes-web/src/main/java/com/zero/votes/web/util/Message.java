package com.zero.votes.web.util;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class Message {
    public static void addInternationalizedMessage(Severity messageType, String messageString) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("com.zero.votes.Locale", context.getViewRoot().getLocale());
        FacesMessage msg = new FacesMessage(bundle.getString(messageString));
        msg.setSeverity(messageType);
        context.addMessage(null, msg);
    }
}
