package me.hsgamer.quizbackenddemo.data;

import lombok.Data;

import java.util.List;

@Data
public class QuestionList {
    private List<Question> questions;
}
