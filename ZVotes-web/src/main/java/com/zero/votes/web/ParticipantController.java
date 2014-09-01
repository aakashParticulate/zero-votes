package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.beans.UserBean;
import com.zero.votes.persistence.ParticipantFacade;
import com.zero.votes.persistence.entities.Organizer;
import com.zero.votes.persistence.entities.Participant;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.Recipient;
import com.zero.votes.persistence.entities.RecipientList;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
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
    @EJB
    private com.zero.votes.persistence.RecipientListFacade recipientListFacade;
    private PaginationHelper pagination;
    @ManagedProperty(value = "#{param.recipientListId}")
    private String recipientListId;

    public ParticipantController() {
    }

    public Participant getSelected() {
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
                    return getFacade().countBy("poll", poll);
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRangeBy("poll", poll, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String getRecipientListId() {
        return recipientListId;
    }

    public void setRecipientListId(String recipientListId) {
        this.recipientListId = recipientListId;
    }

    public List<RecipientList> getPossibleRecipientLists() {
        FacesContext context = FacesContext.getCurrentInstance();
        UserBean userBean = (UserBean) context.getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
        Organizer current_organizer = userBean.getOrganizer();
        return recipientListFacade.findAllBy("organizer", current_organizer);
    }

    public String prepareList(Poll poll) {
        this.poll = poll;
        recreateModel();
        return UrlsPy.PARTICIPANT_LIST.getUrl(true);
    }

    public String prepareImport() {
        return UrlsPy.PARTICIPANT_IMPORT_LIST.getUrl(true);
    }

    public String importList() {
        boolean imported = false;
        if (recipientListId != null) {
            Long realRecipientListId = Long.valueOf(recipientListId);
            RecipientList recipientList = recipientListFacade.find(realRecipientListId);
            String[] fieldNames = {"email", "poll"};
            for (Recipient recipient : recipientList.getRecipients()) {
                Object[] values = {recipient.getEmail(), this.poll};
                int email_amount = getFacade().countBy(fieldNames, values);
                if (email_amount == 0) {
                    Participant newParticipant = new Participant();
                    newParticipant.setEmail(recipient.getEmail());
                    newParticipant.setPoll(this.poll);
                    getFacade().create(newParticipant);
                    imported = true;
                }
            }
            if (imported) {
                ZVotesUtils.addInternationalizedInfoMessage("ImportedRecipientList");
            } else {
                ZVotesUtils.addInternationalizedWarnMessage("NoNewRecipientImported");
            }
            return prepareList(poll);
        }
        return UrlsPy.PARTICIPANT_IMPORT_LIST.getUrl(true);
    }

    public String prepareCreate() {
        current = new Participant();
        current.setPoll(poll);
        return UrlsPy.PARTICIPANT_CREATE.getUrl(true);
    }

    public String create() {
        try {
            getFacade().create(current);
            ZVotesUtils.addInternationalizedInfoMessage("ParticipantCreated");
            return prepareList(current.getPoll());
        } catch (Exception e) {
            ZVotesUtils.addInternationalizedErrorMessage("PersistenceErrorOccured");
            return null;
        }
    }

    public String prepareEdit(Participant participant) {
        current = participant;
        return UrlsPy.PARTICIPANT_EDIT.getUrl(true);
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

    public Participant getParticipant(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    public void validateEmail(FacesContext context, UIComponent component, Object value) {
        String email = String.valueOf(value);
        String EMAIL_PATTERN
                = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern email_pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = email_pattern.matcher(email);

        List<Participant> participants_with_title = getFacade().findAllBy("email", email);
        int amount_participants_with_email = 0;
        for (Participant participant : participants_with_title) {
            if (!Objects.equals(participant.getId(), current.getId()) && (Objects.equals(participant.getPoll().getId(), current.getPoll().getId()))) {
                amount_participants_with_email++;
            }
        }

        boolean result = true;
        if (email == null) {
            result = false;
        } else if (amount_participants_with_email >= 1) {
            ZVotesUtils.throwValidatorException("EmailAlreadyInList");
            result = false;
        } else if (!matcher.matches()) {
            ZVotesUtils.throwValidatorException("NoValidEmail");
            result = false;
        }
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
