package com.official.poker_manager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    // Lista circular para jogadores
    private Table table;

    private class Table {
        private ArrayList<Player> players;

        public Table(ArrayList<Player> players) {
            this.players = players;
        }

        //Requer apenas um set de players
        //Adicionar um player em um posição específica
        //Remover um player de uma posição específica
        //Controla o player em foco e o retorna
        //Gira gira gira
    }

    // Jogado em foco
    private int focusedPlayerID;
    // Se aumenta os blinds
    private boolean autoRaise;
    // Quantidade inicial da "big blind"
    private int bigBlind;
    // Quando aumenta a "big blind"
    private int every;
    // Multiplicador da "big blind"
    private float multiplier;
    
    // Getters e Setters
    public boolean isAutoRaise() {
        return autoRaise;
    }

    public int getEvery() {
        return every;
    }

    public float getMultiplier() {
        return multiplier;
    }
    
    // Construtor
    public Game(ArrayList<Player> players, boolean autoRaise, int bigBlind, int every, float multiplier) {
        this.table = new Table(players);
        this.autoRaise = autoRaise;
        this.bigBlind = bigBlind;
        this.every = every;
        this.multiplier = multiplier;
    }

    public void call()
    {

    }

    public void raise()
    {

    }

    public void fold()
    {
        //playerMap.get(focusedPlayerID).fold();

    }

    private void setNextPlayer()
    {

    }
}
