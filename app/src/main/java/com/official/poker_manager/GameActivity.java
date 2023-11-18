package com.official.poker_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
        if(getIntent().getExtras() != null) {
            game = (Game) getIntent().getSerializableExtra("game");
        }

        for(int i = 0; i < 10; i++)
        {
            //seatViewsMap.put(i, new SeatViews());
            //seatViewsMap.get(i).edtxtPlayerName = game.getPlayers ?
        }

        //OnClick do botão call
            //get

        //OnClick do botão raise

        //OnClick do botão fold
    }
}