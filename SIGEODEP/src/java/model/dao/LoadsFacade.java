/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Loads;

/**
 *
 * @author SANTOS
 */
@Stateless
public class LoadsFacade extends AbstractFacade<Loads> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LoadsFacade() {
        super(Loads.class);
    }

    public int findMax() {
        try {
            String hql = "Select MAX(x.victimId) from Loads x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Loads> findByTagId(String value) {
        String hql;
        try {
            hql = "SELECT l FROM Loads l WHERE l.loadsPK.tagId = :tagId";
            return em.createQuery(hql).setParameter("tagId", value).getResultList();
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
    }
}
