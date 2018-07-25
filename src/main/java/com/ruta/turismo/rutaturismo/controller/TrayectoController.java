package com.ruta.turismo.rutaturismo.controller;

import com.ruta.turismo.rutaturismo.Ruta;
import com.ruta.turismo.rutaturismo.Trayecto;
import com.ruta.turismo.rutaturismo.controller.util.JsfUtil;
import com.ruta.turismo.rutaturismo.controller.util.JsfUtil.PersistAction;
import com.ruta.turismo.rutaturismo.session.TrayectoFacade;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.map.Circle;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polyline;

@Named("trayectoController")
@ViewScoped
public class TrayectoController implements Serializable {

    @EJB
    private com.ruta.turismo.rutaturismo.session.TrayectoFacade ejbFacade;
    private List<Trayecto> items = null;
    private List<Trayecto> trayectosruta = null;
    private Trayecto selected;
    @Inject
    private RutaController rutaController;
    @Inject
    private RutausuarioController rutausuarioController;

    private MapModel emptyModel;
   private Marker marker;
   private String colordefecto="#FF9900";
     private StreamedContent file;

    private double lat;

    private double lng;

    public TrayectoController() {
    }

    public Trayecto getSelected() {
        return selected;
    }

    public void setSelected(Trayecto selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public RutaController getRutaController() {
        return rutaController;
    }

    public void setRutaController(RutaController rutaController) {
        this.rutaController = rutaController;
    }

    private TrayectoFacade getFacade() {
        return ejbFacade;
    }

    public void addMarker() {
        lat = selected.getLatitud();
        lng = selected.getLongitud();
        LatLng coord1=new LatLng(lat, lng);
       marker = new Marker(coord1, selected.getDescripcion(),selected);

        emptyModel.addOverlay(marker);
        Polyline polyline = new Polyline();
        polyline.getPaths().add(coord1);
        polyline.setStrokeWeight(4);
        polyline.setStrokeColor("#FF9900");
        polyline.setStrokeOpacity(0.5);

                emptyModel.addOverlay(polyline);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Added", "Lat:" + lat + ", Lng:" + lng));
    }
    public void onMarkerSelect(OverlaySelectEvent event) {
        marker = (Marker) event.getOverlay();
        selected = (Trayecto)marker.getData();
    }
    public void cargapuntos() {
        emptyModel = new DefaultMapModel();
        
        Polyline polyline = new Polyline();
        if (trayectosruta != null) {
            int inicio=0;
            int fin=trayectosruta.size();
            int cont=0;
            for (Trayecto t : trayectosruta) {
                LatLng coord1=null;
                
                coord1 = new LatLng(t.getLatitud(), t.getLongitud());
                 //Circle
       
 
        
                if(inicio==cont)
                    {
                        System.out.println(fin+" INICIO "+cont); 
                         Circle circle1 = new Circle(coord1, 100);
                        circle1.setStrokeColor("#d93c3c");
                        circle1.setFillColor("#d93c3c");
                        circle1.setFillOpacity(0.5);
                        
                        emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()+" - DIRECCIÓN: "+t.getDireccion()+" - COSTO:"+t.getCostovisita(),t,"https://maps.google.com/mapfiles/ms/micons/blue-dot.png"));
                          emptyModel.addOverlay(circle1);
                    }
                if (cont>0 && cont< fin-1)
                    {
                         System.out.println(fin+" MEDIO "+cont); 

                         emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()+" - DIRECCIÓN: "+t.getDireccion()+" - COSTO:"+t.getCostovisita(),t));   
                    }
                 if(fin-1==cont)
                    {
                         System.out.println(fin+" FIN "+cont); 
                         Circle circle2 = new Circle(coord1, 100);
                        circle2.setStrokeColor("#ffcc00");
                        circle2.setFillColor("#ffcc00");
                        circle2.setStrokeOpacity(0.5);
                         circle2.setFillOpacity(0.5);
                        


                        emptyModel.addOverlay(circle2);
                       emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()+" - DIRECCIÓN: "+t.getDireccion()+" - COSTO:"+t.getCostovisita(),t,"https://maps.google.com/mapfiles/ms/micons/yellow-dot.png")); 
                    }
                 
                polyline.getPaths().add(coord1);
                polyline.setStrokeWeight(4);
               if(rutaController.getSelected()!=null)
               {
                            
                if(rutaController.getSelected().getColor()!=null)
                {
                    polyline.setStrokeColor("#"+rutaController.getSelected().getColor());
                }
                else
                {
                     polyline.setStrokeColor(colordefecto);
                }
               }
               else
                {
                     polyline.setStrokeColor(colordefecto);
                }
                polyline.setStrokeOpacity(0.4);

                emptyModel.addOverlay(polyline);
                 
                cont++;
                    
                    
                
            }
        }

    }
 

    public Trayecto prepareCreate() {

        trayectosruta = ejbFacade.trayectos(rutaController.getSelected());
        cargapuntos();
        
        selected = new Trayecto();
        selected.setIdRuta(rutaController.getSelected());
        selected.setTipotrayecto("D");// tipo de proyecto creados por administraodr
        initializeEmbeddableKey();

        return selected;
    }
     public Trayecto prepareRutaUsuario() {
        
        trayectosruta = ejbFacade.trayectosusuario(rutausuarioController.getSelected());
        cargapuntos();
      
        selected = new Trayecto();
        initializeEmbeddableKey();

        return selected;
    }

    public Trayecto selectednull() {
        selected = null;

        return selected;
    }

    public void create() {
        addMarker();
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TrayectoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TrayectoUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TrayectoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Trayecto> getItems() {
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
                    Integer incremento = getFacade().incremento();
                    selected.setIdTrayecto(incremento);
                    getFacade().create(selected);
                } else if (persistAction == PersistAction.UPDATE) {
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
                            getSelected().setImgtrayecto(null);
                             
                               JsfUtil.addErrorMessage("Sólo se permite subir imagenes con una resolución de 500x500px.");
                             JsfUtil.isValidationFailed();
                              
                        }
                        else
                        {
                             getSelected().setImgtrayecto(contents);
                          
                        
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
    
    public void downloadFoto(Trayecto trayecto){
        
       
          setFile(null);
           StreamedContent archivoRecuperado=null;
         if(trayecto!=null)
         {
             if(trayecto.getImgtrayecto()!=null)
             {
               archivoRecuperado = new DefaultStreamedContent(new ByteArrayInputStream(trayecto.getImgtrayecto()), ".jpg", "imagen.jpg");
               setFile(archivoRecuperado);
             
              
             }
              else
                {

                          JsfUtil.addErrorMessage("No existe Foto para esta trayecto...");
                }
              
                 
         }
         else
         {
                       
                   JsfUtil.addErrorMessage("No h seleccionado una ruta..");
         }
       }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }
    
          

    public Trayecto getTrayecto(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Trayecto> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Trayecto> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public MapModel getEmptyModel() {
        return emptyModel;
    }

    public void setEmptyModel(MapModel emptyModel) {
        this.emptyModel = emptyModel;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public List<Trayecto> getTrayectosruta() {
        return trayectosruta;
    }

    public void setTrayectosruta(List<Trayecto> trayectosruta) {
        this.trayectosruta = trayectosruta;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
    

    @FacesConverter(forClass = Trayecto.class)
    public static class TrayectoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TrayectoController controller = (TrayectoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "trayectoController");
            return controller.getTrayecto(getKey(value));
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
            if (object instanceof Trayecto) {
                Trayecto o = (Trayecto) object;
                return getStringKey(o.getIdTrayecto());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Trayecto.class.getName()});
                return null;
            }
        }

    }

}
