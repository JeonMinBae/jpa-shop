package com.study.jpashop.orders.service;

import com.study.jpashop.config.p6spy.P6spyConfig;
import com.study.jpashop.enums.OrderStatus;
import com.study.jpashop.item.entity.Book;
import com.study.jpashop.item.entity.Item;
import com.study.jpashop.item.exception.NotEnoughStockException;
import com.study.jpashop.member.entity.Address;
import com.study.jpashop.member.entity.Member;
import com.study.jpashop.orders.entity.Order;
import com.study.jpashop.orders.entity.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@Import(P6spyConfig.class)
class OrderServiceTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;


    @Test
    public void 상품주문() {

        //given
        Member member = createMember();

        Book book = createBook("JPA", 10, 10000);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order order = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.ORDER, order.getStatus());
        Assertions.assertEquals(1, order.getOrderItems().size());
        Assertions.assertEquals(10000 * orderCount, order.getTotalPrice());
        Assertions.assertEquals(8, book.getStockQuantity());

    }


    @Test
    public void 삼품주문_재고수량초과() {

        //given
        Member member = createMember();
        Item item = createBook("JPA", 10, 10000);

        int orderCount = 11;

        //when
        Assertions.assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), item.getId(), orderCount));

        //then
    }


    @Test
    public void 주문취소() {

        //given
        Member member = createMember();
        Book book = createBook("JPA", 10, 10000);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancel(orderId);

        //then
        Order order = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.CANCEL, order.getStatus());
        Assertions.assertEquals(10, book.getStockQuantity());

    }

    private Book createBook(String name, int stockQuantity, int orderProice) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(orderProice);
        book.setStockQuantity(stockQuantity);

        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "가로수길" ,"123-123"));

        em.persist(member);
        return member;
    }

}