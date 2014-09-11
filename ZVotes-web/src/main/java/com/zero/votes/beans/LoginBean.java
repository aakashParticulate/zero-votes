package com.zero.votes.beans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * Only a logout method is needed, login is handled via security-constraints
 * and a LDAP realm.
 */
@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 4864684684864L;
    
    /**
     * Invalides the user's session and sends him back to the index page.
     * @return 
     */
    public String doLogout() {

        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();

        return UrlsPy.LOGOUT.getUrl(true);
    }

}
