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
import model.pojo.NonFatalDomesticViolence;

/**
 *
 * @author santos
 */
@Stateless
public class NonFatalDomesticViolenceFacade extends AbstractFacade<NonFatalDomesticViolence> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NonFatalDomesticViolenceFacade() {
        super(NonFatalDomesticViolence.class);
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
                    + "FROM non_fatal_domestic_violence mt"
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
    
    public NonFatalDomesticViolence findNext(int id) {
        try {
            String hql = "Select x from NonFatalDomesticViolence x where x.nonFatalInjuryId>:id order by x.nonFatalInjuryId asc";
            return (NonFatalDomesticViolence) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalDomesticViolence findPrevious(int id) {
        try {
            String hql = "Select x from NonFatalDomesticViolence x where x.nonFatalInjuryId<:id order by x.nonFatalInjuryId desc";
            return (NonFatalDomesticViolence) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            return null;//no existe anterior
        }
    }   

    public NonFatalDomesticViolence findFirst() {
        try {
            String hql = "Select x from NonFatalDomesticViolence x order by x.nonFatalInjuryId asc";
            return (NonFatalDomesticViolence) em.createQuery(hql).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            return null;//no existe primero
        }
    }

    public NonFatalDomesticViolence findLast() {
        try {
            String hql = "Select x from NonFatalDomesticViolence x order by x.nonFatalInjuryId desc";
            return (NonFatalDomesticViolence) em.createQuery(hql).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            return null;//no existe ultimo
        }
    }
    
}
