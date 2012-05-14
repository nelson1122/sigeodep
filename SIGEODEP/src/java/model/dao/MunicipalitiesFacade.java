/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Municipalities;

/**
 *
 * @author santos
 */
@Stateless
public class MunicipalitiesFacade extends AbstractFacade<Municipalities> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MunicipalitiesFacade() {
        super(Municipalities.class);
    }
    
    public Municipalities findByName(String name, short depId) {
	try
	{
        String hql = "Select x from Municipalities x where x.municipalityName=:name AND x.municipalitiesPK.departamentId=:depId";
        return (Municipalities)em.createQuery(hql).setParameter("name", name).setParameter("depId", depId).getSingleResult();
	}
	catch(Exception e)
	{
	    System.out.println("Error: "+e.toString()+"-------------");
	    return null;
	}
    }
    
    
}
