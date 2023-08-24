package me.hsgamer.quizbackenddemo.model;

import lombok.Data;
import me.hsgamer.quizbackenddemo.data.Answer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class AnswerListRequest {
    private List<Answer> answers;

    public Map<String, String> asMap() {
        return answers.stream().collect(Collectors.toMap(Answer::getKey, Answer::getAnswer));
    }
}
