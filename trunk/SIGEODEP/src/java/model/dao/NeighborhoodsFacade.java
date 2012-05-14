/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Neighborhoods;

/**
 *
 * @author santos
 */
@Stateless
public class NeighborhoodsFacade extends AbstractFacade<Neighborhoods> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NeighborhoodsFacade() {
        super(Neighborhoods.class);
    }
    
     public Neighborhoods findByName(String name) {
        String hql = "Select x from Neighborhoods x where x.neighborhoodName=:name";
        try
        {
            return (Neighborhoods)em.createQuery(hql).setParameter("name", name).getSingleResult();
        }
        catch(Exception e)
        {
            System.out.println(e.toString()+"----------------------------------------------------");
            return null;
        }
    }
     public Neighborhoods findById(int id) {
        String hql = "Select x from Neighborhoods x where x.neighborhoodId=:id";
        try
        {
            return (Neighborhoods)em.createQuery(hql).setParameter("id", id).getSingleResult();
        }
        catch(Exception e)
        {
            System.out.println(e.toString()+"----------------------------------------------------");
            return null;
        }
    }
    
}
