package com.rruslek.sneakershopa.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="carts")
public class Cart {
    public Cart(){}
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Cart(String name) {
        this.name = name;
    }

    @Column(name = "name")
    private String name;

    @Column(name = "count")
    private int count = 0;

    @Column(name = "total_price")
    private int price = 0;

    @OneToMany(mappedBy = "cart")
    private List<ItemInCart> item_in_cart;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<ItemInCart> getItem_in_cart() {
        return item_in_cart;
    }

    public void setItem_in_cart(List<ItemInCart> item_in_cart) {
        this.item_in_cart = item_in_cart;
    }

}