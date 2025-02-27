package com.official.poker_manager;

import java.io.Serializable;

public class Player implements Serializable {
    // Nome do jogador
    private final String name;
    // Total de fichas do jogador
    private int chipsTotal;
    // Quantidade de fichas apostadas pelo jogador na rodada atual
    private int roundChipsBetted;
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

    // Construtor
    public Player(String name, int chipsTotal, boolean isPlaying) {
        this.name = name;
        this.chipsTotal = chipsTotal;
        this.isPlaying = isPlaying;
        this.roundChipsBetted = 0;
        this.isDealer = false;
        this.isSmallBlind = false;
        this.isBigBlind = false;
        this.isFolded = false;
    }

    // Getters e Setters
    public int getChips() {
        return chipsTotal;
    }

    public void setChips(int chips) {
        this.chipsTotal = chips;
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

    public String getName() {
        return name;
    }
    
    public boolean isPlaying() {
        return this.isPlaying;
    }

    public int getRoundChipsBetted() {
        return this.roundChipsBetted;
    }

    public void setRoundChipsBetted(int chips) {
        this.roundChipsBetted = chips;
    }

    public void setIsFolded(boolean isFolded) {
        this.isFolded = isFolded;
    }

    // Tira o jogador da rodada
    public void fold() {
        this.isFolded = true;
    }

    // Retorna se o jogador está na rodada
    public boolean isFolded() {
        return this.isFolded;
    }

    // Aposta uma quantia
    public void bet(int amount) {
        if (amount > this.chipsTotal) {
            this.roundChipsBetted += this.chipsTotal;
            this.chipsTotal -= this.chipsTotal;
        } else {
            this.roundChipsBetted += amount;
            this.chipsTotal -= amount;
        }
    }

    // Adiciona fichas ao jogador
    public void addChips(int amount) {
        this.chipsTotal += amount;
    }
}
