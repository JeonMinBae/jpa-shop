package com.study.jpashop.item.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("Book")
public class Book extends Item{

    private String author;
    private String isbn;

}