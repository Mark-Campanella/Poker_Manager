package com.official.poker_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private Game game;
    private ArrayList<Player> players;
    private final Hashtable<Integer, SeatViews> seatViewsMap = new Hashtable<Integer, SeatViews>(10);
    private class SeatViews
    {
        public EditText edtxtPlayerName;
        public TextView txtChipsTotal;
        public TextView txtRoundChipsBetted;
        public TextView txtRoundRole;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Recuperando o objeto Game criado em SetupActivity
        if(getIntent().getExtras() != null)
        {
            game = (Game) getIntent().getSerializableExtra("game");
        }

        players = game.getTable().getPlayers();

        startGameActivity();
        
        // Botão de call ou check
        Button btnCheckCall = findViewById(R.id.btn_check_call);
        btnCheckCall.setOnClickListener(v -> {
            // TODO: lógica se o botão for check ou call
        });
        
        // Botão de fold
        Button btnFold = findViewById(R.id.btn_fold);
        btnFold.setOnClickListener(v -> {
            game.fold();
        });
        
        // Botão de raise
        Button btnRaise = findViewById(R.id.btn_raise);
        btnRaise.setOnClickListener(v -> {
            ValueViewModel viewModel = new ViewModelProvider(this).get(ValueViewModel.class);
            viewModel.getValue().observe(this, value -> {
                // TODO: descomentar o código abaixo quando o raise for implementado
                // Se o valor do raise for válido, chamar o método raise (entre aposta da mesa+1 e all-in)
                //if (value >= this.game.table.bet + 1 && value <= game.table.getFocusedPlayer().getChips())
                    game.raise(value);
                // Se for -1, é all-in
                //else if (value == -1)
                    //game.raise(game.table.getFocusedPlayer().getChips());
                // Senão, é um valor inválido e exibe um AlertDialog
                //else
                    //new AlertDialog.Builder(this)
                            //.setTitle("Valor inválido")
                            //.setMessage("Valor de aposta inválido!")
                            //.setPositiveButton("OK", null)
                            //.show();
            });
            
            // Exibir um pop-up para o usuário digitar o valor do raise
            DialogFragment dialog = new raiseDialog();
            dialog.show(getSupportFragmentManager(), "Raise");
        });
    }

    // Inicializa as informações das views
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void startGameActivity()
    {
        // Faz o bind de todas as views e configura seus valores
        for(int i = 0; i < 10; i++)
        {
            seatViewsMap.put(i, new SeatViews());
            seatViewsMap.get(i).edtxtPlayerName = findViewById(getResources().getIdentifier("seat_" + String.valueOf(i), "id", getPackageName()));
            seatViewsMap.get(i).txtChipsTotal = findViewById(getResources().getIdentifier("txt_chips_" + String.valueOf(i), "id", getPackageName()));
            seatViewsMap.get(i).txtRoundChipsBetted = findViewById(getResources().getIdentifier("txt_bet_" + String.valueOf(i), "id", getPackageName()));
            seatViewsMap.get(i).txtRoundRole = findViewById(getResources().getIdentifier("txt_role_" + String.valueOf(i), "id", getPackageName()));
            if(players.get(i) != null)
            {
                seatViewsMap.get(i).edtxtPlayerName.setText((CharSequence) players.get(i).getName());
                seatViewsMap.get(i).txtChipsTotal.setText((CharSequence) String.valueOf(players.get(i).getChips()));
            }
        }

        int dealerID = selectDealerID();
        players.get(dealerID).setDealer(true);
        seatViewsMap.get(dealerID).txtRoundRole.setText(R.string.role_dealer);

        int smallBlidID = getNextValidPlayer(dealerID);
        players.get(smallBlidID).setSmallBlind(true);
        seatViewsMap.get(smallBlidID).txtRoundRole.setText(R.string.role_small_blind);

        int bigBlidID = getNextValidPlayer(smallBlidID);
        players.get(bigBlidID).setBigBlind(true);
        seatViewsMap.get(bigBlidID).txtRoundRole.setText(R.string.role_big_blind);

        // Primeiro a jogar
        int firstFocusedPlayer = getNextValidPlayer(bigBlidID);
        game.getTable().setFocusedPlayer(firstFocusedPlayer);
        seatViewsMap.get(firstFocusedPlayer).edtxtPlayerName.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.turn));

        // # TODO fazer um startGame em Game e chamar aqui
    }

    private int selectDealerID()
    {
        Random rand = new Random();
        return getNextValidPlayer(rand.nextInt(10));
    }

    private int getNextValidPlayer(int actualFocusedPlayer)
    {
        int nextFocusedPlayer  = actualFocusedPlayer;

        do{
            nextFocusedPlayer++;
            if(nextFocusedPlayer == 10) nextFocusedPlayer = 0;
        }while(players.get(nextFocusedPlayer) == null || players.get(nextFocusedPlayer).isFolded() || !players.get(nextFocusedPlayer).isPlaying());

        return nextFocusedPlayer;
    }
}