/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import beans.connection.ConnectionJDBC;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Loads;
import model.pojo.NonFatalInjuries;

/**
 *
 * @author SANTOS
 */
@Stateless
public class NonFatalInjuriesFacade extends AbstractFacade<NonFatalInjuries> {

//    @EJB
//    TagsFacade tagsFacade;
//    @EJB
//    LoadsFacade loadsFacade;
    
    
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;
    private String message = "";
    private String query = "";

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<NonFatalInjuries> findFromTag(int id_tag) {
        String hql;
        try {
            hql = "Select x from Loads x where x.loadsPK.tagId = " + String.valueOf(id_tag);
            List<Loads> loadsList= em.createQuery(hql).getResultList();
            List<NonFatalInjuries> nonFatalInjuriesList=new ArrayList<NonFatalInjuries>();
            NonFatalInjuries currentNonFatalInjuries;
            for (int i = 0; i < loadsList.size(); i++) {
                currentNonFatalInjuries=this.find(loadsList.get(i).getLoadsPK().getRecordId());//busco la lesion no fatal
                nonFatalInjuriesList.add(currentNonFatalInjuries);
            }
            return nonFatalInjuriesList;
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }

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

    public int countLCENF(int idTag) {
        //determina cuantos registros de lesiones no fatales existen
        //dado un determinado conjunto de datos

        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT "
                    + "    count(*) "
                    + "FROM "
                    + "    public.non_fatal_injuries, "
                    + "    public.loads, "
                    + "    public.tags "
                    + "WHERE "
                    + "    loads.record_id = non_fatal_injuries.non_fatal_injury_id AND "
                    + "    tags.tag_id = loads.tag_id AND "
                    + "    tags.tag_id = " + String.valueOf(idTag) + "; ");
            //AQUI VA DEPRONTO EL ID != DE 53
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

    public int findPosition(int id, int id_tag) {
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT item from"
                    + "("
                    + "   SELECT "
                    + "      ROW_NUMBER() "
                    + "   OVER "
                    + "      (ORDER BY non_fatal_injury_id) "
                    + "   AS "
                    + "      item, mt.* "
                    + "   FROM "
                    + "      non_fatal_injuries mt, "
                    + "      public.loads, "
                    + "      public.tags "
                    + "   WHERE "
                    + "      tags.tag_id = loads.tag_id AND "
                    + "      mt.non_fatal_injury_id = loads.record_id AND "
                    + "      tags.form_id LIKE 'SCC-F-032' AND "
                    + "      tags.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "      injury_id != 53"
                    + ") "
                    + "AS "
                    + "   a "
                    + "WHERE "
                    + "   non_fatal_injury_id=" + String.valueOf(id) + ";");
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

    public NonFatalInjuries findNext(int injury_id, int id_tag) {
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
                    //+ "SELECT "
                    //+ "    non_fatal_injury_id "
                    //+ "FROM "
                    //+ "    non_fatal_injuries "
                    //+ "WHERE "
                    //+ "    non_fatal_injury_id > " + String.valueOf(injury_id) + " AND "
                    //+ "    injury_id != 53 "
                    //+ "ORDER BY "
                    // + "    non_fatal_injury_id ASC"
                    //+ "LIMIT "
                    //+ "    1"
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.non_fatal_injuries "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  non_fatal_injuries.non_fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-032' AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "  non_fatal_injuries.non_fatal_injury_id > " + String.valueOf(injury_id) + " "
                    + "ORDER BY "
                    + "  non_fatal_injuries.non_fatal_injury_id ASC "
                    + "LIMIT "
                    + "  1;");
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

    public NonFatalInjuries findPrevious(int injury_id, int id_tag) {
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
                    //                    + "SELECT non_fatal_injury_id FROM non_fatal_injuries "
                    //                    + "WHERE non_fatal_injury_id < " + String.valueOf(id) + " "
                    //                    + "AND injury_id != 53 "
                    //                    + "ORDER BY non_fatal_injury_id DESC;");
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.non_fatal_injuries "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  non_fatal_injuries.non_fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-032' AND "
                    + "  non_fatal_injuries.injury_id != 53 AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "  non_fatal_injuries.non_fatal_injury_id < " + String.valueOf(injury_id) + " "
                    + "ORDER BY "
                    + "  non_fatal_injuries.non_fatal_injury_id DESC "
                    + "LIMIT "
                    + "  1;");
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

    public NonFatalInjuries findFirst(int id_tag) {
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
                    //                    + "SELECT non_fatal_injury_id "
                    //                    + "FROM non_fatal_injuries "
                    //                    + "WHERE injury_id != 53 "
                    //                    + "ORDER BY non_fatal_injury_id ASC;");
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.non_fatal_injuries "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  non_fatal_injuries.non_fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-032' AND "
                    + "  non_fatal_injuries.injury_id != 53 AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " "
                    + "ORDER BY "
                    + "  non_fatal_injuries.non_fatal_injury_id ASC "
                    + "LIMIT "
                    + "  1;");
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

    public NonFatalInjuries findLast(int id_tag) {
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
                    //                    + "SELECT non_fatal_injury_id "
                    //                    + "FROM non_fatal_injuries "
                    //                    + "WHERE injury_id != 53 "
                    //                    + "ORDER BY non_fatal_injury_id DESC;");
                    + "SELECT "
                    + "  non_fatal_injuries.non_fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.non_fatal_injuries "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  non_fatal_injuries.non_fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-032' AND "
                    + "  non_fatal_injuries.injury_id != 53 AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " "
                    + "ORDER BY "
                    + "  non_fatal_injuries.non_fatal_injury_id DESC "
                    + "LIMIT "
                    + "  1;");
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
