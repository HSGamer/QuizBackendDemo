package me.hsgamer.quizbackenddemo.data;

import lombok.Data;

import java.util.Map;

@Data
public class WalletData {
    private String apiKey;
    private String network;
    private String publicKey;
    private String privateKey;
    private Map<Integer, String> nftRewards;
}
