/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.HealthProfessionals;

/**
 *
 * @author santos
 */
@Stateless
public class HealthProfessionalsFacade extends AbstractFacade<HealthProfessionals> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HealthProfessionalsFacade() {
        super(HealthProfessionals.class);
    }
    
    public HealthProfessionals findByName(String name) {        
        try
        {
            String hql = "Select x from HealthProfessionals x where x.healthProfessionalName=:name";
            return (HealthProfessionals)em.createQuery(hql).setParameter("name", name).getSingleResult();
        }
        catch(Exception e)
        {
            System.out.print("Error: "+e.toString()+"------------------------");
            return null;
        }
    }
    
}
