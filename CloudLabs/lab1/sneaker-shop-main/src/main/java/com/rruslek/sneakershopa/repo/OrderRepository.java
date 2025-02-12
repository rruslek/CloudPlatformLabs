package com.rruslek.sneakershopa.repo;
import com.rruslek.sneakershopa.models.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
