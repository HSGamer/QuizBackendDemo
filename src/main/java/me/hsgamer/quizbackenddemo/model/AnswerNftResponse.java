package me.hsgamer.quizbackenddemo.model;

import lombok.Data;

import java.util.Map;

@Data
public class AnswerNftResponse {
    private int correct;
    private int total;
    private boolean success;
    private String message;
    private Map<String, Object> result;

    public static AnswerNftResponse from(AnswerResultResponse answerResultResponse, MintNftResponse mintNftResponse) {
        var response = new AnswerNftResponse();
        response.setCorrect(answerResultResponse.getCorrect());
        response.setTotal(answerResultResponse.getTotal());
        response.setSuccess(mintNftResponse.isSuccess());
        response.setMessage(mintNftResponse.getMessage());
        response.setResult(mintNftResponse.getResult());
        return response;
    }
}
