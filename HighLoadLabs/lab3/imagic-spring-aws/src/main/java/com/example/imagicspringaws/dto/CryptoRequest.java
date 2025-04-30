package com.example.imagicspringaws.dto;

import java.io.Serializable;

public class CryptoRequest implements Serializable {
    private String text;
    private String key;
    private String requestId;

    public CryptoRequest(String text, String key, String requestId) {
        this.text = text;
        this.key = key;
        this.requestId = requestId;
    }

    public String getText() {
        return text;
    }

    public String getKey() {
        return key;
    }

    public String getRequestId() {
        return requestId;
    }

}
