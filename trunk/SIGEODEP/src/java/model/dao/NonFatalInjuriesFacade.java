/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.AnatomicalLocations;
import model.pojo.NonFatalInjuries;

/**
 *
 * @author santos
 */
@Stateless
public class NonFatalInjuriesFacade extends AbstractFacade<NonFatalInjuries> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NonFatalInjuriesFacade() {
        super(NonFatalInjuries.class);
    }
    
    public int findMax() {
        try {
            String hql = "Select MAX(x.nonFatalInjuryId) from NonFatalInjuries x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public int findMaxId() {
        try {
            String hql = "Select MAX(x.idRelationGroup) from RelationGroup x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public void create(NonFatalInjuries entity) {
        try {
            //for (Diagnoses diagnose : entity.getDiagnosesList()) {
            //    em.find(Diagnoses.class, diagnose.getDiagnosisId());
            //}

            for (AnatomicalLocations al : entity.getAnatomicalLocationsList()) {
                em.find(AnatomicalLocations.class, al.getAnatomicalLocationId());
            }

            super.create(entity);
        } catch (Exception e) {
            System.out.println(e.toString());
        }


    }
    
}
