package com.example.imagicspringaws.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Detected(
        @JsonProperty("eng")
        String name,
        @JsonProperty("coord")
        List<Integer> coordination
) {
}