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

        public void setSmallBlindID(int smallBlindID) { this.smallBlindID = smallBlindID; }

        public int getBigBlindID() { return bigBlindID; }

        public void setBigBlindID(int bigBlindID) { this.bigBlindID = bigBlindID; }

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
                nextPlayer--;
                if(nextPlayer == -1){
                    nextPlayer = 9;
                    roundOver = true;
                }
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
    // Quantidade total apostada
    private  int pot;
    // Quantidade de cartas já viradas
    private int cards;
    // Se for a primeira rodada
    private boolean firstRound;
    // Se voltou no primeiro jogador
    private boolean roundOver;

    // Getters e Setters
    public int getTableBet() {
        return tableBet;
    }

    public int getPot() {
        return pot;
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

    public int getCards() {
        return cards;
    }

    // Construtor
    public Game(ArrayList<Player> players, boolean autoRaise, int blind, int every, float multiplier) {
        this.table = new Table(players);
        this.autoRaise = autoRaise;
        this.blind = blind;
        this.every = every;
        this.multiplier = multiplier;
        this.tableBet = blind;
        this.pot = 0;
        this.cards = 0;
        this.firstRound = true;
        this.roundOver = false;
    }

    public void startGame()
    {
        int dealerID = selectDealerID();
        table.setDealerID(dealerID);
        table.players.get(dealerID).setDealer(true);

        int smallBlind = table.getNextValidPlayer(dealerID);
        table.setSmallBlindID(smallBlind);
        table.players.get(smallBlind).setSmallBlind(true);

        int bigBlind = table.getNextValidPlayer(smallBlind);
        table.setBigBlindID(bigBlind);
        table.players.get(bigBlind).setBigBlind(true);

        int underTheGun = table.getNextValidPlayer(bigBlind);
        table.setFocusedPlayer(underTheGun);

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

        if (roundOver && tableBet == blind)
            nextRound();
    }

    //Função para aumentar a aposta da rodada
    public void raise(int bet)
    {
        tableBet += bet;
        table.players.get(table.getFocusedPlayer()).bet(tableBet);
        
        table.nextTurn();
    }

    //Função para sair do rodada
    public void fold()
    {
        table.players.get(table.getFocusedPlayer()).fold();
        table.nextTurn();
        
        if (roundOver && tableBet == blind)
            nextRound();
    }
    
    // Função para o fim de uma rodada
    private void nextRound() {
        if(firstRound) {
            cards += 3;
            firstRound = false;
        }
        else
            cards += 1;
        
        roundOver = false;
        pot += tableBet;
        tableBet = 0;
    }

    private int selectDealerID()
    {
        Random rand = new Random();
        return table.getNextValidPlayer(rand.nextInt(10));
    }
}
