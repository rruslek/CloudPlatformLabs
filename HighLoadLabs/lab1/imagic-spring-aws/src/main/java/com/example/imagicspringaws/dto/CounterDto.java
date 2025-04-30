package com.example.imagicspringaws.dto;


public class CounterDto {
    private final int counter;

    public CounterDto(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }
}