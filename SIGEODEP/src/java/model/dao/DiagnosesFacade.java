/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Diagnoses;

/**
 *
 * @author SANTOS
 */
@Stateless
public class DiagnosesFacade extends AbstractFacade<Diagnoses> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DiagnosesFacade() {
        super(Diagnoses.class);
    }

    public Diagnoses findByFormId(String idDiagnoses) {
        try {
            String hql = "Select x from Diagnoses x where x.diagnosisId=:id";
            return (Diagnoses) em.createQuery(hql).setParameter("id", idDiagnoses).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Diagnoses> findCriteria(int variable, String value) {
        String hql;
        try {
            switch (variable) {
                case 1:
                    hql = "Select x from Diagnoses x where x.diagnosisId like '" + value + "%'";
                    return em.createQuery(hql).getResultList();
                case 2:
                    hql = "Select x from Diagnoses x where x.diagnosisName like '" + value + "%'";
                    return em.createQuery(hql).getResultList();
            }
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
        return null;
    }
}
