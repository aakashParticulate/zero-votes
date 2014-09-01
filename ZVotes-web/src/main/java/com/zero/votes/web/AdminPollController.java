package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.PollFacade;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;

@Named("adminPollController")
@SessionScoped
public class AdminPollController implements Serializable {

    private DataModel items = null;
    @EJB
    private com.zero.votes.persistence.PollFacade ejbFacade;
    @EJB
    private com.zero.votes.persistence.TokenFacade tokenFacade;
    private PaginationHelper pagination;

    public AdminPollController() {
    }

    private PollFacade getFacade() {
        return ejbFacade;
    }
    
    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String destroy(Poll poll) {
        performDestroy(poll);
        recreatePagination();
        recreateModel();
        return UrlsPy.ADMIN_POLL_LIST.getUrl(true);
    }

    private void performDestroy(Poll poll) {
        try {
            getFacade().remove(poll);
            ZVotesUtils.addInternationalizedInfoMessage("PollDeleted");
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
        }
    }

    public DataModel getItems() {
        return getPagination().createPageDataModel();
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return UrlsPy.ADMIN_POLL_LIST.getUrl(true);
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return UrlsPy.ADMIN_POLL_LIST.getUrl(true);
    }

    public String page(String page) {
        getPagination().setPage(Integer.valueOf(page));
        recreateModel();
        return UrlsPy.ADMIN_POLL_LIST.getUrl(true);
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Poll getPoll(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Poll.class)
    public static class PollControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AdminPollController controller = (AdminPollController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pollController");
            return controller.getPoll(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Poll) {
                Poll o = (Poll) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Poll.class.getName());
            }
        }

    }

}
