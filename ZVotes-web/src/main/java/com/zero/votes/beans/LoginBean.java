/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zero.votes.beans;

import java.io.Serializable; 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
 
 
@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {
 
    private static final long serialVersionUID = 4864684684864L;
     
    public String doLogout() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();
        
        return UrlsPy.LOGOUT.getUrl(true);
    }

}
