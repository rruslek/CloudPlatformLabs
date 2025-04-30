package com.example.imagicspringaws.controller;

import com.example.imagicspringaws.dto.CounterDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/counter")
public class CounterController {
    private final AtomicInteger counter = new AtomicInteger(0);

    @GetMapping
    public CounterDto getCounter() {
        return new CounterDto(counter.incrementAndGet());
    }

    @PostMapping("/reset")
    public CounterDto resetCounter() {
        counter.set(0);
        return new CounterDto(counter.get());
    }
}