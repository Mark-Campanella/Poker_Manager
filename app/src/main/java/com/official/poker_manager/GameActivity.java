package com.official.poker_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    private Game game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Recuperando o objeto Game criado em SetupActivity
        if(getIntent().getExtras() != null) {
            game = (Game) getIntent().getSerializableExtra("game");
        }

        //Criar o map de jogadores também com base no Intent de Setup

        //game = new Game();

        //OnClick do botão call
            //get

        //OnClick do botão raise

        //OnClick do botão fold
    }
}