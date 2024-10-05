package com.rruslek.sneakershopa.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="orders")
public class Order {
    public Order() {
    }

    public Order(Cart cart, String city) {
        this.cart = cart;
        this.city = city;
        this.date = new Date();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "city")
    private String city;

    @Column(name = "date")
    private Date date;

    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}