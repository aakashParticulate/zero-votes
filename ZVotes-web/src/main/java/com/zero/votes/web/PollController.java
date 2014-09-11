package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.beans.UserBean;
import com.zero.votes.cronjobs.TaskManager;
import com.zero.votes.persistence.PollFacade;
import com.zero.votes.persistence.entities.Item;
import com.zero.votes.persistence.entities.ItemType;
import com.zero.votes.persistence.entities.Organizer;
import com.zero.votes.persistence.entities.Participant;
import com.zero.votes.persistence.entities.Poll;
import com.zero.votes.persistence.entities.PollState;
import com.zero.votes.persistence.entities.Token;
import com.zero.votes.web.util.EMailer;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named("pollController")
@SessionScoped
public class PollController implements Serializable {

    private Poll current;
    @EJB
    private com.zero.votes.persistence.PollFacade ejbFacade;
    @EJB
    private com.zero.votes.persistence.TokenFacade tokenFacade;
    @EJB
    private com.zero.votes.persistence.ItemFacade itemFacade;
    @EJB
    private com.zero.votes.persistence.ParticipantFacade participantFacade;
    
    @EJB
    private TaskManager taskManager;
    
    private PaginationHelper pagination;
    
    // Mail delivery
    @EJB
    private EMailer eMailer;

    public PollController() {
    }

    public Poll getSelected() {
        return current;
    }

    private PollFacade getFacade() {
        return ejbFacade;
    }
    
    public String publish(Poll poll) {
        // validate Poll to see if it's ready to get public
        if (validate(poll)) {
            poll.setPollState(PollState.PUBLISHED);
            getFacade().edit(poll);
            // Start Tasks to set the Poll to state started and finished on the according dates
            taskManager.createStartPollTask(poll, getFacade());
            taskManager.createFinishPollTask(poll, getFacade());
            Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
            
            // create token for every participant
            for(Participant participant: poll.getParticipants()) {
                Token token = new Token();
                while (tokenFacade.countBy("tokenString", token.getTokenString()) > 0) {
                    token = new Token();
                }
                token.setPoll(poll);
                tokenFacade.create(token);
                
                // only link if participationTracking is enabled
                if (poll.isParticipationTracking()) {
                    participant.setToken(token);
                    participantFacade.edit(participant);
                }
            }
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
            if (poll.isParticipationTracking()) {
                taskManager.createReminderMailTask(poll, eMailer, locale, url);
            }
            taskManager.createStartedMailTask(poll, eMailer, locale, url);
            ZVotesUtils.addInternationalizedInfoMessage("PollPublishedSuccessfully");
            return UrlsPy.POLL_LIST.getUrl(true);
        }
        else {
            return UrlsPy.POLL_LIST.getUrl(true);
        }
    }
    
    public boolean validate(Poll poll) {
        boolean result = true;
        if (poll.getItems().isEmpty()) {
            ZVotesUtils.addInternationalizedErrorMessage("PollNoItem");
            result = false;
        }
        if (poll.getStartDate() == null) {
            ZVotesUtils.addInternationalizedErrorMessage("StartDateNotSet");
            result = false;
        }
        if (poll.getEndDate() == null) {
            ZVotesUtils.addInternationalizedErrorMessage("EndDateNotSet");
            result = false;
        }
        if (poll.getEndDate().before(poll.getStartDate())) {
            ZVotesUtils.addInternationalizedErrorMessage("EndDateBeforeStartDate");
            result = false;
        }
        if (poll.getTitle().isEmpty()) {
            ZVotesUtils.addInternationalizedErrorMessage("TitleNotSet");
            result = false;
        }
        if (poll.getDescription().isEmpty()) {
            ZVotesUtils.addInternationalizedErrorMessage("DescriptionNotSet");
            result = false;
        }
        if (poll.getParticipants().size() < 3) {
            ZVotesUtils.addInternationalizedErrorMessage("PollLessThan3Participants");
            result = false;
        }
        for (Item item : poll.getItems()) {
            if (item.getType().equals(ItemType.YES_NO)) {
                item.setOwnOptions(false);
                itemFacade.edit(item);
            }
            if (item.getOptions().size() < 2 && !item.isOwnOptions()) {
                //replacing item's title
                HashMap<String, String> replaceMap = new HashMap<>();
                replaceMap.put("$item$", item.getTitle());
                ZVotesUtils.addInternationalizedErrorMessage("AnItemNeedsAtLeast2Options", replaceMap);
                result = false;
            }
            if (item.getOptions().size() < item.getM() && !item.isOwnOptions()) {
                HashMap<String, String> replaceMap = new HashMap<>();
                replaceMap.put("$item$", item.getTitle());
                ZVotesUtils.addInternationalizedErrorMessage("AnItemOptionsThanM", replaceMap);
                result = false;
            }
        }
        return result;
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
                    FacesContext context = FacesContext.getCurrentInstance();
                    UserBean userBean = (UserBean) context.getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
                    Organizer current_organizer = userBean.getOrganizer();
                    return getFacade().countBy("organizers", current_organizer.getId());
                }

