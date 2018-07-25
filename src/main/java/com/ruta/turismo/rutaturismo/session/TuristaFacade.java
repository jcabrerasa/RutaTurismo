/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruta.turismo.rutaturismo.session;

import com.ruta.turismo.rutaturismo.Turista;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author cuer
 */
@Stateless
public class TuristaFacade extends AbstractFacade<Turista> {

    @PersistenceContext(unitName = "com.ruta.turismo_RutaTurismo_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TuristaFacade() {
        super(Turista.class);
    }
      public Integer incremento()
    {
    
      String jpqlQuery = "SELECT LAST_INSERT_ID()";
      Query query = this.em.createNativeQuery(jpqlQuery);
      Integer autoincrementoMysql = Integer.parseInt(String.valueOf(query.getSingleResult()));
      return autoincrementoMysql;
    
    }
    
}
