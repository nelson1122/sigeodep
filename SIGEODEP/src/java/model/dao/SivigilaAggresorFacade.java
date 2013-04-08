/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.SivigilaAggresor;

/**
 *
 * @author santos
 */
@Stateless
public class SivigilaAggresorFacade extends AbstractFacade<SivigilaAggresor> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SivigilaAggresorFacade() {
        super(SivigilaAggresor.class);
    }
    
}
