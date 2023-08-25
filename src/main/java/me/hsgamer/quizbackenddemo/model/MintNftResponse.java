package me.hsgamer.quizbackenddemo.model;

import lombok.Data;

import java.util.Map;

@Data
public class MintNftResponse {
    private boolean success;
    private String message;
    private Map<String, Object> result;
}
