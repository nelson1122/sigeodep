/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.HealthProfessionals;

/**
 *
 * @author SANTOS
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

    public int findMax() {
        try {
            String hql = "Select MAX(x.healthProfessionalId) from HealthProfessionals x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public List<HealthProfessionals> findCriteria(int variable, String value) {
        String hql;
        try {
            switch (variable) {
                case 1:
                    hql = "Select x from HealthProfessionals x where x.healthProfessionalName like '%" + value + "%'";
                    return em.createQuery(hql).getResultList();
                case 2:
                    hql = "Select x from HealthProfessionals x where x.healthProfessionalName like '%" + value + "%'";
                    return em.createQuery(hql).getResultList();
            }
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
        return null;
    }
    
}
