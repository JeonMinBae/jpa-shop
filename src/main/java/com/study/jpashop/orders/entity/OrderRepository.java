package com.study.jpashop.orders.entity;

import com.study.jpashop.orders.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        return em.createQuery("select o from Order as o join o.member", Order.class)
            .getResultList();
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> member = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.equal(o.get("name"), '%' + orderSearch.getMemberName() + '%');
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);

        return query.getResultList();

    }

    public List<Order> findAllWithMemberDelivery() {

        return em.createQuery(
            "select o from Order as o " +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class
        ).getResultList();

    }

    public List<OrderSimpleQueryDto> finOrderDtos() {


        return em.createQuery(
            "select new com.study.jpashop.orders.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, m.address) from Order as o" +
                " join  o.member as m" +
                " join  o.delivery as d", OrderSimpleQueryDto.class)
            .getResultList();

//        return em.createQuery(
//            "select o from Order as o" +
//                " join  o.member as m" +
//                " join  o.delivery as d", OrderSimpleQueryDto.class)
//            .getResultList();
    }

    public List<Order> findAllWithItem() {

        //1:다 페치조인은 페이징 불가능...(인 메모리에서 페이징처리)
        return em.createQuery(
            "select distinct  o from Order as o" +
                " join fetch o.member as m" +
                " join fetch  o.delivery as d" +
                " join  fetch  o.orderItems as oi" +
                " join fetch oi.item as i", Order.class)
            .getResultList();


    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {

        return em.createQuery(
            "select o from Order as o " +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();

    }
}