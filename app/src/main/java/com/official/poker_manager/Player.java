package com.official.poker_manager;

public class Player {
    // Nome do jogador
    private String name;
    // Fichas do jogador
    private int chips;
    // Quantidade de fichas apostadas pelo jogador
    private int currentBet;
    // Se o jogador já está na partida ou não
    private boolean isPlaying;
    // Se o jogador dá as cartas
    private boolean isDealer;
    // Se o jogador paga a "small blind"
    private boolean isSmallBlind;
    // Se o jogador paga a "big blind"
    private boolean isBigBlind;
    // Se o jogador saiu da rodada
    private boolean isFolded;

    // Getters e Setters
    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public boolean isDealer() {
        return isDealer;
    }

    public void setDealer(boolean dealer) {
        isDealer = dealer;
    }

    public boolean isSmallBlind() {
        return isSmallBlind;
    }

    public void setSmallBlind(boolean smallBlind) {
        isSmallBlind = smallBlind;
    }

    public boolean isBigBlind() {
        return isBigBlind;
    }

    public void setBigBlind(boolean bigBlind) {
        isBigBlind = bigBlind;
    }
    
    // Construtor
    public Player(String name, int chips, boolean isPlaying) {
        this.name = name;
        this.chips = chips;
        this.isPlaying = isPlaying;
        this.currentBet = 0;
        this.isDealer = false;
        this.isSmallBlind = false;
        this.isBigBlind = false;
        this.isFolded = false;
    }
    
    // Tira o jogador da rodada
    public void Fold() {
        this.isFolded = true;
    }
    
    // Aposta uma quantia
    public void Bet(int amount) {
        this.currentBet += amount;
        this.chips -= this.currentBet;
    }
}
