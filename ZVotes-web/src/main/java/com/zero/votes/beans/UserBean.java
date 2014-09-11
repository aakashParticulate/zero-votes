package com.zero.votes.beans;

import com.zero.votes.ldap.LdapLogic;
import com.zero.votes.ldap.LdapUser;
import com.zero.votes.persistence.entities.Organizer;
import java.io.Serializable;
import java.security.Principal;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Offers access to the current user
 */
@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private LdapLogic ldapLogic;
    private LdapUser user;
    private Organizer organizer;

    /**
     * If user hasn't been set, a user is returned from ldapLogic with the id
     * used for login. Else it returns user.
     * @return 
     */
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
    
    public boolean getIsAdmin() {
        if (this.getOrganizer() == null) {
            return false;
        }
        return this.getOrganizer().isAdmin();
    }

    /**
     * If organizer hasn't been set, an organizer is returned from ldapLogic with
     * the id used for login. Else it returns organizer.
     * @return 
     */
    public Organizer getOrganizer() {
        if (organizer == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            Principal p = ec.getUserPrincipal();
            if (p != null) {
                String principalName = p.getName();
                organizer = ldapLogic.getOrganizer(principalName);
            }
        }
        return organizer;
    }

}
