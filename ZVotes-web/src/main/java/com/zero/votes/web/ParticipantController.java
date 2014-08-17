package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.persistence.ParticipantFacade;
import com.zero.votes.persistence.entities.Participant;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.Serializable;
import java.util.ResourceBundle;
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

@Named("participantController")
@SessionScoped
public class ParticipantController implements Serializable {

    private Participant current;
    private Poll poll;
    private DataModel items = null;
    @EJB
    private com.zero.votes.persistence.ParticipantFacade ejbFacade;
    private PaginationHelper pagination;

    public ParticipantController() {
    }

    public Participant getSelected() {
        if (current == null) {
            current = new Participant();
        }
        return current;
    }

    private ParticipantFacade getFacade() {
        return ejbFacade;
    }

    public void refresh() {
        Participant updated_current = getFacade().find(current.getId());
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
                    return new ListDataModel(getFacade().findRangeBy("poll", poll, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList(Poll poll) {
        this.poll = poll;
        recreateModel();
        return UrlsPy.PARTICIPANT_LIST.getUrl();
    }

    public String prepareView() {
        return "TODO";
    }

    public String prepareCreate(Poll poll) {
        current = new Participant();
        current.setPoll(poll);
        return UrlsPy.PARTICIPANT_CREATE.getUrl();
    }

    public String create(Participant participant) {
        current = participant;
        try {
            getFacade().create(current);
            ZVotesUtils.addInternationalizedInfoMessage("ParticipantCreated");
            return prepareList(poll);
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String prepareEdit(Participant participant) {
        current = participant;
        return UrlsPy.PARTICIPANT_EDIT.getUrl();
    }

    public String update(Participant participant) {
        current = participant;
        try {
            getFacade().edit(current);
            ZVotesUtils.addInternationalizedInfoMessage("ParticipantUpdated");
            return prepareList(poll);
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String destroy(Participant participant) {
        current = participant;
        performDestroy();
        recreatePagination();
        return prepareList(poll);
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            ZVotesUtils.addInternationalizedInfoMessage("ParticipantDeleted");
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

    public Participant getParticipant(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Participant.class)
    public static class ParticipantControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ParticipantController controller = (ParticipantController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "participantController");
            return controller.getParticipant(getKey(value));
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
            if (object instanceof Participant) {
                Participant o = (Participant) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Participant.class.getName());
            }
        }

    }

}
