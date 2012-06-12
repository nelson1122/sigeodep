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
import model.pojo.FatalInjuryAccident;

/**
 *
 * @author santos
 */
@Stateless
public class FatalInjuryAccidentFacade extends AbstractFacade<FatalInjuryAccident> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FatalInjuryAccidentFacade() {
        super(FatalInjuryAccident.class);
    }
    
    public FatalInjuryAccident findNext(int id) {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjuryAccident x where x.fatalInjuryId>:id order by x.fatalInjuryId asc";
            return (FatalInjuryAccident) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            //System.out.println(e.toString());
            return null;//no existe siguiente
        }
    }

    public FatalInjuryAccident findPrevious(int id) {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjuryAccident x where x.fatalInjuryId<:id order by x.fatalInjuryId desc";
            return (FatalInjuryAccident) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            //System.out.println(e.toString());
            return null;//no existe anterior
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
                    + "OVER (ORDER BY fatal_injury_id) AS item, mt.* "
                    + "FROM fatal_injury_accident mt"
                    + ") "
                    + "as a WHERE fatal_injury_id="+String.valueOf(id)+";");
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

    public FatalInjuryAccident findFirst() {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjuryAccident x order by x.fatalInjuryId asc";
            return (FatalInjuryAccident) em.createQuery(hql).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            //System.out.println(e.toString());
            return null;//no existe primero
        }
    }

    public FatalInjuryAccident findLast() {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjuryAccident x order by x.fatalInjuryId desc";
            return (FatalInjuryAccident) em.createQuery(hql).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            //System.out.println(e.toString());
            return null;//no existe ultimo
        }
    }
    
}
