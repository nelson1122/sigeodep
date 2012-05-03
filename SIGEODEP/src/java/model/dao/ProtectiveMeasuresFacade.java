/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.ProtectiveMeasures;

/**
 *
 * @author santos
 */
@Stateless
public class ProtectiveMeasuresFacade extends AbstractFacade<ProtectiveMeasures> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProtectiveMeasuresFacade() {
        super(ProtectiveMeasures.class);
    }
    
}
