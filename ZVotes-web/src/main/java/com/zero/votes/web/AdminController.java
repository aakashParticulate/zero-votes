package com.zero.votes.web;

import com.zero.votes.beans.UrlsPy;
import com.zero.votes.beans.UserBean;
import com.zero.votes.persistence.OrganizerFacade;
import com.zero.votes.persistence.entities.Organizer;
import com.zero.votes.web.util.JsfUtil;
import com.zero.votes.web.util.PaginationHelper;
import com.zero.votes.web.util.ZVotesUtils;
import java.io.Serializable;
import java.util.List;
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

@Named("adminController")
@SessionScoped
public class AdminController implements Serializable {

    @EJB
    private com.zero.votes.persistence.OrganizerFacade ejbFacade;
    @EJB
    private com.zero.votes.persistence.PollFacade pollFacade;
    private PaginationHelper pagination;
    private DataModel items = null;
    @ManagedProperty(value="#{param.adminId}")
    private String adminId;

    public AdminController() {
    }

    private OrganizerFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().countBy("admin", 1);
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRangeBy("admin", 1, new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }


    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
    
    public List<Organizer> getPossibleAdmins() {
        return getFacade().findAllBy("admin", 0);
    }

    /**
     * Revoking admin-rights of admin only if admin is not the current user
     * himself.
     * @param admin
     * @return 
     */
    public String remove(Organizer admin) {
        FacesContext context = FacesContext.getCurrentInstance();
        UserBean userBean = (UserBean) context.getApplication().evaluateExpressionGet(context, "#{userBean}", UserBean.class);
        if (admin.getId().equals(userBean.getOrganizer().getId())) {
            ZVotesUtils.addInternationalizedInfoMessage("OrganizerCantRemoveYourself");
        return prepareList();
        }
        admin.setAdmin(false);
        getFacade().edit(admin);

        ZVotesUtils.addInternationalizedInfoMessage("AdminRemoved");
        return prepareList();
    }

    public String prepareList() {
        return UrlsPy.ADMIN_LIST.getUrl(true);
    }

    public String prepareAdd() {
        return UrlsPy.ADMIN_ADD.getUrl(true);
    }
    
    /**
     * Grants admin rights to the user with id = adminid.
     * @return 
     */
    public String addAdmin() {
        Organizer admin = getFacade().find(Long.valueOf(adminId));
        admin.setAdmin(true);
        getFacade().edit(admin);
        
        ZVotesUtils.addInternationalizedInfoMessage("AdminAdded");
        return UrlsPy.ADMIN_LIST.getUrl(true);
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
        return UrlsPy.ADMIN_LIST.getUrl(true);
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return UrlsPy.ADMIN_LIST.getUrl(true);
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

    @FacesConverter(forClass = Organizer.class)
    public static class OrganizerControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AdminController controller = (AdminController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "adminController");
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
