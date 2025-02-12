package com.rruslek.sneakershopa.repo;

import com.rruslek.sneakershopa.models.Item;
import com.rruslek.sneakershopa.enums.SneakerTypes;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Long> {
    List<Item> findAllByOrderByPrice();
    List<Item> findAllByOrderByPriceDesc();
    List<Item> findAllByTypeOrderByPrice(SneakerTypes type);
    List<Item> findAllByTypeOrderByPriceDesc(SneakerTypes type);
}
