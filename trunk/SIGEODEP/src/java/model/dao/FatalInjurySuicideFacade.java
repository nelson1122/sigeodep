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
import model.pojo.FatalInjurySuicide;

/**
 *
 * @author santos
 */
@Stateless
public class FatalInjurySuicideFacade extends AbstractFacade<FatalInjurySuicide> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FatalInjurySuicideFacade() {
        super(FatalInjurySuicide.class);
    }
    
    public FatalInjurySuicide findNext(int id) {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjurySuicide x where x.fatalInjuryId>:id order by x.fatalInjuryId asc";
            return (FatalInjurySuicide) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            //System.out.println(e.toString());
            return null;//no existe siguiente
        }
    }

    public FatalInjurySuicide findPrevious(int id) {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjurySuicide x where x.fatalInjuryId<:id order by x.fatalInjuryId desc";
            return (FatalInjurySuicide) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
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
                    + "FROM fatal_injury_suicide mt"
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

    public FatalInjurySuicide findFirst() {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjurySuicide x order by x.fatalInjuryId asc";
            return (FatalInjurySuicide) em.createQuery(hql).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            //System.out.println(e.toString());
            return null;//no existe primero
        }
    }

    public FatalInjurySuicide findLast() {
        try {
            //select * from usuarios where id > 5 order by id asc limit 1;
            String hql = "Select x from FatalInjurySuicide x order by x.fatalInjuryId desc";
            return (FatalInjurySuicide) em.createQuery(hql).setMaxResults(1).getSingleResult();
        } catch (Exception e) {
            //System.out.println(e.toString());
            return null;//no existe ultimo
        }
    }
    
}
