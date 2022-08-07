package com.study.jpashop.orders.service;

import com.study.jpashop.item.entity.Item;
import com.study.jpashop.item.entity.ItemRepository;
import com.study.jpashop.member.entity.Member;
import com.study.jpashop.member.entity.MemberRepository;
import com.study.jpashop.orders.entity.Delivery;
import com.study.jpashop.orders.entity.Order;
import com.study.jpashop.orders.entity.OrderItem;
import com.study.jpashop.orders.entity.OrderRepository;
import com.study.jpashop.orders.entity.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;


    @Transactional
    public Long order (Long memberId, Long itemId, int count){

        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        Order order = Order.createOrder(member, delivery, orderItem);

        orderRepository.save(order);

        return order.getId();
    }


    @Transactional
    public void cancel(Long orderId){
        Order order = orderRepository.findOne(orderId);

        order.cancel();
    }

    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByCriteria(orderSearch);
    }


}
