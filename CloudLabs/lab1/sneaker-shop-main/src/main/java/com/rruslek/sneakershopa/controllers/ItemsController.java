package com.rruslek.sneakershopa.controllers;

import com.rruslek.sneakershopa.models.Cart;
import com.rruslek.sneakershopa.models.Item;
import com.rruslek.sneakershopa.repo.CartRepository;
import com.rruslek.sneakershopa.repo.ItemRepository;
import com.rruslek.sneakershopa.enums.SneakerTypes;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.rruslek.sneakershopa.enums.SneakerTypes.*;

@Controller
public class ItemsController {
    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private CartRepository cartRepo;

    @GetMapping("/")
    String getItems(@RequestParam(value = "type", defaultValue = "ALL") SneakerTypes type, @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir, Model model, HttpSession session ) {
        Iterable<Item> items;
        if (type == ALL) {
            if (sortDir.equals("desc")) {
                items = itemRepo.findAllByOrderByPriceDesc();
            } else {
                items = itemRepo.findAllByOrderByPrice();
            }
        }
        else {
            if (sortDir.equals("desc")) {
                items = itemRepo.findAllByTypeOrderByPriceDesc(type);
            } else {
                items = itemRepo.findAllByTypeOrderByPrice(type);
            }
        }

        Cart cart = cartRepo.findByName(session.getId());
        if (cart == null) cart = cartRepo.save(new Cart(session.getId()));
        model.addAttribute("cart", cart);
        model.addAttribute("items", items);
        model.addAttribute("title", "SneakerShop");
        model.addAttribute("sessionId", session.getId());

        return "index";
    }

}
