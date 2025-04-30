package com.example.imagicspringaws.service;

import com.example.imagicspringaws.dto.CryptoRequest;
import com.example.imagicspringaws.dto.CryptoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class ConsumerService {

    private static final String ENCRYPTION_QUEUE = "encryption_queue";
    private static final String DECRYPTION_QUEUE = "decryption_queue";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 1000)
    public void processEncryptionRequest() throws InterruptedException {
        CryptoRequest request = (CryptoRequest) redisTemplate.opsForList().rightPop(ENCRYPTION_QUEUE);
        if (request != null) {
            String textToEncrypt = request.getText();
            String key = request.getKey();
            String requestId = request.getRequestId();
            try {
                String encryptedText = encrypt(textToEncrypt, key);
                CryptoResponse response = new CryptoResponse(encryptedText, requestId);
                redisTemplate.opsForValue().set(requestId, response);
                System.out.println("Encryption Request ID: " + requestId + ", Encrypted: " + encryptedText);

            } catch (Exception e) {
                System.err.println("Error encrypting: " + e.getMessage());
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    public void processDecryptionRequest() throws InterruptedException {
        CryptoRequest request = (CryptoRequest) redisTemplate.opsForList().rightPop(DECRYPTION_QUEUE);

        if (request != null) {
            String textToDecrypt = request.getText();
            String key = request.getKey();
            String requestId = request.getRequestId();

            try {
                String decryptedText = decrypt(textToDecrypt, key);
                CryptoResponse response = new CryptoResponse(decryptedText, requestId);
                redisTemplate.opsForValue().set(requestId, response);

                System.out.println("Decryption Request ID: " + requestId + ", Decrypted: " + decryptedText);

            } catch (Exception e) {
                System.err.println("Error decrypting: " + e.getMessage());
            }
        }
    }


    private String encrypt(String strToEncrypt, String secret) throws Exception {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e);
            throw e;
        }
    }


    private String decrypt(String strToDecrypt, String secret) throws Exception {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e);
            throw e;
        }
    }
}