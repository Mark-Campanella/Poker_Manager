package com.official.poker_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class SetupActivity extends AppCompatActivity {
    // Mapa de assentos para os jogadores
    private final Hashtable<Integer, EditText> seatsMap = new Hashtable<Integer, EditText>(10);
    // Texto do multiplicador de blinds
    private TextView txtMultiplier;
    // Valor do multiplicador de blinds
    private float multiplier = 1.5f;
    // Switch de auto raise
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchAutoRaise;
    // Texto de chips iniciais
    private TextInputEditText edtxtChipsTotal;
    // Texto de big blind
    private TextInputEditText edtxtBlind;
    // Texto de every
    private TextInputEditText edtxtEvery;
    // Versão do Android
    private int currentApiVersion;

    public void debugFlamengo()
    {
        Toast.makeText(getApplicationContext(), "FLAMENGO", Toast.LENGTH_LONG).show();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        
        // Mapeia os "assentos" ao número dos jogadores
        seatsMap.put(0, (EditText) findViewById(R.id.seat_0));
        seatsMap.put(1, (EditText) findViewById(R.id.seat_1));
        seatsMap.put(2, (EditText) findViewById(R.id.seat_2));
        seatsMap.put(3, (EditText) findViewById(R.id.seat_3));
        seatsMap.put(4, (EditText) findViewById(R.id.seat_4));
        seatsMap.put(5, (EditText) findViewById(R.id.seat_5));
        seatsMap.put(6, (EditText) findViewById(R.id.seat_6));
        seatsMap.put(7, (EditText) findViewById(R.id.seat_7));
        seatsMap.put(8, (EditText) findViewById(R.id.seat_8));
        seatsMap.put(9, (EditText) findViewById(R.id.seat_9));
        
        // Atribui o botão de aumentar o multiplicador de blinds
        Button btnPlus = findViewById(R.id.btn_plus);
        // Atribui o texto do multiplicador de blinds
        txtMultiplier = findViewById(R.id.txt_multiplier);
        // Exibe o valor do multiplicador de blinds
        txtMultiplier.setText(String.format("%.1f", multiplier));
        // Aumenta o multiplicador de blinds ao tocar no botão
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiplier < 10.0f) {
                    multiplier += 0.5f;
                    txtMultiplier.setText(String.format("%.1f", multiplier));
                }
            }
        });
        
        // Atribui o botão de diminuir o multiplicador de blinds
        Button btnMinus = findViewById(R.id.btn_minus);
        // Diminui o multiplicador de blinds ao tocar no botão
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                if (multiplier > 1.0f) {
                    multiplier -= 0.5f;
                    txtMultiplier.setText(String.format("%.1f", multiplier));
                }
            }
        });
        
        // Atribui o switch de auto raise
        switchAutoRaise = findViewById(R.id.switch_auto_raise);
        // Desabilita ou habilita as views relacionadas com o auto raise
        switchAutoRaise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    edtxtEvery.setEnabled(true);
                    btnPlus.setEnabled(true);
                    btnMinus.setEnabled(true);
                    txtMultiplier.setEnabled(true);
                }
                else
                {
                    edtxtEvery.setEnabled(false);
                    btnPlus.setEnabled(false);
                    btnMinus.setEnabled(false);
                    txtMultiplier.setEnabled(false);
                }
            }
        });

        // Atribui o texto de chips iniciais
        edtxtChipsTotal = findViewById(R.id.edtxt_chips_total);
        
        // Atribui o texto de big blind
        edtxtBlind = findViewById(R.id.edtxt_big_blind);
        
        // Atribui o texto de "every"
        edtxtEvery = findViewById(R.id.edtxt_every);
        
        // Botão de iniciar o jogo
        ImageButton btnStartMatch = findViewById(R.id.btn_start_match);
        
        // Inicia a atividade de jogo ao tocar no botão
        btnStartMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pega o valor do switch
                boolean autoRaise = switchAutoRaise.isChecked();

                // Verifica se os campos númericos não estão vazios
                if(edtxtChipsTotal.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Adicione um valor total de fichas", Toast.LENGTH_LONG).show();
                    return;
                }

                if(edtxtBlind.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Adicione um valor para o blind", Toast.LENGTH_LONG).show();
                    return;
                }

                if(autoRaise && edtxtEvery.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Adicione um valor de every", Toast.LENGTH_LONG).show();
                    return;
                }

                // Pega o valor das fichas
                int chipsTotal = Integer.parseInt(edtxtChipsTotal.getText().toString());
                
                // Pega o valor da big blind
                int bigBlind = Integer.parseInt(edtxtBlind.getText().toString());
                
                // Pega o valor do every
                int every = 0;
                if(autoRaise)
                {
                    every = Integer.parseInt(edtxtEvery.getText().toString());
                }

                // Declara o mapa de posições e jogadores
                ArrayList<Player> players = new ArrayList<Player>(Collections.nCopies(10, null));

                // Itera sobre o mapa de posições
                int numPlayers = 0;
                for(int i = 0; i < 10; i++)
                {
                    TextView seat = seatsMap.get(i);

                    if(!seat.getText().toString().isEmpty())
                    {
                        // Pega o nome do jogador
                        String name = seat.getText().toString();
                        
                        // Cria o jogador
                        Player player = new Player(name, chipsTotal, true);
                        
                        // Adiciona o jogador ao mapa de posições e jogadores
                        players.set(i, player);

                        numPlayers++;
                    }
                }

                if(numPlayers < 3)
                {
                    Toast.makeText(getApplicationContext(), "Adicione ao menos três jogadores", Toast.LENGTH_LONG).show();
                    return;
                }
                
                // Cria o jogo
                Game game = new Game(players, autoRaise, bigBlind, every, multiplier);

                // Inicia a atividade de jogo
                startActivity(new Intent(SetupActivity.this, GameActivity.class).putExtra("game", game));
            }
        });
        
        // Daqui para baixo, o código deixa a atividade em tela cheia
        // Retirado de https://stackoverflow.com/questions/21724420/how-to-hide-navigation-bar-permanently-in-android-activity
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {
            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }
    }

    // Também retirado de https://stackoverflow.com/questions/21724420/how-to-hide-navigation-bar-permanently-in-android-activity
    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}