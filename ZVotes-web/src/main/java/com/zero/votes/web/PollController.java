package com.zero.votes.web;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.zero.votes.beans.UrlsPy;
import com.zero.votes.beans.UserBean;
import com.zero.votes.persistence.PollFacade;
import com.zero.votes.persistence.entities.Organizer;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;
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

@Named("pollController")
@SessionScoped
public class PollController implements Serializable {

    private Poll current;
    private DataModel items = null;
    @EJB
    private com.zero.votes.persistence.PollFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public PollController() {
    }

    public Poll getSelected() {
        if (current == null) {
            current = new Poll();
            selectedItemIndex = -1;
        }
        return current;
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

    public String prepareList() {
        recreateModel();
        return UrlsPy.POLL_LIST.getUrl(true);
    }

    public String prepareView() {
        current = (Poll) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Poll();
        
        // Add this Organizer to Poll
        FacesContext context = FacesContext.getCurrentInstance();
        UserBean userBean = (UserBean) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
        Set<Organizer> organizers = current.getOrganizers();
        Organizer current_organizer = userBean.getOrganizer();
        organizers.add(current_organizer);
        current.setOrganizers(organizers);
        
        selectedItemIndex = -1;
        return UrlsPy.POLL_CREATE.getUrl(true);
    }

    public String create() {
        try {
            getFacade().create(current);
            ZVotesUtils.addInternationalizedInfoMessage("PollCreated");
            return prepareList();
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String prepareEdit() {
        current = (Poll) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return UrlsPy.POLL_EDIT.getUrl(true);
    }

    public String update() {
        try {
            getFacade().edit(current);
            ZVotesUtils.addInternationalizedInfoMessage("PollUpdated");
            return "View";
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String destroy() {
        current = (Poll) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return UrlsPy.POLL_LIST.getUrl(true);
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return UrlsPy.POLL_LIST.getUrl(true);
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            ZVotesUtils.addInternationalizedInfoMessage("PollDeleted");
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
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
        return UrlsPy.POLL_LIST.getUrl(true);
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return UrlsPy.POLL_LIST.getUrl(true);
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
            PollController controller = (PollController) facesContext.getApplication().getELResolver().
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
