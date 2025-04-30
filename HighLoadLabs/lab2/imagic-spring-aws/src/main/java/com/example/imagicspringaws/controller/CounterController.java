package com.example.imagicspringaws.controller;

import com.example.imagicspringaws.dto.User;
import com.example.imagicspringaws.service.ClickService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Controller
public class CounterController {

    private final ClickService clickService;

    public CounterController(ClickService clickService) {
        this.clickService = clickService;
    }

    @GetMapping("/")
    public String home(@CookieValue(value = "username", required = false) String username, Model model) {
        if (username == null || username.isEmpty()) {
            return "login";
        }

        long clicks = clickService.getClicks(username);
        model.addAttribute("username", username);
        model.addAttribute("clicks", clicks);
        model.addAttribute("leaders", clickService.getTopUsers(10));
        return "index";
    }

    @PostMapping("/set-name")
    public String setName(@RequestParam String username, HttpServletResponse response) {
        Cookie cookie = new Cookie("username", username);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 дней
        response.addCookie(cookie);

        // создаём запись в Redis если новой нет
        clickService.initUserIfAbsent(username);

        return "redirect:/";
    }

    @PostMapping("/click")
    public String click(@CookieValue("username") String username) {
        clickService.incrementClick(username);
        return "redirect:/";
    }
}