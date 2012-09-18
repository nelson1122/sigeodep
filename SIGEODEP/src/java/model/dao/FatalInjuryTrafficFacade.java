/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import beans.connection.ConnectionJDBC;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.FatalInjuryMurder;
import model.pojo.FatalInjuryTraffic;
import model.pojo.Loads;

/**
 *
 * @author SANTOS
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
    
    public List<FatalInjuryTraffic> findFromTag(int id_tag) {
        String hql;
        try {
            hql = "Select x from Loads x where x.loadsPK.tagId = " + String.valueOf(id_tag);
            List<Loads> loadsList = em.createQuery(hql).getResultList();
            List<FatalInjuryTraffic> fatalInjuryTrafficList = new ArrayList<FatalInjuryTraffic>();
            FatalInjuryTraffic currentFatalInjuryTraffic;
            for (int i = 0; i < loadsList.size(); i++) {
                currentFatalInjuryTraffic = this.find(loadsList.get(i).getLoadsPK().getRecordId());//busco la lesion no fatal
                fatalInjuryTrafficList.add(currentFatalInjuryTraffic);
            }
            return fatalInjuryTrafficList;
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }

    }
       
    
    public int countTraffic(int idTag) {
        //select count(*) from non_fatal_injuries where injury_id=54;
        ConnectionJDBC conx;
	try {
	    conx = new ConnectionJDBC();
	    conx.connect();
	    //ResultSet rs = conx.consult("select count(*) from non_fatal_injuries where injury_id = 53;");
            
            ResultSet rs = conx.consult(""
                    + "SELECT "
                    + "    count(*) "
                    + "FROM "
                    + "    public.fatal_injuries, "
                    + "    public.loads, "
                    + "    public.tags "
                    + "WHERE "
                    + "    fatal_injuries.injury_id = 11 AND "
                    + "    loads.record_id = fatal_injuries.fatal_injury_id AND "
                    + "    tags.tag_id = loads.tag_id AND "
                    + "    tags.tag_id = " + String.valueOf(idTag) + "; ");
            
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

    public int findPosition(int injury_id,int id_tag) {
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();ResultSet rs = conx.consult(""
                    + "SELECT item from"
                    + "("
                    + "   SELECT "
                    + "     ROW_NUMBER() OVER (ORDER BY fatal_injury_id) AS item, "
                    + "     m2.*,"
                    + "     m3.*,"
                    + "     m4.* "
                    + "   FROM "
                    + "     fatal_injury_traffic m2, "
                    + "     loads m3, "
                    + "     tags m4"
                    + "   WHERE"
                    + "      m4.tag_id = m3.tag_id AND"
                    + "      m2.fatal_injury_id = m3.record_id AND"
                    + "      m4.form_id LIKE 'SCC-F-029' AND"
                    + "      m4.tag_id = " + String.valueOf(id_tag) + " "
                    + ") "
                    + "as a WHERE fatal_injury_id=" + String.valueOf(injury_id) + "");
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
    
    
    public FatalInjuryTraffic findByIdVictim(String id) {
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
                    + "SELECT fatal_injury_id FROM fatal_injuries, victims "
                    + "WHERE victims.victim_id = " + id + " "
                    + "AND victims.victim_id = fatal_injuries.victim_id ");
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


    public FatalInjuryTraffic findNext(int injury_id, int id_tag) {
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT "
                    + "  fatal_injury_traffic.fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.fatal_injury_traffic "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  fatal_injury_traffic.fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-029' AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "  fatal_injury_traffic.fatal_injury_id > " + String.valueOf(injury_id) + " "
                    + "ORDER BY "
                    + "  fatal_injury_traffic.fatal_injury_id ASC "
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

    public FatalInjuryTraffic findPrevious(int injury_id, int id_tag) {
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT "
                    + "  fatal_injury_traffic.fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.fatal_injury_traffic "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  fatal_injury_traffic.fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-029' AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "  fatal_injury_traffic.fatal_injury_id < " + String.valueOf(injury_id) + " "
                    + "ORDER BY "
                    + "  fatal_injury_traffic.fatal_injury_id DESC "
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
    
    public FatalInjuryTraffic findFirst(int id_tag) {
//        try {
//            //select * from usuarios where id > 5 order by id asc limit 1;
//            String hql = "Select x from FatalInjuryTraffic x order by x.fatalInjuryId asc";
//            return (FatalInjuryTraffic) em.createQuery(hql).setMaxResults(1).getSingleResult();
//        } catch (Exception e) {
//            //System.out.println(e.toString());
//            return null;//no existe primero
//        }
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT "
                    + "  fatal_injury_traffic.fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.fatal_injury_traffic "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  fatal_injury_traffic.fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-029' AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " "                    
                    + "ORDER BY "
                    + "  fatal_injury_traffic.fatal_injury_id ASC "
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

    public FatalInjuryTraffic findLast(int id_tag) {
//        try {
//            //select * from usuarios where id > 5 order by id asc limit 1;
//            String hql = "Select x from FatalInjuryTraffic x order by x.fatalInjuryId desc";
//            return (FatalInjuryTraffic) em.createQuery(hql).setMaxResults(1).getSingleResult();
//        } catch (Exception e) {
//            //System.out.println(e.toString());
//            return null;//no existe ultimo
//        }
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT "
                    + "  fatal_injury_traffic.fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.fatal_injury_traffic "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  fatal_injury_traffic.fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-029' AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " "
                    + "ORDER BY "
                    + "  fatal_injury_traffic.fatal_injury_id DESC "
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
    
}
