/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.RelationsDiscardedValues;

/**
 *
 * @author SANTOS
 */
@Stateless
public class RelationsDiscardedValuesFacade extends AbstractFacade<RelationsDiscardedValues> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RelationsDiscardedValuesFacade() {
        super(RelationsDiscardedValues.class);
    }
    
    public int findMaxId() {
	try {
	    String hql = "Select MAX(x.idDiscardedValue) from RelationsDiscardedValues x";
	    return em.createQuery(hql, Integer.class).getSingleResult();
	} catch (Exception e) {
	    return 0;
	}
    }
    
}
