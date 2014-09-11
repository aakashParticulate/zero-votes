package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.ItemOptionFacade;
import com.zero.votes.persistence.entities.Item;
import com.zero.votes.persistence.entities.ItemOption;
import com.zero.votes.persistence.entities.ItemType;
import com.zero.votes.persistence.entities.PollState;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

@Named("itemOptionController")
@SessionScoped
public class ItemOptionController implements Serializable {

    private ItemOption current;
    private Item item;
    @EJB
    private com.zero.votes.persistence.ItemOptionFacade ejbFacade;
    @Inject
    private com.zero.votes.web.ItemController itemController;

    public ItemOptionController() {
    }

    public ItemOption getSelected() {
        return current;
    }

    private ItemOptionFacade getFacade() {
        return ejbFacade;
    }

    public void refresh() {
        ItemOption updated_current = getFacade().find(current.getId());
        if (updated_current != null) {
            current = updated_current;
        }
    }
    public String prepareList(Item item) {
        this.item = item;
        return itemController.prepareEdit(item);
    }

    public String prepareCreate(Item item) {
        if (item.getPoll().getPollState().equals(PollState.PREPARING)) {
            this.item = item;
            current = new ItemOption();
            current.setItem(item);
            return UrlsPy.ITEM_OPTION_CREATE.getUrl(true);
        } else {
            ZVotesUtils.addInternationalizedWarnMessage("PollAlreadyPublished");
            return prepareList(item);
        }
    }

    public String create() {
        if (item.getPoll().getPollState().equals(PollState.PREPARING)) {
            try {
                if (this.item.getType().equals(ItemType.YES_NO)) {
                    ZVotesUtils.addInternationalizedErrorMessage("ItemIsYesNo");
                } else {
                    getFacade().create(current);
                    ZVotesUtils.addInternationalizedInfoMessage("ItemOptionCreated");
                }
                return prepareList(item);
            } catch (Exception e) {
                ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
                return null;
            }
        } else {
            ZVotesUtils.addInternationalizedWarnMessage("PollAlreadyPublished");
            return prepareList(item);
        }
    }

    public String prepareEdit(ItemOption itemOption) {
        if (itemOption.getItem().getPoll().getPollState().equals(PollState.PREPARING)) {
            current = itemOption;
            this.item = current.getItem();
            refresh();
            return UrlsPy.ITEM_OPTION_EDIT.getUrl(true);
        } else {
            ZVotesUtils.addInternationalizedWarnMessage("PollAlreadyPublished");
            return prepareList(itemOption.getItem());
        }
    }

    public String update() {
        if (current.getItem().getPoll().getPollState().equals(PollState.PREPARING)) {
            try {
                item = current.getItem();
                if (item.getType().equals(ItemType.YES_NO)) {
                    ZVotesUtils.addInternationalizedErrorMessage("ItemIsYesNo");
                } else {
                    getFacade().edit(current);
                    ZVotesUtils.addInternationalizedInfoMessage("ItemOptionUpdated");
                }
                return prepareList(item);
            } catch (Exception e) {
                ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
                return null;
            }
        } else {
            ZVotesUtils.addInternationalizedWarnMessage("PollAlreadyPublished");
            return prepareList(current.getItem());
        }
    }
    
    public void validateShortName(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String shortName = (String) value;
        if (shortName.isEmpty()) {
            ZVotesUtils.throwValidatorException("ShortNameNotSet");
        }
        String[] fieldNames = {"shortName", "item"};
        Object[] values = {shortName, item};
        List<ItemOption> itemOptionsWithTitle = getFacade().findAllBy(fieldNames, values);
        int amountItemOptionsWithTitle = 0;
        for (ItemOption itemOption: itemOptionsWithTitle) {
            if (!Objects.equals(itemOption.getId(), current.getId())) {
                amountItemOptionsWithTitle++;
            }
        }
        if (amountItemOptionsWithTitle >= 1) {
            ZVotesUtils.throwValidatorException("ShortNameAlreadyUsed");
        }
    }

    public String destroy(ItemOption itemOption) {
        Item item = itemOption.getItem();
        if (item.getType().equals(ItemType.YES_NO)) {
            ZVotesUtils.addInternationalizedErrorMessage("ItemIsYesNo");
        } else {
            current = itemOption;
            performDestroy();
        }
        return prepareList(item);
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            ZVotesUtils.addInternationalizedInfoMessage("ItemOptionDeleted");
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
        }
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public ItemOption getItemOption(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    public Item getItem() {
        return this.item;
    }
    
    public void checkForInstance() throws IOException {
        if (current == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("/account/item/edit/");
        }
    }

    @FacesConverter(forClass = ItemOption.class)
    public static class ItemOptionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemOptionController controller = (ItemOptionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemOptionController");
            return controller.getItemOption(getKey(value));
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
            if (object instanceof ItemOption) {
                ItemOption o = (ItemOption) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ItemOption.class.getName());
            }
        }

    }

}
