package com.official.poker_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {
    private String playerName;

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        long sleepytime = 500;
        Button btn_restart = (Button) findViewById(R.id.btn_restart_game);
        ImageView easter_egg = (ImageView) findViewById(R.id.easter);
        TextView txt_winner_name = (TextView) findViewById(R.id.txt_winner);



        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                easter_egg.setVisibility(View.VISIBLE);
                try {
                    Thread.sleep(sleepytime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                easter_egg.setVisibility(View.INVISIBLE);
                startActivity(new Intent(EndActivity.this, MainActivity.class));
            }
        });
    }


}