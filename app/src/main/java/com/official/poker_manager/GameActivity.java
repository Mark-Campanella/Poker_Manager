package com.official.poker_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    private Game game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Pegar dados do setup usando o Intent e passar para o construtor de game
        //Criar o map de jogadores também com base no Intent de Setup

        //game = new Game();

        //OnClick do botão call
            //get

        //OnClick do botão raise

        //OnClick do botão fold
    }
}