                @Override
                public DataModel createPageDataModel() {
                    FacesContext context = FacesContext.getCurrentInstance();
                    UserBean userBean = (UserBean) context.getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
                    Organizer current_organizer = userBean.getOrganizer();
                    return new ListDataModel(getFacade().findRangeBy("organizers", current_organizer.getId(), new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        return UrlsPy.POLL_LIST.getUrl(true);
    }

    public String prepareCreate() {
        current = new Poll();

        // Add this Organizer to Poll
        FacesContext context = FacesContext.getCurrentInstance();
        UserBean userBean = (UserBean) context.getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
        Organizer current_organizer = userBean.getOrganizer();

        List<Organizer> organizers = current.getOrganizers();
        if (!organizers.contains(current_organizer)) {
            organizers.add(current_organizer);
            current.setOrganizers(organizers);
        }
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

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        return UrlsPy.POLL_LIST.getUrl(true);
    }

    public String previous() {
        getPagination().previousPage();
        return UrlsPy.POLL_LIST.getUrl(true);
    }

    public String page(String page) {
        getPagination().setPage(Integer.valueOf(page));
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
        if (startDate == null) {
            ZVotesUtils.throwValidatorException("StartDateNotSet");
        }
    }

    public void validateEndDate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Date endDate = (Date) value;
        if (endDate == null) {
            ZVotesUtils.throwValidatorException("EndDateNotSet");
        }
        UIInput startDateComponent = (UIInput) (context.getViewRoot().findComponent("pollEditForm:startDate"));
	Date startDate = (Date) startDateComponent.getValue();
        if (startDate.after(endDate)) {
            ZVotesUtils.throwValidatorException("EndDateBeforeStartDate");
        }
        if (endDate.before(new Date())) {
            ZVotesUtils.throwValidatorException("EndDateBeforeNow");
        }
        if (current.getId() != null) {
            Date previousEndDate = getFacade().find(current.getId()).getEndDate();
            if ((current.getPollState().equals(PollState.PUBLISHED) || current.getPollState().equals(PollState.STARTED) || current.getPollState().equals(PollState.VOTING)) && endDate.before(previousEndDate)) {
                ZVotesUtils.throwValidatorException("EndDateCantBeMovedToEarlierTime");
            }
        }
    }

    public void validateTitle(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String title = (String) value;
        if (title.isEmpty()) {
            ZVotesUtils.throwValidatorException("TitleNotSet");
        }
        List<Poll> polls_with_title = getFacade().findAllBy("title", title);
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

    public void validateDescription(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String description = (String) value;
        if (description.isEmpty()) {
            ZVotesUtils.throwValidatorException("DescriptionNotSet");
        }
    }
    
    public void checkForInstance() throws IOException {
        if (current == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("/account/polls/");
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
