/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.VictimCharacteristics;

/**
 *
 * @author SANTOS
 */
@Stateless
public class VictimCharacteristicsFacade extends AbstractFacade<VictimCharacteristics> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VictimCharacteristicsFacade() {
        super(VictimCharacteristics.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.characteristicId) from VictimCharacteristics x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<VictimCharacteristics> findCriteria(int variable, String value) {
        String hql;
        try {
            switch (variable) {
                case 1:
                    hql = "Select x from VictimCharacteristics x where x.characteristicName like '%" + value + "%'";
                    return em.createQuery(hql).getResultList();
                case 2:
                    hql = "Select x from VictimCharacteristics x where x.characteristicName like '%" + value + "%'";
                    return em.createQuery(hql).getResultList();
            }
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
        return null;
    }
}
