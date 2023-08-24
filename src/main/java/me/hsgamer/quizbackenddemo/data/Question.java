package me.hsgamer.quizbackenddemo.data;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    private String key;
    private String question;
    private List<String> choices;
    private String answer;
}
