package com.official.poker_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private Game game;
    private ArrayList<Player> players;
    private ArrayList<ImageView> cards;
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

        // Inicializa a array de cartas
        cards = new ArrayList<ImageView>(Collections.nCopies(5, null));
        for (int i = 0; i < 5; i++) {
            cards.set(i, (ImageView) findViewById(getResources().getIdentifier("card_" + String.valueOf(i), "id", getPackageName())));
        }

        startGameActivity();
        
        // Botão de call ou check
        Button btnCheckCall = findViewById(R.id.btn_check_call);
        btnCheckCall.setOnClickListener(v -> {
            call();
        });
        
        // Botão de fold
        Button btnFold = findViewById(R.id.btn_fold);
        btnFold.setOnClickListener(v -> {
            fold();
        });
        
        // Botão de raise
        Button btnRaise = findViewById(R.id.btn_raise);
        btnRaise.setOnClickListener(v -> {
            raise();
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

        game.startGame();

        seatViewsMap.get(game.getTable().getDealerID()).txtRoundRole.setText(R.string.role_dealer);

        seatViewsMap.get(game.getTable().getSmallBlindID()).txtRoundRole.setText(R.string.role_small_blind);

        seatViewsMap.get(game.getTable().getBigBlindID()).txtRoundRole.setText(R.string.role_big_blind);

        seatViewsMap.get(game.getTable().getFocusedPlayer()).edtxtPlayerName.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.turn));
    }
    
    // Atualiza as informações das views a cada call/check, fold ou raise
    private void updateGameActivity() {
        // Atualiza as informações de todos os jogadores
        for(int i = 0; i < 10; i++) {
            if(players.get(i) != null) {
                seatViewsMap.get(i).txtChipsTotal.setText((CharSequence) String.valueOf(players.get(i).getChips()));
                seatViewsMap.get(i).txtRoundChipsBetted.setText((CharSequence) String.valueOf(players.get(i).getRoundChipsBetted()));
                seatViewsMap.get(i).txtRoundRole.setText("");
                if (players.get(i).isFolded())
                    seatViewsMap.get(i).edtxtPlayerName.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.folded));
                else
                    seatViewsMap.get(i).edtxtPlayerName.setBackgroundTintList(null);
            }
        }
        
        // Atualiza as informações de quem é o dealer, small blind, big blind e de quem é a vez
        seatViewsMap.get(game.getTable().getDealerID()).txtRoundRole.setText(R.string.role_dealer);
        seatViewsMap.get(game.getTable().getSmallBlindID()).txtRoundRole.setText(R.string.role_small_blind);
        seatViewsMap.get(game.getTable().getBigBlindID()).txtRoundRole.setText(R.string.role_big_blind);
        seatViewsMap.get(game.getTable().getFocusedPlayer()).edtxtPlayerName.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.turn));
        
        // Atualiza as cartas
        if (game.getCards() < 6)
            for (int i = 0; i < game.getCards(); i++)
                this.cards.get(i).setImageResource(R.drawable.carta_espadas_vector);
        else {
            // TODO: Lógica de vencer a hand
            Toast.makeText(GameActivity.this, "Parabéns!!! Você venceu (REAL!!! - Não é fake)", Toast.LENGTH_SHORT).show();
        }
    }
    
    // Ações de call
    private void call () {
        if(game.getTableBet() < players.get(game.getTable().getFocusedPlayer()).getChips())
            game.call();
        else
            new AlertDialog.Builder(this)
                    .setTitle("Insufficient funds")
                    .setMessage("You don't have enough funds to call this bet!")
                    .setPositiveButton(R.string.ok, null)
                    .show();
        updateGameActivity();
    }
    
    // Ações de fold
    private void fold () {
        game.fold();
        updateGameActivity();
    }
    
    // Ações de raise
    private void raise() {
        ValueViewModel viewModel = new ViewModelProvider(this).get(ValueViewModel.class);
        viewModel.getValue().observe(this, value -> {
            int raiseValue = value.intValue();
            // Se o valor do raise for válido, chamar o método raise (entre aposta da mesa+1 e all-in)
            if (raiseValue >= (this.game.getTableBet() + 1) && raiseValue+game.getTableBet() <= players.get(game.getTable().getFocusedPlayer()).getChips())
                game.raise(raiseValue);
            // Se for -1, é all-in
            else if (raiseValue == -1)
                game.raise(players.get(game.getTable().getFocusedPlayer()).getChips());
            // Senão, é um valor inválido e exibe um AlertDialog
            else
                new AlertDialog.Builder(this)
                        .setTitle("Insufficient funds")
                        .setMessage("You don't have enough funds to raise in this bet!")
                        .setPositiveButton(R.string.ok, null)
                        .show();
            updateGameActivity();
        });

        // Exibir um pop-up para o usuário digitar o valor do raise
        DialogFragment dialog = new raiseDialog();
        dialog.show(getSupportFragmentManager(), "Raise");
    }
}