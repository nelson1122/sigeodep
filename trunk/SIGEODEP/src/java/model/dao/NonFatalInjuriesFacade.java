/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import beans.connection.ConnectionJDBC;
import java.sql.ResultSet;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.NonFatalInjuries;
import sun.misc.resources.Messages_it;

/**
 *
 * @author SANTOS
 */
@Stateless
public class NonFatalInjuriesFacade extends AbstractFacade<NonFatalInjuries> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;
    private String message = "";
    private String query = "";

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NonFatalInjuriesFacade() {
        super(NonFatalInjuries.class);
    }

    public int findMax() {
//        try {
//            String hql = "Select MAX(x.nonFatalInjuryId) from NonFatalInjuries x";
//            return em.createQuery(hql, Integer.class).getSingleResult();
//        } catch (Exception e) {
//            return 0;
//        }
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT MAX(non_fatal_injury_id) "
                    + "FROM non_fatal_injuries;");
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

    public int findMaxId2() {
//        try {
//            String hql = "Select MAX(x.idRelationGroup) from RelationGroup x";
//            return em.createQuery(hql, Integer.class).getSingleResult();
//        } catch (Exception e) {
//            return 1;
//        }
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT MAX(id_relation_group) "
                    + "FROM relation_group;");
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

    public int countLCENF() {
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult("select count(*) from non_fatal_injuries where injury_id != 53;");
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
                    + "FROM non_fatal_injuries mt WHERE injury_id != 53"
                    + ") "
                    + "as a WHERE non_fatal_injury_id=" + String.valueOf(id) + ";");
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
//        try {
//            String hql = "SELECT x FROM NonFatalInjuries x where x.nonFatalInjuryId>:id AND x.injuryId.injuryId != 53 order by x.nonFatalInjuryId asc";
//            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe siguiente
//        }
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT non_fatal_injury_id FROM non_fatal_injuries "
                    + "WHERE non_fatal_injury_id > " + String.valueOf(id) + " "
                    + "AND injury_id != 53 "
                    + "ORDER BY non_fatal_injury_id ASC;");
            conx.disconnect();
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalInjuries findByIdVictim(String id) {
//        try {
//            String hql = "SELECT x FROM NonFatalInjuries x where x.nonFatalInjuryId>:id AND x.injuryId.injuryId != 53 order by x.nonFatalInjuryId asc";
//            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe siguiente
//        }
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT non_fatal_injury_id FROM non_fatal_injuries, victims "
                    + "WHERE victims.victim_id = " + id + " "
                    + "AND victims.victim_id = non_fatal_injuries.victim_id ");
                    //+ "ORDER BY non_fatal_injury_id ASC;");
            conx.disconnect();
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalInjuries findPrevious(int id) {
//        try {
//            String hql = "Select x from NonFatalInjuries x where x.nonFatalInjuryId<:id AND x.injuryId.injuryId != 53 order by x.nonFatalInjuryId desc";
//            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).setParameter("id", id).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe anterior
//        }
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT non_fatal_injury_id FROM non_fatal_injuries "
                    + "WHERE non_fatal_injury_id < " + String.valueOf(id) + " "
                    + "AND injury_id != 53 "
                    + "ORDER BY non_fatal_injury_id DESC;");
            conx.disconnect();
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalInjuries findFirst() {
//	try {
//	    String hql = "Select x from NonFatalInjuries x WHERE x.injuryId.injuryId != 53 order by x.nonFatalInjuryId asc";
//            NonFatalInjuries nfi=(NonFatalInjuries) em.createQuery(hql).setMaxResults(1).getSingleResult();
//	    return nfi;
//	} catch (Exception e) {
//          return null;            
//	}
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT non_fatal_injury_id "
                    + "FROM non_fatal_injuries "
                    + "WHERE injury_id != 53 "
                    + "ORDER BY non_fatal_injury_id ASC;");
            conx.disconnect();
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public NonFatalInjuries findLast() {
//        try {
//            String hql = "Select x from NonFatalInjuries x WHERE x.injuryId.injuryId != 53 order by x.nonFatalInjuryId desc";
//            return (NonFatalInjuries) em.createQuery(hql).setMaxResults(1).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe ultimo
//        }
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT non_fatal_injury_id "
                    + "FROM non_fatal_injuries "
                    + "WHERE injury_id != 53 "
                    + "ORDER BY non_fatal_injury_id DESC;");
            conx.disconnect();
            if (rs.next()) {
                return this.find(Integer.parseInt(rs.getString(1)));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;//no existe siguiente
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
