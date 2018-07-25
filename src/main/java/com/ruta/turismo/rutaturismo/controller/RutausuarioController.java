package com.ruta.turismo.rutaturismo.controller;

import com.ruta.turismo.rutaturismo.Rutausuario;
import com.ruta.turismo.rutaturismo.controller.util.JsfUtil;
import com.ruta.turismo.rutaturismo.controller.util.JsfUtil.PersistAction;
import com.ruta.turismo.rutaturismo.session.RutausuarioFacade;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;

@Named("rutausuarioController")
@SessionScoped
public class RutausuarioController implements Serializable {

    @EJB
    private RutausuarioFacade ejbFacade;
    private List<Rutausuario> items = null;
    private Rutausuario selected;
    
 
    @PostConstruct
    public void init()
    {
        items = null;
    }
    

    public RutausuarioController() {
        items=null;
    }

    public Rutausuario getSelected() {
        return selected;
    }

    public void setSelected(Rutausuario selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private RutausuarioFacade getFacade() {
        return ejbFacade;
    }

    public Rutausuario prepareCreate() {
        selected = new Rutausuario();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RutausuarioCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RutausuarioUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RutausuarioDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Rutausuario> getItems() {
        if (items == null) {
            items = getFacade().rutasAllusuarios();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction == PersistAction.CREATE) {
                     Integer incremento= getFacade().incremento();
                     selected.setIdRutaUsuario(incremento);
                     getFacade().create(selected);
                 }
                 else if (persistAction == PersistAction.UPDATE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Rutausuario getRutausuario(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Rutausuario> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Rutausuario> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
     public void onTabChange(TabChangeEvent event) {
        FacesMessage msg = new FacesMessage("Cambio de Tab", "Activación Tab: " + event.getTab().getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
         
    public void onTabClose(TabCloseEvent event) {
        FacesMessage msg = new FacesMessage("Tab Cerrado", "Cerrado tab: " + event.getTab().getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    
    

    @FacesConverter(forClass = Rutausuario.class)
    public static class RutausuarioControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RutausuarioController controller = (RutausuarioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rutausuarioController");
            return controller.getRutausuario(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Rutausuario) {
                Rutausuario o = (Rutausuario) object;
                return getStringKey(o.getIdRutaUsuario());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Rutausuario.class.getName()});
                return null;
            }
        }

    }

}
