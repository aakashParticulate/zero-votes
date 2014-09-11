package com.zero.votes.persistence;

import com.zero.votes.persistence.entities.Recipient;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Stateless
public class RecipientFacade extends AbstractFacade<Recipient> {

    @PersistenceContext(unitName = "com.zero.votes.ZVotesPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RecipientFacade() {
        super(Recipient.class);
    }
    
    /**
     * Returns all entities of type Recipient from range[0] to range[1]
     * with an attribute fieldName:=value, ordered ascending by email.
     * @param fieldName
     * @param value
     * @param range
     * @return 
     */
    public List<Recipient> findRangeByOrderByEmail(String fieldName, Object value, int[] range) {
        getEntityManager().getEntityManagerFactory().getCache().evictAll();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Recipient> cq = cb.createQuery(Recipient.class);
        Root<Recipient> rt = cq.from(Recipient.class);
        cq.select(rt).where(cb.equal(rt.get(fieldName), value));
        cq.orderBy(cb.asc(rt.get("email")));
        TypedQuery<Recipient> q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

}
