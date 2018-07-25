/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruta.turismo.rutaturismo.session;

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
public class RutausuarioFacade extends AbstractFacade<Rutausuario> {

    @PersistenceContext(unitName = "com.ruta.turismo_RutaTurismo_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RutausuarioFacade() {
        super(Rutausuario.class);
    }
    public Integer incremento()
    {
    
      String jpqlQuery = "SELECT LAST_INSERT_ID()";
      Query query = this.em.createNativeQuery(jpqlQuery);
      Integer autoincrementoMysql = Integer.parseInt(String.valueOf(query.getSingleResult()));
      return autoincrementoMysql;
    
    }
    public List<Rutausuario> rutasAllusuarios()
    {
            Query query = this.em.createQuery("select c from Rutausuario c order by c.idRutaUsuario desc");
//              query.setHint("javax.persistence.cache.storeMode", "REFRESH");
             query.setHint(QueryHints.REFRESH, true);
		try {
                   
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
			// TODO: handle exception
		}
    }
    
}
