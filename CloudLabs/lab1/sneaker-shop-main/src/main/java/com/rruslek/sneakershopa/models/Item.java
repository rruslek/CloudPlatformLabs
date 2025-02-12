package com.rruslek.sneakershopa.models;

import com.rruslek.sneakershopa.enums.SneakerTypes;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="items")
public class Item {
    public Item(){}
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String brand, model, img_url;

    private int price;

    private SneakerTypes type;

    @OneToMany(mappedBy = "item")
    private List<ItemInCart> item_in_cart;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public SneakerTypes getType() {
        return type;
    }

    public void setType(SneakerTypes type) {
        this.type = type;
    }

    public List<ItemInCart> getItem_in_cart() {
        return item_in_cart;
    }

    public void setItem_in_cart(List<ItemInCart> item_in_cart) {
        this.item_in_cart = item_in_cart;
    }
}
