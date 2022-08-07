package com.study.jpashop.orders.entity.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;


    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
            "select new com.study.jpashop.orders.entity.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item as i" +
                " where oi.order.id = :orderId", OrderItemQueryDto.class
        ).setParameter("orderId", orderId)
            .getResultList();
    }


    private  List<OrderQueryDto> findOrders(){
        return em.createQuery(
            "select new com.study.jpashop.orders.entity.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from  Order as o" +
                " join o.member as m" +
                " join o.delivery as d", OrderQueryDto.class)
            .getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
            "select new com.study.jpashop.orders.entity.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item as i" +
                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
            .setParameter("orderIds", orderIds)
            .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
            .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
            .map(o -> o.getOrderId())
            .collect(Collectors.toList());
        return orderIds;
    }

    public List<OrderFlatDto>  findAllByDto_flat() {

        return em.createQuery(
            "select new com.study.jpashop.orders.entity.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                " from Order as o" +
                " join o.member as m" +
                " join o.delivery as d" +
                " join o.orderItems as oi" +
                " join oi.item as i", OrderFlatDto.class
        ).getResultList();

    }
}









