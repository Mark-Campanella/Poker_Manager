package com.official.poker_manager;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Game implements Serializable
{
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
            this.dealerID = nextDealerID;
            this.players.get(nextDealerID).setDealer(true);

            this.players.get(nextDealerID).setSmallBlind(false);
            int nextSmallBlindID = getNextValidPlayer(nextDealerID);
            this.smallBlindID = nextSmallBlindID;
            this.players.get(nextSmallBlindID).setSmallBlind(true);

            this.players.get(nextSmallBlindID).setBigBlind(false);
            int nextBigBlindID = getNextValidPlayer(nextSmallBlindID);
            this.bigBlindID = nextBigBlindID;
            this.players.get(nextBigBlindID).setBigBlind(true);

            int nextFocusedPlayerID = getNextValidPlayer(nextBigBlindID);
            this.setFocusedPlayer(nextFocusedPlayerID);
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
                if(nextPlayer == -1)
                {
                    nextPlayer = 9;
                }
            }while(players.get(nextPlayer) == null || players.get(nextPlayer).isFolded() || !players.get(nextPlayer).isPlaying());

            return nextPlayer;
        }

        public ArrayList<Integer> getValidPlayers()
        {
            ArrayList<Integer> validPlayers = new ArrayList<>();

            for(int i = 0; i < 10; i++)
            {
                Player player = this.players.get(i);
                if(player != null && !player.isFolded() && player.isPlaying())
                {
                    validPlayers.add(i);
                }
            }

            return validPlayers;
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
    // Último jogador a aumentar a aposta
    private int lastPlayerToRaise;
    private int tempLastPlayerToRaise;
    private boolean setLastPlayerToRaise;
    private int handsCount;
    private int remainedHandToAutoRaise;

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
    
    public void setCards(int cards) {
        this.cards = cards;
    }

    public int getBlind() {
        return blind;
    }

    public int getHandsCount() { return handsCount; }

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

        table.players.get(table.getBigBlindID()).bet(blind);
        table.players.get(table.getSmallBlindID()).bet(blind/2);
        lastPlayerToRaise = underTheGun;
        setLastPlayerToRaise = false;
        handsCount = 0;
        remainedHandToAutoRaise = 0;
    }

    // Função referente a situação de quando a aposta da mesa é 0 (check) ou diferente de 0 (Call)
    public void call()
    {
        Player alexandre = table.players.get(table.getFocusedPlayer());
        alexandre.bet(tableBet-alexandre.getRoundChipsBetted());
        table.nextTurn();

        if(checkNextRound() && table.getFocusedPlayer() == lastPlayerToRaise)
        {
            nextRound();
        }
    }

    // Função para aumentar a aposta da rodada
    public void raise(int bet)
    {
        tableBet += bet;
        lastPlayerToRaise = table.getFocusedPlayer();
        Player alexandre = table.players.get(table.getFocusedPlayer());
        alexandre.bet(tableBet-alexandre.getRoundChipsBetted());

        table.nextTurn();
    }

    // Função para sair do rodada
    public void fold()
    {
        table.players.get(table.getFocusedPlayer()).fold();
        if(lastPlayerToRaise == table.getFocusedPlayer())
        {
            table.nextTurn();
            setLastPlayerToRaise = true;
            tempLastPlayerToRaise = table.getFocusedPlayer();
        }
        else
        {
            table.nextTurn();
        }

        // Verificar se não sobrou apenas uma pessoa na mesa
        if(checkOnlyOnePlayer())
        {
            // Dar vitória ao focusedPlayer
        }

        if(checkNextRound() && table.getFocusedPlayer() == lastPlayerToRaise)
        {
            nextRound();
        }

        // Caso segundo round, o primeiro jogador dê fold
        if(setLastPlayerToRaise)
        {
            lastPlayerToRaise = tempLastPlayerToRaise;
            setLastPlayerToRaise = false;
        }
    }

    // Função para o fim de uma rodada
    private void nextRound()
    {
        if(firstRound)
        {
            cards += 3;
            firstRound = false;
        }
        else
        {
            cards += 1;
        }

        pot += withdrawChips();
        tableBet = 0;
        if(!table.players.get(table.getSmallBlindID()).isFolded())
        {
            table.setFocusedPlayer(table.getSmallBlindID());
            lastPlayerToRaise = table.getSmallBlindID();
        }
        else
        {
            table.setFocusedPlayer(table.getNextValidPlayer(table.getSmallBlindID()));
            lastPlayerToRaise = table.getNextValidPlayer(table.getSmallBlindID());
        }
    }

    public void nextHand(ArrayList<Integer> winners)
    {
        int quantityWinners = winners.size();
        int chipsPerWinner = pot/quantityWinners;

        for(int winner : winners)
        {
            table.players.get(winner).addChips(chipsPerWinner);
        }

        // Verificando se o jogador foi eliminado e resentando folds
        for(int i = 0; i < 10; i++)
        {
            Player player = table.players.get(i);

            if(player != null && player.isPlaying())
            {
                player.setIsFolded(false);

                if(player.getChips() == 0)
                {
                    table.removePlayer(i);
                }
            }
        }

        table.nextTableHand();
        table.players.get(table.getBigBlindID()).bet(blind);
        table.players.get(table.getSmallBlindID()).bet(blind/2);
        lastPlayerToRaise = table.getFocusedPlayer();

        cards = 0;
        pot = 0;
        handsCount++;
        firstRound = true;
        tableBet = blind;

        remainedHandToAutoRaise++;
        if(autoRaise && remainedHandToAutoRaise >= every)
        {
            performeAutoRaise();
        }
    }

    private void performeAutoRaise()
    {
        remainedHandToAutoRaise = 0;
        blind = (int) (blind * multiplier);
    }

    // Verifica se todos os jogadores em jogo tem sua aposta igualada à referência da mesa
    private boolean checkNextRound()
    {
        for(Player player: table.getPlayers())
        {
            if(player != null && !player.isFolded() && player.isPlaying())
            {
                if(!(player.getRoundChipsBetted() == tableBet)) return false;
            }
        }

        return true;
    }

    private boolean checkOnlyOnePlayer()
    {
        int count_players = 0;

        for(Player player: table.getPlayers())
        {
            if(player != null && !player.isFolded() && player.isPlaying())
            {
                count_players++;
            }
        }
        return count_players == 1;
    }

    // Retorna a soma das apostas da rodada e zera as apostas atuais
    private int withdrawChips()
    {
        int chips = 0;

        for(Player player: table.getPlayers())
        {
            if (player != null && player.isPlaying())
            {
                chips += player.getRoundChipsBetted();
                player.setRoundChipsBetted(0);
            }
        }

        return chips;
    }

    private int selectDealerID()
    {
        Random rand = new Random();
        return table.getNextValidPlayer(rand.nextInt(10));
    }
}
