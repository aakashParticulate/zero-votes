package com.zero.votes.web.util;

import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

public class ZVotesUtils {

    public static void addInternationalizedMessage(Severity messageType, String messageString) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("com.zero.votes.Locale", context.getViewRoot().getLocale());
        FacesMessage msg = new FacesMessage(bundle.getString(messageString));
        msg.setSeverity(messageType);
        context.addMessage(null, msg);
    }
    
    public static void addInternationalizedMessage(Severity messageType, String messageString, Map<String, String> replaceMap) {
        
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("com.zero.votes.Locale", context.getViewRoot().getLocale());
        String showmsg = bundle.getString(messageString);
        for(String toreplace : replaceMap.keySet()){
            showmsg = showmsg.replace(toreplace, replaceMap.get(toreplace));
        }
        FacesMessage msg = new FacesMessage(showmsg);
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

    public static void addInternationalizedErrorMessage(String messageString, Map<String, String> replaceMap) {
        addInternationalizedMessage(FacesMessage.SEVERITY_ERROR, messageString, replaceMap);
    }

    public static void addInternationalizedFatalMessage(String messageString) {
        addInternationalizedMessage(FacesMessage.SEVERITY_FATAL, messageString);
    }

    public static void throwValidatorException(String messageString) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = ResourceBundle.getBundle("com.zero.votes.Locale", context.getViewRoot().getLocale());
        FacesMessage msg = new FacesMessage(bundle.getString(messageString));
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        context.addMessage(null, msg);
        throw new ValidatorException(msg);
    }
}
