/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Departaments;

/**
 *
 * @author santos
 */
@Stateless
public class DepartamentsFacade extends AbstractFacade<Departaments> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DepartamentsFacade() {
        super(Departaments.class);
    }
    
    public Departaments findByName(String name) {
        
        try
        {
            String hql = "Select x from Departaments x where x.departamentName=:name";
            return (Departaments)em.createQuery(hql).setParameter("name", name).getSingleResult();
        }
        catch(Exception e)
        {
            System.out.print("Error: "+e.toString()+"------------------------");
            return null;
        }
    }
    public Departaments findById(Short id) {
        try
        {
            String hql = "Select x from Departaments x where x.departamentId=:id";
            return (Departaments)em.createQuery(hql).setParameter("id", id).getSingleResult();
        }
        catch(Exception e)
        {
            System.out.print("Error: "+e.toString()+"------------------------");
            return null;
        }
    }
    
}