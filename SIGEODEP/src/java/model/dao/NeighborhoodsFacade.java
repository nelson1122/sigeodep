/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Neighborhoods;

/**
 *
 * @author SANTOS
 */
@Stateless
public class NeighborhoodsFacade extends AbstractFacade<Neighborhoods> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NeighborhoodsFacade() {
        super(Neighborhoods.class);
    }

    public Neighborhoods findByName(String name) {
        String hql = "Select x from Neighborhoods x where x.neighborhoodName=:name";
        try {
            return (Neighborhoods) em.createQuery(hql).setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
    }

    public Neighborhoods findById(int id) {
        String hql = "Select x from Neighborhoods x where x.neighborhoodId like :id";
        try {
            return (Neighborhoods) em.createQuery(hql).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
    }

    public int findMax() {
        try {
            String hql = "Select MAX(x.neighborhoodId) from Neighborhoods x";
            return em.createQuery(hql, Short.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    /*
     * determinar maximo segun tipo de zona y comuna
     */

    public int findMax(String type, String suburb) {
        String hql;
        int valueInt = 0;
        String valueStr;
        int t=Integer.valueOf(type);
        try {
            switch (t) {
                case 1://zona urbana
                    hql = "Select MAX(x.neighborhoodId) from Neighborhoods x where "
                            + "x.neighborhoodType like '1' and x.suburbId = " + suburb + "";
                    valueInt = em.createQuery(hql, Integer.class).getSingleResult();
                    if (valueInt == 0) {
                        valueStr = "52";
                        if(suburb.length()==1){
                            valueStr = valueStr +"0";                                                    
                        }
                        valueStr = valueStr+String.valueOf(suburb);                            
                        valueStr = valueStr+"01";
                        valueInt=Integer.parseInt(valueStr);                        
                    }
                    break;
                case 2://zona rural
                    hql = "Select MAX(x.neighborhoodId) from Neighborhoods x where "
                            + "x.neighborhoodType like '2'";
                    valueInt = em.createQuery(hql, Integer.class).getSingleResult();
                    if (valueInt == 0) {
                        valueStr = "52";                        
                        valueStr = valueStr+"01";
                        valueInt=Integer.parseInt(valueStr);                        
                    }
                    break;
            }
            return valueInt;

        } catch (Exception e) {
            return 0;
        }
    }

    public List<Neighborhoods> findCriteria(int variable, String value) {
        try {
            switch (variable) {
                case 1:
                    List<Neighborhoods> neighborhoodsList = (List<Neighborhoods>) em.createNativeQuery("select * from neighborhoods where neighborhood_id::text like '" + value + "%';", Neighborhoods.class).getResultList();
                    return neighborhoodsList;
                case 2:
                    String hql = "Select x from Neighborhoods x where x.neighborhoodName like '" + value + "%'";
                    return em.createQuery(hql).getResultList();
            }
        } catch (Exception e) {
            System.out.println(e.toString() + "----------------------------------------------------");
            return null;
        }
        return null;
    }
}
