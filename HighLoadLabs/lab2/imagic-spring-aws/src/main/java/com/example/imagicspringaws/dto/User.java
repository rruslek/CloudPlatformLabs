package com.example.imagicspringaws.dto;


import java.io.Serializable;

public class User implements Serializable {
    private int score = 0;
    private int id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public Integer getScore() {
        return score;
    }

    public void addScore() {
        this.score = score+1;
    }

    public void setName(String name) {
        this.name = name;
    }

}