package com.ruta.turismo.rutaturismo.controller;

import com.ruta.turismo.rutaturismo.Ruta;
import com.ruta.turismo.rutaturismo.controller.util.JsfUtil;
import com.ruta.turismo.rutaturismo.controller.util.JsfUtil.PersistAction;
import com.ruta.turismo.rutaturismo.session.RutaFacade;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.inject.Named;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named("rutaController")
@ViewScoped
public class RutaController implements Serializable {

    @Inject
    private RutaFacade ejbFacade;
    private List<Ruta> items = null;
    private Ruta selected;
     private StreamedContent file;

    public RutaController() {
    }

    public Ruta getSelected() {
        return selected;
    }

    public void setSelected(Ruta selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private RutaFacade getFacade() {
        return ejbFacade;
    }

    public Ruta prepareCreate() {
        selected = new Ruta();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RutaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
       
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RutaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RutaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Ruta> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                 if (persistAction == PersistAction.CREATE) {
                     Integer incremento= getFacade().incremento();
                     selected.setIdRuta(incremento);
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
    public void addImagen(FileUploadEvent event) {
//        System.out.println("INGRESA addImagen ");
        
        byte[] contents = event.getFile().getContents();        
      
        if(event.getFile().getContentType().equals("image/jpeg") )
        {
            if(event.getFile().getSize()<1048576)
            {
                if(getSelected()!=null)
                {
       
                    try (InputStream is = event.getFile().getInputstream()) 
                    {
                        BufferedImage bi = ImageIO.read(is);
                        if (bi.getWidth() > 500 || bi.getHeight() > 500) {
                            getSelected().setImgruta(null);
                             
                               JsfUtil.addErrorMessage("Sólo se permite subir imagenes con una resolución de 500x500px.");
                             JsfUtil.isValidationFailed();
                              
                        }
                        else
                        {
                             getSelected().setImgruta(contents);
                          
                        
                          JsfUtil.addSuccessMessage("Se ha subido la foto satisfactoriamente, por favor guarde los cambios");
                          JsfUtil.addSuccessMessage("En caso de no visualizar la foto por favor actualice la pantalla..");
                        }

                    } catch (IOException ex) {
                         JsfUtil.addErrorMessage("No se ha podido cargar la imagen.");
                       JsfUtil.isValidationFailed();
                        
                    }
                
                }
            }
            else
            {
              String    msg = "Error al cargar, Tamaño Inválido 1MB, por favor revise";
              JsfUtil.addErrorMessage( msg);
            }
        }
        else
        {
                String    msg = "Tamaño Inválido o Extensión Inválida, por favor revise";
                 JsfUtil.addErrorMessage( msg);
        }
    }
    
    public void downloadFoto(Ruta ruta){
        
       
         
         if(ruta!=null)
         {
             if(ruta.getImgruta()!=null)
             {
                  StreamedContent archivoRecuperado = new DefaultStreamedContent(new ByteArrayInputStream(ruta.getImgruta()), ".jpg", "imagen.jpg");
               setFile(archivoRecuperado);
             }
              else
         {
                       
                   JsfUtil.addErrorMessage("No existe Foto para esta Ruta...");
         }
              
                 
         }
         else
         {
                       
                   JsfUtil.addErrorMessage("No h seleccionado una ruta..");
         }
       }
          
      
          
          
     

    public Ruta getRuta(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Ruta> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Ruta> getItemsAvailableSelectOne() {
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

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }
    

    @FacesConverter(forClass = Ruta.class)
    public static class RutaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RutaController controller = (RutaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rutaController");
            return controller.getRuta(getKey(value));
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
            if (object instanceof Ruta) {
                Ruta o = (Ruta) object;
                return getStringKey(o.getIdRuta());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Ruta.class.getName()});
                return null;
            }
        }

    }

}
