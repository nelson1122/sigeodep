/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.DomesticViolenceDataSources;

/**
 *
 * @author SANTOS
 */
@Stateless
public class DomesticViolenceDataSourcesFacade extends AbstractFacade<DomesticViolenceDataSources> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DomesticViolenceDataSourcesFacade() {
        super(DomesticViolenceDataSources.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.domesticViolenceDataSourcesId) from DomesticViolenceDataSources x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<DomesticViolenceDataSources> findCriteria(int variable, String value) {
        String hql;
        try {
            switch (variable) {
                case 1:
                    hql = "Select x from DomesticViolenceDataSources x where x.domesticViolenceDataSourcesName like '" + value + "%'";
                    return em.createQuery(hql).getResultList();
                case 2:
                    hql = "Select x from DomesticViolenceDataSources x where x.domesticViolenceDataSourcesName like '" + value + "%'";
                    return em.createQuery(hql).getResultList();
            }
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
        return null;
    }
}
