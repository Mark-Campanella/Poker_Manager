package com.official.poker_manager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class GameActivity extends AppCompatActivity {
    private final Hashtable<Integer, SeatViews> seatViewsMap = new Hashtable<>(10);
    private Game game;
    private ArrayList<Player> players;
    private ArrayList<ImageView> cards;
    private ArrayList<Integer> winners;
    private AppCompatImageButton btnConfirm;
    private Button btnCheckCall;
    private Button btnFold;
    private Button btnRaise;

    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Recuperando o objeto Game criado em SetupActivity
        if (null != getIntent().getExtras()) {
            game = (Game) getIntent().getSerializableExtra("game");
        }

        players = game.getTable().getPlayers();

        // Inicializa a array de cartas
        cards = new ArrayList<ImageView>(Collections.nCopies(5, null));
        for (int i = 0; 5 > i; i++) {
            cards.set(i, findViewById(getResources().getIdentifier("card_" + i, "id", getPackageName())));
        }

        startGameActivity();

        //
        winners = new ArrayList<>();

        //

        // Botão de call ou check
        btnCheckCall = findViewById(R.id.btn_check_call);
        btnCheckCall.setOnClickListener(v -> {
            call();
        });

        // Botão de fold
        btnFold = findViewById(R.id.btn_fold);
        btnFold.setOnClickListener(v -> {
            fold();
        });

        // Botão de raise
        btnRaise = findViewById(R.id.btn_raise);
        btnRaise.setOnClickListener(v -> {
            raise();
        });
    }

    // Inicializa as informações das views
    @SuppressLint({"UseCompatLoadingForColorStateLists", "WrongViewCast", "DiscouragedApi"})
    private void startGameActivity() {
        game.startGame();

        // Inicializa a array de cartas
        cards = new ArrayList<ImageView>(Collections.nCopies(5, null));
        for (int i = 0; 5 > i; i++) {
            cards.set(i, findViewById(getResources().getIdentifier("card_" + i, "id", getPackageName())));
        }

        // Inicializa a ViewModel usada em raise
        ValueViewModel viewModel = new ViewModelProvider(this).get(ValueViewModel.class);
        viewModel.getValue().observe(this, value -> {
            int raiseValue = value.intValue();
            // Se o valor do raise for válido, chamar o método raise (entre aposta da mesa+1 e all-in)
            if (raiseValue >= this.game.getBlind() && raiseValue + game.getTableBet() - players.get(game.getTable().getFocusedPlayer()).getRoundChipsBetted() <= players.get(game.getTable().getFocusedPlayer()).getChips())
                game.raise(raiseValue);
                // Se for -1, é all-in
            else if (-1 == raiseValue)
                game.raise(players.get(game.getTable().getFocusedPlayer()).getChips() - players.get(game.getTable().getFocusedPlayer()).getRoundChipsBetted());
                // Senão, é um valor inválido e exibe um AlertDialog
            else
                new AlertDialog.Builder(this)
                        .setTitle("Invalid raise value")
                        .setMessage("The value you are trying to raise is too low or you don't have enough funds to raise in this bet!")
                        .setPositiveButton(R.string.ok, null)
                        .show();
            updateGameActivity();
        });

        // Faz o bind de todas as views e configura seus valores
        for (int i = 0; 10 > i; i++) {
            seatViewsMap.put(i, new SeatViews());
            seatViewsMap.get(i).edtxtPlayerName = findViewById(getResources().getIdentifier("seat_" + i, "id", getPackageName()));
            seatViewsMap.get(i).txtChipsTotal = findViewById(getResources().getIdentifier("txt_chips_" + i, "id", getPackageName()));
            seatViewsMap.get(i).txtRoundChipsBetted = findViewById(getResources().getIdentifier("txt_bet_" + i, "id", getPackageName()));
            seatViewsMap.get(i).txtRoundRole = findViewById(getResources().getIdentifier("txt_role_" + i, "id", getPackageName()));
            if (null != players.get(i)) {
                seatViewsMap.get(i).edtxtPlayerName.setText(players.get(i).getName());
                seatViewsMap.get(i).txtChipsTotal.setText(String.valueOf(players.get(i).getChips()));
            }
            int finalI = i;
            seatViewsMap.get(i).edtxtPlayerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!winners.contains(finalI)) {
                        winners.add(finalI);
                        seatViewsMap.get(finalI).edtxtPlayerName.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.checked));
                    } else {
                        winners.remove((Object) finalI);
                        seatViewsMap.get(finalI).edtxtPlayerName.setBackgroundTintList(null);
                    }
                }
            });
        }
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (1 > winners.size()) {
                    Toast.makeText(GameActivity.this, "Selecione ao menos um vencedor", Toast.LENGTH_LONG).show();
                    return;
                }


                btnConfirm.setEnabled(false);
                btnConfirm.setVisibility(View.INVISIBLE);
                ArrayList<Integer> validPlayers = game.getTable().getValidPlayers();
                for (int validPlayer : validPlayers) {
                    seatViewsMap.get(validPlayer).edtxtPlayerName.setClickable(false);
                    seatViewsMap.get(validPlayer).edtxtPlayerName.setEnabled(false);
                }
                //Reabilitando os botões de controle (call, raise e fold)
                btnCheckCall.setEnabled(true);
                btnRaise.setEnabled(true);
                btnFold.setEnabled(true);

                nextHandGameActivity();
            }
        });

        updateGameActivity();
    }

    // Atualiza as informações das views a cada call/check, fold ou raise
    private void updateGameActivity() {
        int pot = 0;
        int playerCount = 0;
        int playersBalance = 0;
        int activePlayerCount = 0;
        // Atualiza as informações de todos os jogadores
        for (int i = 0; 10 > i; i++) {
            if (null != players.get(i)) {
                pot += players.get(i).getRoundChipsBetted();
                playerCount++;

                seatViewsMap.get(i).txtChipsTotal.setText(String.valueOf(players.get(i).getChips()));
                seatViewsMap.get(i).txtRoundChipsBetted.setText(String.valueOf(players.get(i).getRoundChipsBetted()));
                seatViewsMap.get(i).txtRoundRole.setText("");
                if (players.get(i).isFolded())
                    seatViewsMap.get(i).edtxtPlayerName.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.folded));
                else {
                    seatViewsMap.get(i).edtxtPlayerName.setBackgroundTintList(null);
                    playersBalance += players.get(i).getChips();
                    activePlayerCount++;
                }
            }
        }

        // Condições de "vitória imediata"
        if (0 == playersBalance || 1 == activePlayerCount) {
            game.setCards(6);
        }

        // Atualiza as informações de quem é o dealer, small blind, big blind e de quem é a vez
        // Se só tiver dois jogadores, o update deve operar de modo diferente:
        // O Dealer/BigBlind recebe a role D/B
        // E o SmallBlind continua igual
        if (2 < playerCount) {
            seatViewsMap.get(game.getTable().getDealerID()).txtRoundRole.setText(R.string.role_dealer);
            seatViewsMap.get(game.getTable().getBigBlindID()).txtRoundRole.setText(R.string.role_big_blind);
        } else {
            seatViewsMap.get(game.getTable().getDealerID()).txtRoundRole.setText(R.string.DealerBB);
        }

        seatViewsMap.get(game.getTable().getSmallBlindID()).txtRoundRole.setText(R.string.role_small_blind);
        seatViewsMap.get(game.getTable().getFocusedPlayer()).edtxtPlayerName.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.turn));

        // Atualiza as cartas
        if (0 < game.getCards()) {
            // Indica o fim da mão
            if (5 < game.getCards()) {
                for (int i = 0; 5 > i; i++)
                    this.cards.get(i).setImageResource(R.drawable.back_card);

                Toast.makeText(GameActivity.this, "Selecione o(s) vencedor(es) dessa mão", Toast.LENGTH_LONG).show();
                btnConfirm.setEnabled(true);
                btnConfirm.setVisibility(View.VISIBLE);
                ArrayList<Integer> validPlayers = game.getTable().getValidPlayers();
                seatViewsMap.get(game.getTable().getFocusedPlayer()).edtxtPlayerName.setBackgroundTintList(null);
                //Desabilitando os botões de controle (call, raise e fold)
                btnCheckCall.setEnabled(false);
                btnRaise.setEnabled(false);
                btnFold.setEnabled(false);

                for (int validPlayer : validPlayers) {
                    seatViewsMap.get(validPlayer).edtxtPlayerName.setClickable(true);
                    seatViewsMap.get(validPlayer).edtxtPlayerName.setEnabled(true);
                    seatViewsMap.get(validPlayer).edtxtPlayerName.setKeyListener(null);
                }
            } else
                for (int i = 0; i < game.getCards(); i++)
                    this.cards.get(i).setImageResource(R.drawable.carta_espadas_vector);
        }

        // Verifica se a ação é call ou check
        Player currentPlayer = players.get(game.getTable().getFocusedPlayer());
        Button btnCallCheck = findViewById(R.id.btn_check_call);
        if (currentPlayer.getRoundChipsBetted() == game.getTableBet()) {
            btnCallCheck.setText(R.string.check);
            btnCallCheck.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.checked));
        } else {
            btnCallCheck.setText(R.string.call);
            btnCallCheck.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.call));
        }

        // Texto de pagamento
        TextView txtToPay = findViewById(R.id.txtToPay);
        txtToPay.setText("To Pay:\n" + (game.getTableBet() - currentPlayer.getRoundChipsBetted()));

        // Texto do pot
        TextView txtPot = findViewById(R.id.txt_pot);
        txtPot.setText("Pot: " + (game.getPot() + pot));

        // Texto de hands
        TextView txtHands = findViewById(R.id.txt_hands);
        txtHands.setText("Hands: " + game.getHandsCount());
    }

    private void nextHandGameActivity() {
        // Se não houver mais mãos, chamar a activity de fim de jogo e passar o vencedor
        if (!game.nextHand(winners)) {
            // Determinar o vencedor
            Player winner = null;
            for (int i = 0; 10 > i; i++)
                if (null != players.get(i) && (null == winner || players.get(i).getChips() > winner.getChips()))
                    winner = players.get(i);
            
            String winnerName = winner.getName();

            // Chamar a activity de fim de jogo, passando o nome do vencedor e encerrando a activity atual
            startActivity(new Intent(GameActivity.this, EndActivity.class).putExtra("playerName", winnerName));
            finish();
        }

        for (int i = 0; 10 > i; i++) {
            Player player = players.get(i);

            if (null == player) {
                seatViewsMap.get(i).edtxtPlayerName.setText("");
                seatViewsMap.get(i).txtChipsTotal.setText("0");
                seatViewsMap.get(i).txtRoundChipsBetted.setText("0");
                seatViewsMap.get(i).txtRoundRole.setText("");
                seatViewsMap.get(i).edtxtPlayerName.setBackgroundTintList(null);
            }
        }

        for (int i = 0; 5 > i; i++) {
            this.cards.get(i).setImageResource(R.drawable.back_card);
        }

        winners.clear();

        updateGameActivity();
    }

    // Ações de call
    private void call() {
        game.call();
        updateGameActivity();
    }

    // Ações de fold
    private void fold() {
        game.fold();
        updateGameActivity();
    }

    // Ações de raise
    private void raise() {
        // Exibir um pop-up para o usuário digitar o valor do raise
        DialogFragment dialog = new raiseDialog();
        dialog.show(getSupportFragmentManager(), "Raise");
    }

    private class SeatViews {
        public EditText edtxtPlayerName;
        public TextView txtChipsTotal;
        public TextView txtRoundChipsBetted;
        public TextView txtRoundRole;
    }
}