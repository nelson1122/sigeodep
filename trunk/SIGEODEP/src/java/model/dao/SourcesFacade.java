/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Sources;

/**
 *
 * @author santos
 */
@Stateless
public class SourcesFacade extends AbstractFacade<Sources> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SourcesFacade() {
        super(Sources.class);
    }
    
    public Sources findBySourceName(String sourceName) {
        String hql = "Select x from Sources x where x.sourceName=:name";
        return (Sources)em.createQuery(hql).setParameter("name", sourceName).getSingleResult();
    }
    
}
