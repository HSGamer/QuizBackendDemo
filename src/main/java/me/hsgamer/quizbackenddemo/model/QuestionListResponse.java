package me.hsgamer.quizbackenddemo.model;

import lombok.Data;
import me.hsgamer.quizbackenddemo.data.Question;

import java.util.List;

@Data
public class QuestionListResponse {
    private List<QuestionResponse> questions;

    public static QuestionListResponse from(List<Question> questions) {
        var response = new QuestionListResponse();
        response.setQuestions(questions.stream().map(QuestionResponse::from).toList());
        return response;
    }
}
