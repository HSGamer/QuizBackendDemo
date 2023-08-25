package me.hsgamer.quizbackenddemo;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.plugin.bundled.CorsPluginConfig;
import me.hsgamer.quizbackenddemo.loader.QuestionLoader;
import me.hsgamer.quizbackenddemo.loader.WalletLoader;
import me.hsgamer.quizbackenddemo.model.AnswerListRequest;
import me.hsgamer.quizbackenddemo.model.AnswerNftResponse;
import me.hsgamer.quizbackenddemo.model.AnswerResultResponse;
import me.hsgamer.quizbackenddemo.model.QuestionListResponse;

import java.io.IOException;

public class QuizBackendDemo {
    private static final QuestionLoader questionLoader = new QuestionLoader();
    private static final WalletLoader walletLoader = new WalletLoader();

    public static void main(String[] args) throws IOException {
        questionLoader.load();
        walletLoader.load();

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 7070;

        var app = Javalin.create(config -> config.plugins.enableCors(corsContainer -> corsContainer.add(CorsPluginConfig::anyHost)))
                .get("/", ctx -> ctx.result("Hello World"))
                .get("/questions", ctx -> ctx.json(QuestionListResponse.from(questionLoader.getQuestionList().getQuestions())))
                .post("/answer", ctx -> {
                    AnswerListRequest request = ctx.bodyValidator(AnswerListRequest.class).get();
                    AnswerResultResponse answerResultResponse = questionLoader.handle(request);
                    ctx.future(() ->
                            walletLoader.handleReward(request.getWallet(), answerResultResponse)
                                    .thenAccept(mintNftResponse -> {
                                        if (!mintNftResponse.isSuccess()) {
                                            ctx.status(HttpStatus.BAD_REQUEST);
                                        }
                                        ctx.json(AnswerNftResponse.from(answerResultResponse, mintNftResponse));
                                    })
                    );
                })
                .start(port);

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }
}
