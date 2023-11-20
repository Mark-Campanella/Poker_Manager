package com.official.poker_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Hashtable;

public class GameActivity extends AppCompatActivity {
    private Game game;
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
        if (getIntent().getExtras() != null) {
            game = (Game) getIntent().getSerializableExtra("game");
        }

        for (int i = 0; i < 10; i++) {
            //seatViewsMap.put(i, new SeatViews());
            //seatViewsMap.get(i).edtxtPlayerName = game.getPlayers ?
        }
        
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
                game.raise(value);
            });
            
            // Exibir um pop-up para o usuário digitar o valor do raise
            DialogFragment dialog = new InputValueDialog();
            dialog.show(getSupportFragmentManager(), "Raise");
        });
        
    }
}