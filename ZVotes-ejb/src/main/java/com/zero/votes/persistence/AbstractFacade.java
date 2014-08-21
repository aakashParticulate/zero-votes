package com.zero.votes.persistence;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        getEntityManager().persist(entity);
    }

    public T edit(T entity) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        T result = getEntityManager().merge(entity);
        getEntityManager().flush();
        return result;
    }

    public void remove(T entity) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public int countBy(String fieldName, Object value) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(cb.count(rt)).where(cb.equal(rt.get(fieldName), value));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public T findBy(String fieldName, Object value) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rt = cq.from(entityClass);
        cq.select(rt).where(cb.equal(rt.get(fieldName), value));
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<T> findAllBy(String fieldName, Object value) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rt = cq.from(entityClass);
        cq.select(rt).where(cb.equal(rt.get(fieldName), value));
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<T> findRangeBy(String fieldName, Object value, int[] range) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rt = cq.from(entityClass);
        cq.select(rt).where(cb.equal(rt.get(fieldName), value));
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
