/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.InvolvedVehicles;

/**
 *
 * @author SANTOS
 */
@Stateless
public class InvolvedVehiclesFacade extends AbstractFacade<InvolvedVehicles> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvolvedVehiclesFacade() {
        super(InvolvedVehicles.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.involvedVehicleId) from InvolvedVehicles x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<InvolvedVehicles> findCriteria(int variable, String value) {
        String hql;
        try {
            switch (variable) {
                case 1:
                    hql = "Select x from InvolvedVehicles x where x.involvedVehicleName like '%" + value + "%'";
                    return em.createQuery(hql).getResultList();
                case 2:
                    hql = "Select x from InvolvedVehicles x where x.involvedVehicleName like '%" + value + "%'";
                    return em.createQuery(hql).getResultList();
            }
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
        return null;
    }
}
