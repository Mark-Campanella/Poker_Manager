package com.official.poker_manager;

import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Game implements Serializable {
    // Lista circular para jogadores
    private Table table;

    private class Table {
        private ArrayList<Player> players;
        private int focusedPlayerID;

        public Table(ArrayList<Player> players)
        {
            this.players = players;
            this.focusedPlayerID = 0;
        }

        public void addPlayer(int playerID, Player player)
        {
            this.players.set(playerID, player);
        }

        public void removePlayer(int playerID)
        {
            this.players.set(playerID, null);
        }

        public void setFocusedPlayer(int playerID)
        {
            this.focusedPlayerID = playerID;
        }

        public Player getFocusedPlayer()
        {
            return players.get(focusedPlayerID);
        }

        public void nextPlayer()
        {
            do {
                focusedPlayerID++;
                if (focusedPlayerID == 10) focusedPlayerID = 0;
            } while (players.get(focusedPlayerID) == null);
        }
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

    public void startGame()
    {
        // Escolher focusedPlayer inicial
        // Setar outras informações necessaŕias para iniciar o game
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
