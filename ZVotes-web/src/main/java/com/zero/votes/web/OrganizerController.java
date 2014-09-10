package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.beans.UserBean;
import com.zero.votes.persistence.OrganizerFacade;
import com.zero.votes.persistence.entities.Organizer;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
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

@Named("organizerController")
@SessionScoped
public class OrganizerController implements Serializable {

    private Organizer current;
    @EJB
    private com.zero.votes.persistence.OrganizerFacade ejbFacade;
    @EJB
    private com.zero.votes.persistence.PollFacade pollFacade;
    private PaginationHelper pagination;
    private Poll poll;
    private DataModel items = null;
    @ManagedProperty(value="#{param.organizerId}")
    private String organizerId;

    public OrganizerController() {
    }

    public Organizer getSelected() {
        return current;
    }

    private OrganizerFacade getFacade() {
        return ejbFacade;
    }

    public void refresh() {
        Organizer updated_current = getFacade().find(current.getId());
        if (updated_current != null) {
            current = updated_current;
        }
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().countBy("polls", poll.getId());
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRangeBy("polls", poll.getId(), new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public Poll getPoll() {
        return poll;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }
    
    public List<Organizer> getPossibleOrganizers() {
        return getFacade().findAll();
    }

    public String remove(Organizer organizer) {
        FacesContext context = FacesContext.getCurrentInstance();
        UserBean userBean = (UserBean) context.getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
        if (organizer.getId().equals(userBean.getOrganizer().getId())) {
            ZVotesUtils.addInternationalizedInfoMessage("OrganizerCantRemoveYourself");
            return prepareList(poll);
        }

        List<Organizer> poll_organizers = poll.getOrganizers();
        poll_organizers.remove(organizer);
        poll.setOrganizers(poll_organizers);
        pollFacade.edit(poll);

        ZVotesUtils.addInternationalizedInfoMessage("OrganizerRemoved");
        return prepareList(poll);
    }

    public String prepareList(Poll poll) {
        this.poll = poll;
        return UrlsPy.ORGANIZER_LIST.getUrl(true);
    }

    public String prepareAdd() {
        return UrlsPy.ORGANIZER_ADD.getUrl(true);
    }
    
    public String addOrganizer() {
        Organizer organizer = getFacade().find(Long.valueOf(organizerId));
        
        List<Organizer> poll_organizers = poll.getOrganizers();
        if (!poll_organizers.contains(organizer)) {
            poll_organizers.add(organizer);
            poll.setOrganizers(poll_organizers);
            pollFacade.edit(poll);
            ZVotesUtils.addInternationalizedInfoMessage("OrganizerAdded");
        } else {
            ZVotesUtils.addInternationalizedInfoMessage("OrganizerAlreadyPresent");
        }
        return prepareList(poll);
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
        return UrlsPy.ORGANIZER_LIST.getUrl(true);
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return UrlsPy.ORGANIZER_LIST.getUrl(true);
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Organizer getOrganizer(java.lang.Long id) {
        return ejbFacade.find(id);
    }
    
    public void checkForInstance() throws IOException {
        if (current == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("/account/organizers/");
        }
    }
    
    public void checkForPoll() throws IOException {
        if (poll == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("/account/polls/");
        }
    }

    @FacesConverter(forClass = Organizer.class)
    public static class OrganizerControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            OrganizerController controller = (OrganizerController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "organizerController");
            return controller.getOrganizer(getKey(value));
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
            if (object instanceof Organizer) {
                Organizer o = (Organizer) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Organizer.class.getName());
            }
        }

    }

}
