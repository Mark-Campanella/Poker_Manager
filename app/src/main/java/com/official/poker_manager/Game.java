package com.official.poker_manager;

import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;

public class Game implements Serializable {
    // Lista circular para jogadores
    public Table table;

    private class Table implements Serializable
    {
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

    public int getTableBet() {
        return tableBet;
    }

    public void setTableBet(int apostaMesa) {
        this.tableBet = tableBet;
    }

    private int tableBet;
    
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
        this.tableBet = 0;
    }

    public void startGame()
    {
        // Escolher focusedPlayer inicial
        // Setar outras informações necessaŕias para iniciar o game
    }

    public void nextRound(){
        //mudar a rodada do jogo
    }

    //Função referente a situação de quando a aposta da mesa é 0 (check)
    // ou diferente de 0 (Call)
    public void call(){
        Player moment;
        moment = table.getFocusedPlayer();
        moment.bet(tableBet);
        table.nextPlayer();
    }
    //Função para aumentar a aposta da rodada
    public void raise(int bet){
        Player moment;
        moment = table.getFocusedPlayer();
        bet = bet + tableBet;
        tableBet = bet;
        moment.bet(bet);
        table.nextPlayer();
    }

    //Função para sair do rodada
    public void fold() {
        Player moment;
        moment = table.getFocusedPlayer();
        moment.fold();
        table.nextPlayer();
    }
}
