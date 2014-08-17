package com.zero.votes.web;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.zero.votes.beans.UrlsPy;
import com.zero.votes.beans.UserBean;
import com.zero.votes.persistence.PollFacade;
import com.zero.votes.persistence.entities.Organizer;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

@Named("pollController")
@SessionScoped
public class PollController implements Serializable {

    private Poll current;
    private DataModel items = null;
    @EJB
    private com.zero.votes.persistence.PollFacade ejbFacade;
    private PaginationHelper pagination;

    public PollController() {
    }

    public Poll getSelected() {
        if (current == null) {
            current = new Poll();
        }
        return current;
    }

    private PollFacade getFacade() {
        return ejbFacade;
    }
    
    public String publish(Poll poll) {
        poll.setPollState(PollState.STARTED);
        getFacade().edit(poll);
        return UrlsPy.POLL_LIST.getUrl(true);
    }

    public void refresh() {
        Poll updated_current = getFacade().find(current.getId());
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
        return UrlsPy.POLL_LIST.getUrl(true);
    }

    public String prepareView(Poll poll) {
        return "TODO";
    }

    public String prepareCreate() {
        current = new Poll();

        // Add this Organizer to Poll
        FacesContext context = FacesContext.getCurrentInstance();
        UserBean userBean = (UserBean) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
        Organizer current_organizer = userBean.getOrganizer();

        Set<Organizer> organizers = current.getOrganizers();
        organizers.add(current_organizer);
        current.setOrganizers(organizers);

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

    public String prepareEdit(Poll poll) {
        current = poll;
        refresh();
        return UrlsPy.POLL_EDIT.getUrl(true);
    }

    public String update() {
        try {
            getFacade().edit(current);
            ZVotesUtils.addInternationalizedInfoMessage("PollUpdated");
            return UrlsPy.POLL_LIST.getUrl(true);
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String destroy(Poll poll) {
        current = poll;
        performDestroy();
        recreatePagination();
        recreateModel();
        return UrlsPy.POLL_LIST.getUrl(true);
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
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
        return UrlsPy.POLL_LIST.getUrl(true);
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return UrlsPy.POLL_LIST.getUrl(true);
    }

    public String page(int page) {
        getPagination().setPage(page);
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

    public void validateStartDate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Date startDate = (Date) value;
        if ((!this.current.getPollState().equals(PollState.PREPARED)) && (!startDate.equals(this.current.getStartDate()))) {
            ZVotesUtils.throwValidatorException("PollAlreadyStarted");
        }
    }

    public void validateTitle(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String title = (String) value;
        List<Poll> polls_with_title = getFacade().findMultipleBy("title", value);
        int amt_polls_with_title = 0;
        for (Poll poll: polls_with_title) {
            if (!Objects.equals(poll.getId(), current.getId())) {
                amt_polls_with_title++;
            }
        }
        if (amt_polls_with_title >= 1) {
            ZVotesUtils.throwValidatorException("TitleAlreadyUsed");
        }
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
