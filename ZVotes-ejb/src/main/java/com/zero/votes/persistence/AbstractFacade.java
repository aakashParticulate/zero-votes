package com.zero.votes.persistence;

import java.lang.reflect.Field;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    /**
     * Creates the entity in the database
     * @param entity 
     */
    public void create(T entity) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        getEntityManager().persist(entity);
    }

    /**
     * Overwrites the entity in the database
     * @param entity
     * @return 
     */
    public T edit(T entity) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        T result = getEntityManager().merge(entity);
        getEntityManager().flush();
        return result;
    }

    /**
     * Removes the enity from the database
     * @param entity 
     */
    public void remove(T entity) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    /**
     * Returns an entity of type T with the fitting id
     * @param id
     * @return 
     */
    public T find(Object id) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        return getEntityManager().find(entityClass, id);
    }

    /**
     * Returns all entities of type T
     * @return 
     */
    public List<T> findAll() {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Return all entities of type T from range[0] to range[1]
     * @param range  
     * @return 
     */
    public List<T> findRange(int[] range) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    /**
     * Returns the number of entities of type T
     * @return 
     */
    public int count() {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Returns the number of entities, which contain an attribute
     * fieldName := value.
     * @param fieldName
     * @param value
     * @return 
     */
    public int countBy(String fieldName, Object value) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<T> rt = cq.from(entityClass);
        try {
            Field current_field = entityClass.getDeclaredField(fieldName);
            if (current_field.isAnnotationPresent(ManyToMany.class) || current_field.isAnnotationPresent(OneToMany.class)) {
                Join join = rt.join(fieldName);
                cq.select(cb.count(rt)).where(cb.equal(join.get("id"), value));
            } else {
                cq.select(cb.count(rt)).where(cb.equal(rt.get(fieldName), value));
            }
        } catch (NoSuchFieldException ex) {
            return -1;
        }
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Returns the number of entities, where for all indices n from the parameters
     * fieldName[n] := values[n]
     * @param fieldNames
     * @param values
     * @return 
     */
    public int countBy(String[] fieldNames, Object[] values) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<T> rt = cq.from(entityClass);
        Predicate[] predicates = new Predicate[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            try {
                Field current_field = entityClass.getDeclaredField(fieldNames[i]);
                if (current_field.isAnnotationPresent(ManyToMany.class) || current_field.isAnnotationPresent(OneToMany.class)) {
                    Join join = rt.join(fieldNames[i]);
                    predicates[i] = cb.equal(join.get("id"), values[i]);
                } else {
                    predicates[i] = cb.equal(rt.get(fieldNames[i]), values[i]);
                }
            } catch (NoSuchFieldException ex) {
                return -1;
            }
        }
        cq.select(cb.count(rt)).where(predicates);
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Returns an entity of type T, which contain an attribute fieldName := value
     * @param fieldName
     * @param value
     * @return 
     */
    public T findBy(String fieldName, Object value) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rt = cq.from(entityClass);
        try {
            Field current_field = entityClass.getDeclaredField(fieldName);
            if (current_field.isAnnotationPresent(ManyToMany.class) || current_field.isAnnotationPresent(OneToMany.class)) {
                Join join = rt.join(fieldName);
                cq.select(rt).where(cb.equal(join.get("id"), value));
            } else {
                cq.select(rt).where(cb.equal(rt.get(fieldName), value));
            }
        } catch (NoSuchFieldException ex) {
            return null;
        }
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns an entity of type T, where for all indices n from the method's parameters
     * fieldName[n] := values[n]
     * @param fieldNames
     * @param values
     * @return 
     */
    public T findBy(String[] fieldNames, Object[] values) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rt = cq.from(entityClass);
        Predicate[] predicates = new Predicate[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            try {
                Field current_field = entityClass.getDeclaredField(fieldNames[i]);
                if (current_field.isAnnotationPresent(ManyToMany.class) || current_field.isAnnotationPresent(OneToMany.class)) {
                    Join join = rt.join(fieldNames[i]);
                    predicates[i] = cb.equal(join.get("id"), values[i]);
                } else {
                    predicates[i] = cb.equal(rt.get(fieldNames[i]), values[i]);
                }
            } catch (NoSuchFieldException ex) {
                return null;
            }
        }
        cq.select(rt).where(predicates);
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns all entities of type T with an attribute fieldName:=value
     * @param fieldName
     * @param value
     * @return 
     */
    public List<T> findAllBy(String fieldName, Object value) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rt = cq.from(entityClass);
        try {
            Field current_field = entityClass.getDeclaredField(fieldName);
            if (current_field.isAnnotationPresent(ManyToMany.class) || current_field.isAnnotationPresent(OneToMany.class)) {
                Join join = rt.join(fieldName);
                cq.select(rt).where(cb.equal(join.get("id"), value));
            } else {
                cq.select(rt).where(cb.equal(rt.get(fieldName), value));
            }
        } catch (NoSuchFieldException ex) {
            return null;
        }
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns all entities of type T, where for all indices n from the method's parameters
     * fieldName[n] := values[n]
     * @param fieldNames
     * @param values
     * @return 
     */
    public List<T> findAllBy(String[] fieldNames, Object[] values) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rt = cq.from(entityClass);
        Predicate[] predicates = new Predicate[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            try {
                Field current_field = entityClass.getDeclaredField(fieldNames[i]);
                if (current_field.isAnnotationPresent(ManyToMany.class) || current_field.isAnnotationPresent(OneToMany.class)) {
                    Join join = rt.join(fieldNames[i]);
                    predicates[i] = cb.equal(join.get("id"), values[i]);
                } else {
                    predicates[i] = cb.equal(rt.get(fieldNames[i]), values[i]);
                }
            } catch (NoSuchFieldException ex) {
                return null;
            }
        }
        cq.select(rt).where(predicates);
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns all entities of type T from range[0] to range[1] 
     * with an attribute fieldName:=value
     * @param fieldName
     * @param value
     * @param range
     * @return 
     */
    public List<T> findRangeBy(String fieldName, Object value, int[] range) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> rt = cq.from(entityClass);
        try {
            Field current_field = entityClass.getDeclaredField(fieldName);
            if (current_field.isAnnotationPresent(ManyToMany.class) || current_field.isAnnotationPresent(OneToMany.class)) {
                Join join = rt.join(fieldName);
                cq.select(rt).where(cb.equal(join.get("id"), value));
            } else {
                cq.select(rt).where(cb.equal(rt.get(fieldName), value));
            }
        } catch (NoSuchFieldException ex) {
            return null;
        }
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1]);
        q.setFirstResult(range[0]);
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Returns all entities of type T from range[0] to range[1], where for all indices n 
     * from the method's parameters fieldName[n] := values[n]
     * @param fieldNames
     * @param values
     * @param range
     * @return 
     */
    public List<T> findRangeBy(String[] fieldNames, Object[] values, int[] range) {
        if (fieldNames.length == values.length) {
            getEntityManager().getEntityManagerFactory().getCache().evictAll();
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> rt = cq.from(entityClass);
            Predicate[] predicates = new Predicate[fieldNames.length];
            for (int i = 0; i < fieldNames.length; i++) {
                try {
                    Field current_field = entityClass.getDeclaredField(fieldNames[i]);
                    if (current_field.isAnnotationPresent(ManyToMany.class) || current_field.isAnnotationPresent(OneToMany.class)) {
                        Join join = rt.join(fieldNames[i]);
                        predicates[i] = cb.equal(join.get("id"), values[i]);
                    } else {
                        predicates[i] = cb.equal(rt.get(fieldNames[i]), values[i]);
                    }
                } catch (NoSuchFieldException ex) {
                    return null;
                }
            }
            cq.select(rt).where(predicates);
            TypedQuery<T> q = getEntityManager().createQuery(cq);
            q.setMaxResults(range[1]);
            q.setFirstResult(range[0]);
            try {
                return q.getResultList();
            } catch (NoResultException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
