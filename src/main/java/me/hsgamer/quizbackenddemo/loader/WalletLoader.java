package me.hsgamer.quizbackenddemo.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.hsgamer.quizbackenddemo.data.WalletData;
import me.hsgamer.quizbackenddemo.model.AnswerResultResponse;
import me.hsgamer.quizbackenddemo.model.MintNftResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WalletLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private WalletData walletData = new WalletData();

    public void load() throws IOException {
        File file = new File("wallet.json");
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();

            var defaultWalletData = new WalletData();
            defaultWalletData.setNetwork("devnet");
            defaultWalletData.setApiKey("API_KEY");
            defaultWalletData.setPublicKey("PUBLIC_KEY");
            defaultWalletData.setPrivateKey("PRIVATE_KEY");
            defaultWalletData.setNftRewards(Map.of(
                    0, "NFT_ADDRESS_1",
                    3, "NFT_ADDRESS_2"
            ));

            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, defaultWalletData);
        }
        walletData = MAPPER.readValue(file, WalletData.class);
    }

    public CompletableFuture<MintNftResponse> handleReward(String wallet, AnswerResultResponse answerResultResponse) {
        int correctAnswer = answerResultResponse.getCorrect();

        String nftAddress = "";
        int nearestCorrectAnswer = 0;
        for (Map.Entry<Integer, String> entry : walletData.getNftRewards().entrySet()) {
            String currentNftAddress = entry.getValue();
            int currentCorrectAnswer = entry.getKey();

            if (correctAnswer >= currentCorrectAnswer && currentCorrectAnswer >= nearestCorrectAnswer) {
                nftAddress = currentNftAddress;
                nearestCorrectAnswer = currentCorrectAnswer;
            }
        }

        if (nftAddress.isEmpty()) {
            MintNftResponse response = new MintNftResponse();
            response.setSuccess(false);
            response.setMessage("No reward");
            response.setResult(Collections.emptyMap());
            return CompletableFuture.completedFuture(response);
        }


        HttpRequest httpRequest;
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("network", walletData.getNetwork());
            requestBody.put("private_key", walletData.getPrivateKey());
            requestBody.put("master_nft_address", nftAddress);
            requestBody.put("receiver", wallet);

            httpRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://api.shyft.to/sol/v1/nft/mint"))
                    .headers("Content-Type", "application/json")
                    .headers("x-api-key", walletData.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(requestBody)))
                    .build();
        } catch (URISyntaxException | JsonProcessingException e) {
            e.printStackTrace(new PrintWriter(System.out));

            MintNftResponse response = new MintNftResponse();
            response.setSuccess(false);
            response.setMessage("Exception");
            response.setResult(Map.of("detail", e.getMessage()));
            return CompletableFuture.completedFuture(response);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                String jsonString = response.body();
                return MAPPER.readValue(jsonString, MintNftResponse.class);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace(new PrintWriter(System.out));

                MintNftResponse response = new MintNftResponse();
                response.setSuccess(false);
                response.setMessage("Exception");
                response.setResult(Map.of("detail", e.getMessage()));
                return response;
            }
        });
    }
}
