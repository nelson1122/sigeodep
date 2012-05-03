/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

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
    
}
