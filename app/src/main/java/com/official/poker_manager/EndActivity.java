package com.official.poker_manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndActivity extends AppCompatActivity {
    public String playerName;

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        // Recuperando o nome do vencedor
        if (null != getIntent().getExtras())
            playerName = (String) getIntent().getSerializableExtra("playerName");
        
        Button btn_restart = findViewById(R.id.btn_restart_game);
        TextView txt_winner_name = findViewById(R.id.txt_winner);
        txt_winner_name.setText(playerName);

        // Botão para reiniciar o jogo
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EndActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}