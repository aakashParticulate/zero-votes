package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.RecipientFacade;
import com.zero.votes.persistence.entities.Recipient;
import com.zero.votes.persistence.entities.RecipientList;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.Set;
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

@Named("recipientController")
@SessionScoped
public class RecipientController implements Serializable {

    private Recipient current;
    private DataModel items = null;
    @EJB
    private com.zero.votes.persistence.RecipientFacade ejbFacade;
    private RecipientList recipientList;
    private PaginationHelper pagination;

    public RecipientController() {
    }

    public Recipient getSelected() {
        if (current == null) {
            current = new Recipient();
        }
        return current;
    }

    private RecipientFacade getFacade() {
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

    public String prepareList(RecipientList recipientList) {
        this.recipientList = recipientList;
        recreateModel();
        return UrlsPy.RECIPIENT_LIST.getUrl(true);
    }

    public String prepareCreate() {
        current = new Recipient();
        //current.setRecipientLists(recipientList);
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            ZVotesUtils.addInternationalizedInfoMessage("RecipientCreated");
            return prepareCreate();
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String prepareEdit() {
        current = (Recipient) getItems().getRowData();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            ZVotesUtils.addInternationalizedInfoMessage("RecipientUpdated");
            return "View";
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String destroy() {
        current = (Recipient) getItems().getRowData();
        performDestroy();
        recreatePagination();
        recreateModel();
        return UrlsPy.RECIPIENT_LIST.getUrl(true);
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            ZVotesUtils.addInternationalizedInfoMessage("RecipientDeleted");
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
        return UrlsPy.RECIPIENT_LIST.getUrl(true);
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return UrlsPy.RECIPIENT_LIST.getUrl(true);
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Recipient getRecipient(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Recipient.class)
    public static class RecipientControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RecipientController controller = (RecipientController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "recipientController");
            return controller.getRecipient(getKey(value));
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
            if (object instanceof Recipient) {
                Recipient o = (Recipient) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Recipient.class.getName());
            }
        }

    }

}
