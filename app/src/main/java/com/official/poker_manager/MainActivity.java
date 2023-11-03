package com.official.poker_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    // Botão de iniciar o jogo
    private Button btnStartGame;
    // Botão de "sobre"
    private Button btnAbout;
    // Botão de sair
    private Button btnQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Atribui o botão de iniciar o jogo
        btnStartGame = findViewById(R.id.btn_start_game);
        // Inicia a atividade de setup ao tocar no botão
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SetupActivity.class));
            }
        });

        // Atribui o botão de "sobre"
        Button btnAbout = findViewById(R.id.btn_about);
        // Abre o link de "sobre" ao tocar no botão
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });

        // Atribui o botão de quit
        Button btnQuit = findViewById(R.id.btn_quit);
        // Fecha o aplicativo ao tocar no botão
        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
    }
}