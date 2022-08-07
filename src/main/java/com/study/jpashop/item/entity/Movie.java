package com.study.jpashop.item.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("Moive")
public class Movie  extends Item{

    private String director;
    private String actor;
}
