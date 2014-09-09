package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.RecipientFacade;
import com.zero.votes.persistence.entities.Recipient;
import com.zero.votes.persistence.entities.RecipientList;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
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
    @ManagedProperty(value = "#{param.recipientData}")
    private String recipientData;

    private PaginationHelper pagination;

    public RecipientController() {
    }

    public String getRecipientData() {
        return recipientData;
    }

    public void setRecipientData(String recipientData) {
        this.recipientData = recipientData;
    }

    public RecipientList getRecipientList() {
        return recipientList;
    }

    public Recipient getSelected() {
        return current;
    }

    private RecipientFacade getFacade() {
        return ejbFacade;
    }

    public void refresh() {
        Recipient updated_current = getFacade().find(current.getId());
        if (updated_current != null) {
            current = updated_current;
        }
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().countBy("recipientList", recipientList);
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRangeByOrderByEmail("recipientList", recipientList, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList(RecipientList recipientList) {
        this.recipientList = recipientList;
        recreateModel();
        recreatePagination();
        return UrlsPy.RECIPIENT_LIST.getUrl(true);
    }

    public String prepareCreate() {
        return UrlsPy.RECIPIENT_CREATE.getUrl(true);
    }

    public String create() {
        try {
            boolean created = false;
            Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(recipientData);
            while (m.find()) {
                String email = m.group().toLowerCase();
                List<Recipient> dbEntries = getFacade().findAllBy("email", email);
                boolean existInRecipientList = false;
                if (!dbEntries.isEmpty()) {
                    for (Recipient dbEntry : dbEntries) {
                        if (dbEntry.getRecipientList().getId().equals(recipientList.getId())) {
                            existInRecipientList = true;
                        }
                    }
                }
                if (!existInRecipientList) {
                    Recipient recipient = new Recipient();
                    recipient.setRecipientList(recipientList);
                    recipient.setEmail(email);
                    getFacade().create(recipient);
                    created = true;
                }
            }

            if (created) {
                ZVotesUtils.addInternationalizedInfoMessage("RecipientCreated");
            } else {
                ZVotesUtils.addInternationalizedWarnMessage("RecipientNoEmailFound");
            }
            recipientData = "";
            return prepareList(recipientList);
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String prepareEdit(Recipient recipient) {
        current = recipient;
        refresh();
        return UrlsPy.RECIPIENT_EDIT.getUrl(true);
    }

    public String update() {
        try {
            getFacade().edit(current);
            ZVotesUtils.addInternationalizedInfoMessage("RecipientUpdated");
            return UrlsPy.RECIPIENT_LIST.getUrl(true);
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String destroy(Recipient recipient) {
        current = recipient;
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

    public String page(String page) {
        getPagination().setPage(Integer.valueOf(page));
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
    
    public void checkForInstance() throws IOException {
        if (current == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("/account/recipients/");
        }
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
