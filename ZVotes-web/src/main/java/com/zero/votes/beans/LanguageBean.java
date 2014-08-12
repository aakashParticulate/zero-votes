package com.zero.votes.beans;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
 
@ManagedBean(name="language")
@SessionScoped
public class LanguageBean implements Serializable {
 
    private static final long serialVersionUID = 1L;

    private Locale currentLocale;
    private String localeCode;

    private static Map<String,Locale> availableLocales;
    static {
        availableLocales = new LinkedHashMap<String,Locale>();
        availableLocales.put("Deutsch", Locale.GERMAN);
        availableLocales.put("English", Locale.ENGLISH);
    }

    public Map<String, Locale> getAvailableLocales() {
        return this.availableLocales;
    }

    public String getLocaleCode() {
        if (this.localeCode == null) {
            this.localeCode = this.getCurrentLocale().getLanguage();
        }
        return this.localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public Locale getCurrentLocale() {
        if (this.currentLocale == null) {
            this.currentLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
        }
        if (this.currentLocale == null) {
            this.currentLocale = Locale.GERMAN;
        }
        return this.currentLocale;
    }

    public void setLocale(Locale locale) {
        this.currentLocale = locale;
    }

    public void localeChanged(ValueChangeEvent e) {

        String newLocaleValue = e.getNewValue().toString();

        for (Map.Entry<String, Locale> entry : this.availableLocales.entrySet()) {

           if (entry.getValue().toString().equals(newLocaleValue)) {
                FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale)entry.getValue());
          }
       }
    }
}
