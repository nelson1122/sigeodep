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
import model.pojo.FatalInjuryTraffic;

/**
 *
 * @author santos
 */
@Stateless
public class FatalInjuryTrafficFacade extends AbstractFacade<FatalInjuryTraffic> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FatalInjuryTrafficFacade() {
        super(FatalInjuryTraffic.class);
    }
    
    public FatalInjuryTraffic findNext(int id) {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjuryTraffic x where x.fatalInjuryId>:id order by x.fatalInjuryId asc";
            return (FatalInjuryTraffic) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            //System.out.println(e.toString());
            return null;//no existe siguiente
        }
    }

    public FatalInjuryTraffic findPrevious(int id) {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjuryTraffic x where x.fatalInjuryId<:id order by x.fatalInjuryId desc";
            return (FatalInjuryTraffic) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
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
                    + "FROM fatal_injury_traffic mt"
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

    public FatalInjuryTraffic findFirst() {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjuryTraffic x order by x.fatalInjuryId asc";
            return (FatalInjuryTraffic) em.createQuery(hql).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            //System.out.println(e.toString());
            return null;//no existe primero
        }
    }

    public FatalInjuryTraffic findLast() {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjuryTraffic x order by x.fatalInjuryId desc";
            return (FatalInjuryTraffic) em.createQuery(hql).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            //System.out.println(e.toString());
            return null;//no existe ultimo
        }
    }
    
}
