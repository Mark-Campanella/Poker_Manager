package com.official.poker_manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Game implements Serializable {
    // Lista circular para jogadores
    private Table table;

    public class Table implements Serializable
    {
        private ArrayList<Player> players;
        private int dealerID;
        private int smallBlindID;
        private int bigBlindID;
        private int focusedPlayerID;

        public Table(ArrayList<Player> players)
        {
            this.players = players;
        }

        public ArrayList<Player> getPlayers() { return players; }

        public int getDealerID() { return dealerID; }

        public void setDealerID(int dealerID) { this.dealerID = dealerID; }

        public int getSmallBlindID() { return smallBlindID; }

        public int getBigBlindID() { return bigBlindID; }

        public void setFocusedPlayer(int playerID)
        {
            this.focusedPlayerID = playerID;
        }

        public int getFocusedPlayer() { return focusedPlayerID; }

        public void addPlayer(int playerID, Player player)
        {
            this.players.set(playerID, player);
        }

        public void removePlayer(int playerID)
        {
            this.players.set(playerID, null);
        }

        // Método para avançar para a próxima rodada (avança Dealer, Small Blind e Big Blind)
        public void nextTableHand()
        {
            this.players.get(dealerID).setDealer(false);
            int nextDealerID = getNextValidPlayer(dealerID);
            this.players.get(nextDealerID).setDealer(true);

            this.players.get(nextDealerID).setSmallBlind(false);
            int nextSmallBlindID = getNextValidPlayer(nextDealerID);
            this.players.get(nextSmallBlindID).setSmallBlind(true);

            this.players.get(nextSmallBlindID).setBigBlind(false);
            int nextBigBlindID = getNextValidPlayer(nextSmallBlindID);
            this.players.get(nextBigBlindID).setBigBlind(true);

            nextTurn();
        }

        // Método para avançar para o próxima turno (avança o Focused Player)
        public void nextTurn()
        {
            focusedPlayerID = getNextValidPlayer(focusedPlayerID);
        }

        protected int getNextValidPlayer(int currentPlayer)
        {
            int nextPlayer  = currentPlayer;

            do{
                nextPlayer++;
                if(nextPlayer == 10) nextPlayer = 0;
            }while(players.get(nextPlayer) == null || players.get(nextPlayer).isFolded() || !players.get(nextPlayer).isPlaying());

            return nextPlayer;
        }
    }

    // Se aumenta os blinds
    private boolean autoRaise;
    // Quantidade inicial da "big blind"
    private int blind;
    // Quando aumenta a "big blind"
    private int every;
    // Multiplicador da "big blind"
    private float multiplier;
    // Quantidade apostada na rodada
    private int tableBet;

    // Getters e Setters
    public int getTableBet() {
        return tableBet;
    }

    public void setTableBet(int apostaMesa) {
        this.tableBet = tableBet;
    }

    public boolean isAutoRaise() {
        return autoRaise;
    }

    public int getEvery() {
        return every;
    }

    public Table getTable() { return table; }

    public float getMultiplier() {
        return multiplier;
    }
    
    // Construtor
    public Game(ArrayList<Player> players, boolean autoRaise, int blind, int every, float multiplier) {
        this.table = new Table(players);
        this.autoRaise = autoRaise;
        this.blind = blind;
        this.every = every;
        this.multiplier = multiplier;
        this.tableBet = 0;
    }

    public void startGame()
    {
        int dealerID = selectDealerID();
        table.setDealerID(dealerID);
        nextHand();
        // Setar outras informações necessaŕias para iniciar o game
    }

    public void nextHand()
    {
        table.nextTableHand();
        // resetar as cartas
        // resetar os pots
        // etc
        // Incrementar um contador de hands e verificar se deve fazer auto raise
    }

    //Função referente a situação de quando a aposta da mesa é 0 (check) ou diferente de 0 (Call)
    public void call()
    {
        table.players.get(table.getFocusedPlayer()).bet(tableBet);
        table.nextTurn();
    }

    //Função para aumentar a aposta da rodada
    public void raise(int bet)
    {
        int moment;
        moment = table.getFocusedPlayer();
        bet = bet + tableBet;
        tableBet = bet;
        //moment.bet(bet);
        table.nextTurn();
    }

    //Função para sair do rodada
    public void fold()
    {
        table.players.get(table.getFocusedPlayer()).fold();
        table.nextTurn();
    }

    private int selectDealerID()
    {
        Random rand = new Random();
        return table.getNextValidPlayer(rand.nextInt(10));
    }
}
