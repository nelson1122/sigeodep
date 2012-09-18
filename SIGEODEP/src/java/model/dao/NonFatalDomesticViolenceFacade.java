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
import model.pojo.FatalInjuryAccident;
import model.pojo.Loads;
import model.pojo.NonFatalDomesticViolence;
import model.pojo.NonFatalInjuries;

/**
 *
 * @author SANTOS
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
    
    public List<NonFatalDomesticViolence> findFromTag(int id_tag) {
        String hql;
        try {
            hql = "Select x from Loads x where x.loadsPK.tagId = " + String.valueOf(id_tag);
            List<Loads> loadsList= em.createQuery(hql).getResultList();
            List<NonFatalDomesticViolence> nonFatalDomesticViolenceList=new ArrayList<NonFatalDomesticViolence>();
            NonFatalDomesticViolence currentNonFatalDomesticViolence;
            for (int i = 0; i < loadsList.size(); i++) {
                currentNonFatalDomesticViolence=this.find(loadsList.get(i).getLoadsPK().getRecordId());//busco la lesion no fatal
                nonFatalDomesticViolenceList.add(currentNonFatalDomesticViolence);
            }
            return nonFatalDomesticViolenceList;
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }

    }
    
    public int findPosition(int id,int id_tag) {
        ConnectionJDBC conx;
	try {
	    conx = new ConnectionJDBC();
	    conx.connect();
	    ResultSet rs = conx.consult(""
		    + "SELECT item from"
		    + "("
		    + "   SELECT "
                    + "     ROW_NUMBER() OVER (ORDER BY non_fatal_injury_id) AS item, "
                    + "     m2.*,"
                    + "     m3.*,"
                    + "     m4.* "
                    + "   FROM "
                    + "      non_fatal_injuries m2, "
                    + "      loads m3, "
                    + "      tags m4"
                    + "   WHERE "
                    + "      m4.tag_id = m3.tag_id AND "
                    + "      m2.non_fatal_injury_id = m3.record_id AND "
                    + "      m4.form_id LIKE 'SCC-F-033' AND "
                    + "      m4.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "      m2.injury_id = 53"
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
    
    public NonFatalDomesticViolence findByIdVictim(String id) {
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
    
    public int countVIF(int idTag) {
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
                    + "    public.non_fatal_injuries, "
                    + "    public.loads, "
                    + "    public.tags "
                    + "WHERE "
                    + "    non_fatal_injuries.injury_id = 53 AND "
                    + "    loads.record_id = non_fatal_injuries.non_fatal_injury_id AND "
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
    
    public NonFatalDomesticViolence findNext(int injury_id, int id_tag) {
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.non_fatal_domestic_violence "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-033' AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id > " + String.valueOf(injury_id) + " "
                    + "ORDER BY "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id ASC "
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

    public NonFatalDomesticViolence findPrevious(int injury_id, int id_tag) {
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.non_fatal_domestic_violence "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-033' AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " AND "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id < " + String.valueOf(injury_id) + " "
                    + "ORDER BY "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id DESC "
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

    public NonFatalDomesticViolence findFirst(int id_tag) {
//        try {
//            String hql = "Select x from NonFatalDomesticViolence x WHERE x.nonFatalInjuries.injuryId.injuryId = 53 order by x.nonFatalInjuryId asc";
//            return (NonFatalDomesticViolence) em.createQuery(hql).setMaxResults(1).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe primero
//        }
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.non_fatal_domestic_violence "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-033' AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " "                    
                    + "ORDER BY "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id ASC "
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

    public NonFatalDomesticViolence findLast(int id_tag) {
//        try {
//            String hql = "Select x from NonFatalDomesticViolence x WHERE x.nonFatalInjuries.injuryId.injuryId = 53 order by x.nonFatalInjuryId desc";
//            return (NonFatalDomesticViolence) em.createQuery(hql).setMaxResults(1).getSingleResult();
//        } catch (Exception e) {
//            return null;//no existe ultimo
//        }
        ConnectionJDBC conx;
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult(""
                    + "SELECT "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id "
                    + "FROM "
                    + "  public.loads, "
                    + "  public.tags, "
                    + "  public.non_fatal_domestic_violence "
                    + "WHERE "
                    + "  tags.tag_id = loads.tag_id AND "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id = loads.record_id AND "
                    + "  tags.form_id LIKE 'SCC-F-033' AND "
                    + "  tags.tag_id = " + String.valueOf(id_tag) + " "
                    + "ORDER BY "
                    + "  non_fatal_domestic_violence.non_fatal_injury_id DESC "
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
