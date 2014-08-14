package com.zero.votes.web;

import com.zero.votes.persistence.ItemFacade;
import com.zero.votes.persistence.entities.Item;
import com.zero.votes.persistence.entities.ItemType;
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

@Named("itemController")
@SessionScoped
public class ItemController implements Serializable {

    private Item current;
    private DataModel items = null;
    @EJB
    private com.zero.votes.persistence.ItemFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public ItemController() {
    }

    public Item getSelected() {
        if (current == null) {
            current = new Item();
            selectedItemIndex = -1;
        }
        return current;
    }

    private ItemFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(1) {

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
        return "item_list.xhtml";
    }

    public String prepareView() {
        current = (Item) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Item();
        selectedItemIndex = -1;
        return "item_create.xhtml";
    }
    
    public String prepareCreateForPoll(Poll poll) {
        current = new Item();
        current.setPoll(poll);
        selectedItemIndex = -1;
        return "item_create.xhtml";
    }

    public String create(Poll poll) {
        try {
            getFacade().create(current);
            ZVotesUtils.addInternationalizedInfoMessage("ItemCreated");
            return prepareList();
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String prepareEdit() {
        current = (Item) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "item_edit.xhtml";
    }

    public String update() {
        try {
            getFacade().edit(current);
            ZVotesUtils.addInternationalizedInfoMessage("ItemUpdated");
            return "View";
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String destroy() {
        current = (Item) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
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
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            ZVotesUtils.addInternationalizedInfoMessage("ItemDeleted");
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
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Item getItem(java.lang.Long id) {
        return ejbFacade.find(id);
    }
    
    public ItemType[] getAvailableItemTypes() {
        return ItemType.values();
    }

    @FacesConverter(forClass = Item.class)
    public static class ItemControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemController controller = (ItemController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemController");
            return controller.getItem(getKey(value));
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
            if (object instanceof Item) {
                Item o = (Item) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Item.class.getName());
            }
        }

    }

}
