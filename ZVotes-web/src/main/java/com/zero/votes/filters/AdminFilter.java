package com.zero.votes.filters;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.entities.Organizer;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminFilter implements Filter {
    
    @Inject
    private com.zero.votes.ldap.LdapLogic ldapLogic;

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
     public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getUserPrincipal() != null) {
            Organizer user = ldapLogic.getOrganizer(req.getUserPrincipal().getName());
            if (user.isAdmin() && req.isUserInRole("VALIDUSER")) {
                chain.doFilter(request, response);
            } else {
                HttpServletResponse res = (HttpServletResponse) response;
                res.sendRedirect(UrlsPy.LOGIN.getUrl());
            }
        } else {
            HttpServletResponse res = (HttpServletResponse) response;
            res.sendError(404);
//            res.sendRedirect(UrlsPy.LOGIN.getUrl());
        }
    }

    @Override
    public void destroy() {
    }
}
