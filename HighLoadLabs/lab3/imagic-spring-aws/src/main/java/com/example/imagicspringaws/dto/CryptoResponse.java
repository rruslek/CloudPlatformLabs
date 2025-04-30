package com.example.imagicspringaws.dto;

import java.io.Serializable;

public class CryptoResponse implements Serializable {
    private String resultText;
    private String requestId;

    public CryptoResponse(String resultText, String requestId) {
        this.resultText = resultText;
        this.requestId = requestId;
    }

    public String getResultText() {
        return resultText;
    }

    public String getRequestId() {
        return requestId;
    }
}