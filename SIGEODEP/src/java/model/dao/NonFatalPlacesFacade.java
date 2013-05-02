/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.NonFatalPlaces;

/**
 *
 * @author SANTOS
 */
@Stateless
public class NonFatalPlacesFacade extends AbstractFacade<NonFatalPlaces> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NonFatalPlacesFacade() {
        super(NonFatalPlaces.class);
    }

    public int findMax() {
        try {
            String hql = "Select MAX(x.nonFatalPlaceId) from NonFatalPlaces x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public List<NonFatalPlaces> findCriteria(int variable, String value) {
        String hql;
        try {
            switch (variable) {
                case 1:
                    hql = "Select x from NonFatalPlaces x where x.nonFatalPlaceName like '%" + value + "%'";
                    return em.createQuery(hql).getResultList();
                case 2:
                    hql = "Select x from NonFatalPlaces x where x.nonFatalPlaceName like '%" + value + "%'";
                    return em.createQuery(hql).getResultList();
            }
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
        return null;
    }
}
