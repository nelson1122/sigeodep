/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.HealthProfessionals;

/**
 *
 * @author santos
 */
@Stateless
public class HealthProfessionalsFacade extends AbstractFacade<HealthProfessionals> {
    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HealthProfessionalsFacade() {
        super(HealthProfessionals.class);
    }
    
}
