package com.zilch.card.dao;

import com.zilch.card.dto.TransactionFilter;
import com.zilch.card.entity.Transaction;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional
public class TransactionDao {

    private final EntityManager em;

    public TransactionDao(EntityManager em) {
        this.em = em;
    }

    public void save(Transaction transaction) {
        em.persist(transaction);
    }

    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(em.find(Transaction.class, id));
    }

    public List<Transaction> filter(TransactionFilter filter, String sort, Integer offset, Integer limit, boolean desc) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> root = cq.from(Transaction.class);

        if (Objects.nonNull(filter)) {
            cq.where(getPredicate(cb, root, filter));
        }

        cq.orderBy(desc ? cb.desc(root.get(sort)) : cb.asc(root.get(sort)));

        return em.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

    }

    private static Predicate getPredicate(CriteriaBuilder cb, Root<Transaction> root, TransactionFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(filter.getAction())) {
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        if (Objects.nonNull(filter.getCardNumber())) {
            predicates.add(cb.equal(root.get("cardNumber"), filter.getCardNumber()));
        }

        if (Objects.nonNull(filter.getAmountFrom())) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), filter.getAmountFrom()));
        }

        if (Objects.nonNull(filter.getAmountTo())) {
            predicates.add(cb.lessThanOrEqualTo(root.get("amount"), filter.getAmountTo()));
        }

        if (Objects.nonNull(filter.getDateFrom())) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.getDateFrom()));
        }

        if (Objects.nonNull(filter.getDateTo())) {
            predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), filter.getDateTo()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
