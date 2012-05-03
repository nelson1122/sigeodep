/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.Municipalities;

/**
 *
 * @author santos
 */
@Stateless
public class MunicipalitiesFacade extends AbstractFacade<Municipalities> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MunicipalitiesFacade() {
        super(Municipalities.class);
    }
    
}