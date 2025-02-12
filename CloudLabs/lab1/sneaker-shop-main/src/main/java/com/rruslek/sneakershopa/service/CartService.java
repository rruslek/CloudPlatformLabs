package com.rruslek.sneakershopa.service;

import com.rruslek.sneakershopa.models.Cart;
import com.rruslek.sneakershopa.models.Item;
import com.rruslek.sneakershopa.models.ItemInCart;
import com.rruslek.sneakershopa.models.Order;
import com.rruslek.sneakershopa.repo.CartRepository;
import com.rruslek.sneakershopa.repo.ItemInCartRepository;
import com.rruslek.sneakershopa.repo.ItemRepository;
import com.rruslek.sneakershopa.repo.OrderRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private ItemInCartRepository itemInCartRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Transactional
    public Integer addItem(Long Item_id, HttpSession session){
        String cartSession = session.getId();
        Cart cart = cartRepo.findByName(cartSession);
        if (cart == null) cart = cartRepo.save(new Cart(cartSession));
        Item item = itemRepo.findById(Item_id).get();
        ItemInCart itemInCart = itemInCartRepo.findByItemAndCart(item, cart);
        if (itemInCart == null) itemInCart = itemInCartRepo.save(new ItemInCart(item, cart));

        itemInCart.setCount(itemInCart.getCount() + 1);
        cart.setCount(cart.getCount() + 1);
        cart.setPrice(cart.getPrice() + item.getPrice());

        itemInCartRepo.save(itemInCart);
        cartRepo.save(cart);

        return cart.getCount();
    }

    @Transactional
    public Integer deleteItem(Long Item_id, HttpSession session){
        String cartSession = session.getId();
        Cart cart = cartRepo.findByName(cartSession);
        Item item = itemRepo.findById(Item_id).get();
        ItemInCart itemInCart = itemInCartRepo.findByItemAndCart(item, cart);
        if (itemInCart.getCount() == 1) {
            itemInCartRepo.delete(itemInCart);
        }
        else {
            itemInCart.setCount(itemInCart.getCount() - 1);
            itemInCartRepo.save(itemInCart);
        }

        cart.setCount(cart.getCount() - 1);
        cart.setPrice(cart.getPrice() - item.getPrice());
        cartRepo.save(cart);

        return cart.getCount();
    }
    @Transactional
    public void createOrder(String city, HttpSession session){
        String cartSession = session.getId();
        Cart cart = cartRepo.findByName(cartSession);
        if (cart.getCount() > 0){
            orderRepo.save(new Order(cart, city));
            session.invalidate();
        }
    }
}
