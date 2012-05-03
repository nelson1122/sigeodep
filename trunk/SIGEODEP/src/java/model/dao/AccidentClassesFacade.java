/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.AccidentClasses;

/**
 *
 * @author santos
 */
@Stateless
public class AccidentClassesFacade extends AbstractFacade<AccidentClasses> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AccidentClassesFacade() {
        super(AccidentClasses.class);
    }
    
}
