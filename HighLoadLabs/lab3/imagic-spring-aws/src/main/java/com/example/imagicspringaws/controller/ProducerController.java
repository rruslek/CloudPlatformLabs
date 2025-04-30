package com.example.imagicspringaws.controller;

import com.example.imagicspringaws.dto.CryptoRequest;
import com.example.imagicspringaws.dto.CryptoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;
import java.util.UUID;

@Controller
public class ProducerController {

    private static final String ENCRYPTION_QUEUE = "encryption_queue";
    private static final String DECRYPTION_QUEUE = "decryption_queue";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/")
    public String showForm() {
        return "index";
    }

    @PostMapping("/encrypt")
    public String encryptText(@RequestParam("text") String text, @RequestParam("key") String key, Model model) throws InterruptedException {
        String requestId = UUID.randomUUID().toString();
        CryptoRequest request = new CryptoRequest(text, key, requestId);
        System.out.println(requestId);

        Random random = new Random();
        Thread.sleep(random.nextInt(300));

        redisTemplate.opsForList().leftPush(ENCRYPTION_QUEUE, request);
        model.addAttribute("message", "Текст отправлен на шифрование.");
        model.addAttribute("requestId", requestId);
        return "process";
    }

    @PostMapping("/decrypt")
    public String decryptText(@RequestParam("text") String text, @RequestParam("key") String key, Model model) throws InterruptedException {
        String requestId = UUID.randomUUID().toString();
        CryptoRequest request = new CryptoRequest(text, key, requestId);

        Random random = new Random();
        Thread.sleep(random.nextInt(300));

        redisTemplate.opsForList().leftPush(DECRYPTION_QUEUE, request);
        model.addAttribute("message", "Текст отправлен на дешифрование.");
        model.addAttribute("requestId", requestId);
        return "process";
    }

    @GetMapping("/result")
    public String getResult(@RequestParam("requestId") String requestId, Model model) {
        CryptoResponse response = (CryptoResponse) redisTemplate.opsForValue().get(requestId);

        model.addAttribute("result", response.getResultText());
        model.addAttribute("message", "Зашифрованный текст:");

        return "result";

    }
}