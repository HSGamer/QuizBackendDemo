package me.hsgamer.quizbackenddemo.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import me.hsgamer.quizbackenddemo.data.Question;
import me.hsgamer.quizbackenddemo.data.QuestionList;
import me.hsgamer.quizbackenddemo.model.AnswerListRequest;
import me.hsgamer.quizbackenddemo.model.AnswerResultResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class QuestionLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private QuestionList questionList = new QuestionList();

    public void load() throws IOException {
        File file = new File("questions.json");
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();

            var defaultQuestionList = new QuestionList();

            var defaultQuestion = new Question();
            defaultQuestion.setKey("demo");
            defaultQuestion.setQuestion("What is the answer to life, the universe, and everything?");
            defaultQuestion.setChoices(List.of("42", "24"));
            defaultQuestion.setAnswer("42");

            var defaultQuestion2 = new Question();
            defaultQuestion2.setKey("demo2");
            defaultQuestion2.setQuestion("What is 1 + 1?");
            defaultQuestion2.setChoices(List.of("1", "2", "3", "4"));
            defaultQuestion2.setAnswer("2");

            defaultQuestionList.setQuestions(List.of(defaultQuestion, defaultQuestion2));

            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, defaultQuestionList);
        }
        questionList = MAPPER.readValue(file, QuestionList.class);
    }

    public AnswerResultResponse handle(AnswerListRequest request) {
        Map<String, String> answerMap = request.asMap();

        int correct = 0;
        int total = questionList.getQuestions().size();
        for (Question question : questionList.getQuestions()) {
            String answer = answerMap.get(question.getKey());
            if (answer != null) {
                if (Objects.equals(question.getAnswer(), answer)) {
                    correct++;
                }
            }
        }

        var answerResultResponse = new AnswerResultResponse();
        answerResultResponse.setCorrect(correct);
        answerResultResponse.setTotal(total);
        return answerResultResponse;
    }
}
