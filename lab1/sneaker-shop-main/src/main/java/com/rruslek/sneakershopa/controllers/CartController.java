package com.rruslek.sneakershopa.controllers;

import com.rruslek.sneakershopa.models.Cart;
import com.rruslek.sneakershopa.models.ItemInCart;
import com.rruslek.sneakershopa.repo.CartRepository;
import com.rruslek.sneakershopa.repo.ItemInCartRepository;
import com.rruslek.sneakershopa.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepo;
    @Autowired
    private ItemInCartRepository itemInCartRepo;
    @GetMapping("/cart")
    String getCart(Model model, HttpSession session) {
        Cart cart = cartRepo.findByName(session.getId());
        if (cart == null) cart = cartRepo.save(new Cart(session.getId()));
        Iterable<ItemInCart> itemsInCart = itemInCartRepo.findAllByCartOrderByItem(cart);
        model.addAttribute("title", "Your Cart");
        model.addAttribute("items", itemsInCart);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/cart/add/{item_id}")
    ResponseEntity<Integer> addItemToCart(@PathVariable("item_id") Long itemId, HttpSession session){
        int count = cartService.addItem(itemId, session);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @DeleteMapping("/cart/add/{item_id}")
    ResponseEntity<Integer> deleteItemFromCart(@PathVariable("item_id") Long itemId, HttpSession session){
        int count = cartService.deleteItem(itemId, session);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping("/order/{city}")
    ResponseEntity<Integer> addItemToCart(@PathVariable("city") String city, HttpSession session){
        cartService.createOrder(city, session);
        return new ResponseEntity(HttpStatus.CREATED);
    }

}
