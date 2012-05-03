/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.pojo.NonFatalDataSources;

/**
 *
 * @author santos
 */
@Stateless
public class NonFatalDataSourcesFacade extends AbstractFacade<NonFatalDataSources> {

    @PersistenceContext(unitName = "SIGEODEPPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NonFatalDataSourcesFacade() {
        super(NonFatalDataSources.class);
    }

    public NonFatalDataSources findByName(String nameDataSource) {
        String hql = "SELECT f FROM NonFatalDataSources f WHERE f.nonFatalDataSourceName = :name";
        return (NonFatalDataSources) em.createQuery(hql).setParameter("name", nameDataSource).getSingleResult();
    }
}
