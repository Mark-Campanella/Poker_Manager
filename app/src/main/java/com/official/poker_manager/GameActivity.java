package com.official.poker_manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class GameActivity extends AppCompatActivity {
    private Game game;
    private ArrayList<Player> players;
    private ArrayList<ImageView> cards;
    private final Hashtable<Integer, SeatViews> seatViewsMap = new Hashtable<>(10);
    private ArrayList<Integer> winners;
    private ValueViewModel viewModel;

    private class SeatViews {
        public EditText edtxtPlayerName;
        public TextView txtChipsTotal;
        public TextView txtRoundChipsBetted;
        public TextView txtRoundRole;
    }

    private AppCompatImageButton btnConfirm;
    private Button btnCheckCall;
    private Button btnFold;
    private Button btnRaise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Recuperando o objeto Game criado em SetupActivity
        if (getIntent().getExtras() != null) {
            game = (Game) getIntent().getSerializableExtra("game");
        }

        players = game.getTable().getPlayers();

        // Inicializa a array de cartas
        cards = new ArrayList<ImageView>(Collections.nCopies(5, null));
        for (int i = 0; i < 5; i++) {
            cards.set(i, (ImageView) findViewById(getResources().getIdentifier("card_" + String.valueOf(i), "id", getPackageName())));
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
    @SuppressLint({"UseCompatLoadingForColorStateLists", "WrongViewCast"})
    private void startGameActivity() {
        game.startGame();

        // Inicializa a array de cartas
        cards = new ArrayList<ImageView>(Collections.nCopies(5, null));
        for (int i = 0; i < 5; i++) {
            cards.set(i, (ImageView) findViewById(getResources().getIdentifier("card_" + String.valueOf(i), "id", getPackageName())));
        }

        // Inicializa a ViewModel usada em raise
        viewModel = new ViewModelProvider(this).get(ValueViewModel.class);
        viewModel.getValue().observe(this, value -> {
            int raiseValue = value.intValue();
            // Se o valor do raise for válido, chamar o método raise (entre aposta da mesa+1 e all-in)
            if (raiseValue >= this.game.getBlind() && raiseValue + game.getTableBet() - players.get(game.getTable().getFocusedPlayer()).getRoundChipsBetted() <= players.get(game.getTable().getFocusedPlayer()).getChips())
                game.raise(raiseValue);
                // Se for -1, é all-in
            else if (raiseValue == -1)
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
        for (int i = 0; i < 10; i++) {
            seatViewsMap.put(i, new SeatViews());
            seatViewsMap.get(i).edtxtPlayerName = findViewById(getResources().getIdentifier("seat_" + String.valueOf(i), "id", getPackageName()));
            seatViewsMap.get(i).txtChipsTotal = findViewById(getResources().getIdentifier("txt_chips_" + String.valueOf(i), "id", getPackageName()));
            seatViewsMap.get(i).txtRoundChipsBetted = findViewById(getResources().getIdentifier("txt_bet_" + String.valueOf(i), "id", getPackageName()));
            seatViewsMap.get(i).txtRoundRole = findViewById(getResources().getIdentifier("txt_role_" + String.valueOf(i), "id", getPackageName()));
            if (players.get(i) != null) {
                seatViewsMap.get(i).edtxtPlayerName.setText((CharSequence) players.get(i).getName());
                seatViewsMap.get(i).txtChipsTotal.setText((CharSequence) String.valueOf(players.get(i).getChips()));
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
                if (winners.size() < 1) {
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
        for (int i = 0; i < 10; i++) {
            if (players.get(i) != null) {
                pot += players.get(i).getRoundChipsBetted();
                playerCount++;

                seatViewsMap.get(i).txtChipsTotal.setText((CharSequence) String.valueOf(players.get(i).getChips()));
                seatViewsMap.get(i).txtRoundChipsBetted.setText((CharSequence) String.valueOf(players.get(i).getRoundChipsBetted()));
                seatViewsMap.get(i).txtRoundRole.setText("");
                if (players.get(i).isFolded())
                    seatViewsMap.get(i).edtxtPlayerName.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.folded));
                else{
                    seatViewsMap.get(i).edtxtPlayerName.setBackgroundTintList(null);
                    playersBalance += players.get(i).getChips();
                    activePlayerCount++;
                }
            }
        }
        
        // Condições de "vitória imediata"
        if (playersBalance == 0 || activePlayerCount == 1) {
            game.setCards(6);
        }

        // Atualiza as informações de quem é o dealer, small blind, big blind e de quem é a vez
        // Se só tiver dois jogadores, o update deve operar de modo diferente:
        // O Dealer/BigBlind recebe a role D/B
        // E o SmallBlind continua igual
        if (playerCount > 2) {
            seatViewsMap.get(game.getTable().getDealerID()).txtRoundRole.setText(R.string.role_dealer);
            seatViewsMap.get(game.getTable().getBigBlindID()).txtRoundRole.setText(R.string.role_big_blind);
        } else {
            seatViewsMap.get(game.getTable().getDealerID()).txtRoundRole.setText(R.string.DealerBB);
        }

        seatViewsMap.get(game.getTable().getSmallBlindID()).txtRoundRole.setText(R.string.role_small_blind);
        seatViewsMap.get(game.getTable().getFocusedPlayer()).edtxtPlayerName.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.turn));

        // Atualiza as cartas
        if (game.getCards() > 0) {
            // Indica o fim da mão
            if (game.getCards() > 5) {
                for (int i = 0; i < 5; i++)
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
        Button btnCallCheck = (Button) findViewById(R.id.btn_check_call);
        if (currentPlayer.getRoundChipsBetted() == game.getTableBet()) {
            btnCallCheck.setText(R.string.check);
            btnCallCheck.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.checked));
        } else {
            btnCallCheck.setText(R.string.call);
            btnCallCheck.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.call));
        }

        // Texto de pagamento
        TextView txtToPay = (TextView) findViewById(R.id.txtToPay);
        txtToPay.setText("To Pay:\n" + String.valueOf(game.getTableBet() - currentPlayer.getRoundChipsBetted()));

        // Texto do pot
        TextView txtPot = (TextView) findViewById(R.id.txt_pot);
        txtPot.setText("Pot: " + String.valueOf(game.getPot() + pot));

        // Texto de hands
        TextView txtHands = (TextView) findViewById(R.id.txt_hands);
        txtHands.setText("Hands: " + String.valueOf(game.getHandsCount()));
    }

    private void nextHandGameActivity() {
        game.nextHand(winners);

        for (int i = 0; i < 10; i++) {
            Player player = players.get(i);

            if (player == null) {
                seatViewsMap.get(i).edtxtPlayerName.setText("");
                seatViewsMap.get(i).txtChipsTotal.setText("0");
                seatViewsMap.get(i).txtRoundChipsBetted.setText("0");
                seatViewsMap.get(i).txtRoundRole.setText("");
                seatViewsMap.get(i).edtxtPlayerName.setBackgroundTintList(null);
            }
        }

        for (int i = 0; i < 5; i++) {
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
}