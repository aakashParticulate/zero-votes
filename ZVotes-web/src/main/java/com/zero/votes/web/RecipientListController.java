package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.beans.UserBean;
import com.zero.votes.persistence.entities.RecipientList;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.persistence.RecipientListFacade;
import com.zero.votes.persistence.entities.Organizer;
import com.zero.votes.web.util.ZVotesUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

@Named("recipientListController")
@SessionScoped
public class RecipientListController implements Serializable {

    private RecipientList current;
    private DataModel items = null;
    @EJB
    private com.zero.votes.persistence.RecipientListFacade ejbFacade;
    private PaginationHelper pagination;

    public RecipientListController() {
    }

    public RecipientList getSelected() {
        if (current == null) {
            current = new RecipientList();
        }
        return current;
    }

    private RecipientListFacade getFacade() {
        return ejbFacade;
    }
    
    public void refresh() {
        RecipientList updated_current = getFacade().find(current.getId());
        if (updated_current != null) {
            current = updated_current;
        }
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
        return UrlsPy.RECIPIENTLIST_LIST.getUrl(true);
    }

    public String preparePreview(RecipientList recipientList) {
        return "TODO";
    }

    public String prepareCreate() {
        current = new RecipientList();
        
        FacesContext context = FacesContext.getCurrentInstance();
        UserBean userBean = (UserBean) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
        Organizer current_organizer = userBean.getOrganizer();

        current.setOrganizer(current_organizer);
        
        return UrlsPy.RECIPIENTLIST_CREATE.getUrl(true);
    }

    public String create() {
        try {
            getFacade().create(current);
            ZVotesUtils.addInternationalizedInfoMessage("RecipientListCreated");
            return prepareList();
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String prepareEdit(RecipientList recipientList) {
        current = recipientList;
        refresh();
        return UrlsPy.RECIPIENTLIST_EDIT.getUrl(true);
    }

    public String update() {
        try {
            getFacade().edit(current);
            ZVotesUtils.addInternationalizedInfoMessage("RecipientListUpdated");
            return "View";
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String destroy(RecipientList recipientList) {
        current = recipientList;
        performDestroy();
        recreatePagination();
        recreateModel();
        return UrlsPy.RECIPIENTLIST_LIST.getUrl(true);
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            ZVotesUtils.addInternationalizedInfoMessage("RecipientListDeleted");
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
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
        return UrlsPy.RECIPIENTLIST_LIST.getUrl(true);
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return UrlsPy.RECIPIENTLIST_LIST.getUrl(true);
    }

    public String page(String page) {
        getPagination().setPage(Integer.valueOf(page));
        recreateModel();
        return UrlsPy.RECIPIENTLIST_LIST.getUrl(true);
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public RecipientList getRecipientList(java.lang.Long id) {
        return ejbFacade.find(id);
    }
    
    public void validateName(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String name = (String) value;
        if (name.isEmpty()) {
            ZVotesUtils.throwValidatorException("NameNotSet");
        }
        List<RecipientList> recipient_list_with_name = getFacade().findAllBy("name", name);
        int amt_recipient_list_with_name = 0;
        for (RecipientList recipient_list: recipient_list_with_name) {
            if (!Objects.equals(recipient_list.getId(), current.getId())) {
                amt_recipient_list_with_name++;
            }
        }
        if (amt_recipient_list_with_name >= 1) {
            ZVotesUtils.throwValidatorException("NameAlreadyUsed");
        }
    }

    @FacesConverter(forClass = RecipientList.class)
    public static class RecipientListControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RecipientListController controller = (RecipientListController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "recipientListController");
            return controller.getRecipientList(getKey(value));
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
            if (object instanceof RecipientList) {
                RecipientList o = (RecipientList) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + RecipientList.class.getName());
            }
        }

    }

}
