package com.study.jpashop.member.service;

import com.study.jpashop.config.p6spy.P6spyConfig;
import com.study.jpashop.member.entity.Address;
import com.study.jpashop.member.entity.Member;
import com.study.jpashop.member.entity.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@Import(P6spyConfig.class)
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void 회원가입() {

        //given
        Member member = new Member();
        member.setName("kim");

        Address address = new Address("시티", "스트리트", "집코드");
        member.setAddress(address);

        //when
        Long savedId = memberService.join(member);

        //then
        em.flush();
        Assertions.assertEquals(member, memberRepository.findOne(savedId ));

    }

    @Test()
    public void 중복_회원_예외() {

        //given
        Member member1 = new Member();
        member1.setName("kim");


        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });
        //then
//        fail("예외가 발생해야한다.");

    }

}