package com.zero.votes.beans;

import com.zero.votes.ldap.LdapLogic;
import com.zero.votes.ldap.LdapUser;
import java.io.Serializable;
import java.security.Principal;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private LdapLogic ldapLogic;
    private LdapUser user;

    public LdapUser getUser() {
        if (user == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            Principal p = ec.getUserPrincipal();
            if (p != null) {
                String principalName = p.getName();
                user = ldapLogic.getUser(principalName);
            }
        }
        return user;
    }

}
