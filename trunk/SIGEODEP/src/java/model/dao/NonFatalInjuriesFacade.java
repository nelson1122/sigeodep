/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import beans.connection.ConnectionJDBC;
import java.sql.ResultSet;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.AnatomicalLocations;
import model.pojo.NonFatalDomesticViolence;
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

    public int findMaxId2() {
        try {
            String hql = "Select MAX(x.idRelationGroup) from RelationGroup x";
            return em.createQuery(hql, Integer.class).getSingleResult();
        } catch (Exception e) {
            return 1;
        }
    }
    public int findPosition(int id) {
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT item from"
                    + "("
                    + "SELECT ROW_NUMBER() "
                    + "OVER (ORDER BY non_fatal_injury_id) AS item, mt.* "
                    + "FROM non_fatal_injuries mt"
                    + ") "
                    + "as a WHERE non_fatal_injury_id="+String.valueOf(id)+";");
            conx.disconnect();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
    }
    
    public NonFatalInjuries findNext(int id) {
        try {
            String hql = "Select x from NonFatalInjuries x where x.nonFatalInjuryId>:id order by x.nonFatalInjuryId asc";
            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalInjuries findPrevious(int id) {
        try {
            String hql = "Select x from NonFatalInjuries x where x.nonFatalInjuryId<:id order by x.nonFatalInjuryId desc";
            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            return null;//no existe anterior
        }
    }   

    public NonFatalInjuries findFirst() {
        try {
            String hql = "Select x from NonFatalInjuries x order by x.nonFatalInjuryId asc";
            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            return null;//no existe primero
        }
    }

    public NonFatalInjuries findLast() {
        try {
            String hql = "Select x from NonFatalInjuries x order by x.nonFatalInjuryId desc";
            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            return null;//no existe ultimo
        }
    }

//    @Override
//    public void create(NonFatalInjuries entity) {
//        try {
//            for (Diagnoses diagnose : entity.getDiagnosesList()) {
//                em.find(Diagnoses.class, diagnose.getDiagnosisId());
//            }
//            for (AnatomicalLocations al : entity.getAnatomicalLocationsList()) {
//                em.find(AnatomicalLocations.class, al.getAnatomicalLocationId());
//            }
//            super.create(entity);
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//    }
    
}
