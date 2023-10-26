package com.official.poker_manager;

import java.util.Map;

public class Game {
    // Pote da partida → PROVISÓRIO
    private int pot;
    // Mapa de posições para jogadores
    public Map<Integer, Player> playerMap;
    // Se aumenta os blinds
    private boolean autoRaise;
    // Quantidade inicial da "big blind"
    private int bigBlind;
    // Quando aumenta a "big blind"
    private int every;
    // Multiplicador da "big blind"
    private float multiplier;
    
    // Getters e Setters
    public int getPot() {
        return pot;
    }

    public boolean isAutoRaise() {
        return autoRaise;
    }

    public int getEvery() {
        return every;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setPot(int pot) {
        this.pot = pot;
    }
    
    // Construtor
    public Game(Map<Integer, Player> playerMap, boolean autoRaise, int bigBlind, int every, float multiplier) {
        this.playerMap = playerMap;
        this.autoRaise = autoRaise;
        this.bigBlind = bigBlind;
        this.every = every;
        this.multiplier = multiplier;
    }
}
