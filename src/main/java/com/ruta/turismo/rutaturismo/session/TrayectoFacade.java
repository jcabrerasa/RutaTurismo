/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruta.turismo.rutaturismo.session;

import com.ruta.turismo.rutaturismo.Trayecto;
import com.ruta.turismo.rutaturismo.Ruta;
import com.ruta.turismo.rutaturismo.Rutausuario;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.eclipse.persistence.config.QueryHints;

/**
 *
 * @author cuer
 */
@Stateless
public class TrayectoFacade extends AbstractFacade<Trayecto> {

    @PersistenceContext(unitName = "com.ruta.turismo_RutaTurismo_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TrayectoFacade() {
        super(Trayecto.class);
    }
    public Integer incremento()
    {
    
      String jpqlQuery = "SELECT LAST_INSERT_ID()";
      Query query = this.em.createNativeQuery(jpqlQuery);
       Integer autoincrementoMysql = Integer.parseInt(String.valueOf(query.getSingleResult()));
      return autoincrementoMysql;
    
    }
    public List<Trayecto> trayectos(Ruta ruta)
    {
            Query query = this.em.createQuery("select c from Trayecto c where c.idRuta = :ruta and c.tipotrayecto = 'D' order by c.orden");
//              query.setHint("javax.persistence.cache.storeMode", "REFRESH");
                   query.setHint(QueryHints.REFRESH, true);
		try {
                    query.setParameter("ruta", ruta);
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
			// TODO: handle exception
		}
    }
     public List<Trayecto> trayectosusuario(Rutausuario ruta)
    {
            Query query = this.em.createQuery("select c from Trayecto c where c.idRutaUsuario = :ruta and c.tipotrayecto = 'T' order by c.orden");
//              query.setHint("javax.persistence.cache.storeMode", "REFRESH");
                query.setHint(QueryHints.REFRESH, true);
		try {
                    query.setParameter("ruta", ruta);
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
			// TODO: handle exception
		}
    }
    
}
