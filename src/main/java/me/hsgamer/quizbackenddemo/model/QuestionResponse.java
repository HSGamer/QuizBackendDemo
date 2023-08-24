package me.hsgamer.quizbackenddemo.model;

import lombok.Data;
import me.hsgamer.quizbackenddemo.data.Question;

import java.util.List;

@Data
public class QuestionResponse {
    private String key;
    private String question;
    private List<String> choices;

    public static QuestionResponse from(Question question) {
        var response = new QuestionResponse();
        response.setKey(question.getKey());
        response.setQuestion(question.getQuestion());
        response.setChoices(question.getChoices());
        return response;
    }
}
