package com.zero.votes.web.util;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class ZVotesUtils {
    public static void addInternationalizedMessage(Severity messageType, String messageString) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("com.zero.votes.Locale", context.getViewRoot().getLocale());
        FacesMessage msg = new FacesMessage(bundle.getString(messageString));
        msg.setSeverity(messageType);
        context.addMessage(null, msg);
    }
    
    public static void addInternationalizedInfoMessage(String messageString) {
        addInternationalizedMessage(FacesMessage.SEVERITY_INFO, messageString);
    }
    
    public static void addInternationalizedWarnMessage(String messageString) {
        addInternationalizedMessage(FacesMessage.SEVERITY_WARN, messageString);
    }
    
    public static void addInternationalizedErrorMessage(String messageString) {
        addInternationalizedMessage(FacesMessage.SEVERITY_ERROR, messageString);
    }
    
    public static void addInternationalizedFatalMessage(String messageString) {
        addInternationalizedMessage(FacesMessage.SEVERITY_FATAL, messageString);
    }
}