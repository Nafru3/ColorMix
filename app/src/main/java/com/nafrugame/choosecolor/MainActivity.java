package com.nafrugame.choosecolor;

import static android.app.PendingIntent.getActivity;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity { //Rajouter croix pour quitter couleur
    //Quand on passe en dark mode, ça redémarre l'appli
    boolean classic;

    Drawable drawable;
    boolean badColor;
    AlertDialog dialog;

    private EditText playerNameEditText;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button classicButton = findViewById(R.id.classicButton);
        Button laboButton = findViewById(R.id.laboButton);
        Button infiniButton = findViewById(R.id.infiniButton);
        Button howToPlayButton = findViewById(R.id.tutoButton);
        Button leaderButton = findViewById(R.id.leaderButton);

        View.OnClickListener onClickListener = v -> classicMode();
        View.OnClickListener listenerLabo = v -> labo();
        View.OnClickListener listenerTuto = v -> tutoMode();
        View.OnClickListener listenerInfini = v -> infiniMode();
        View.OnClickListener listenerLeader = v -> toLeaderBoard();


        laboButton.setOnClickListener(listenerLabo);
        classicButton.setOnClickListener(onClickListener);
        infiniButton.setOnClickListener(listenerInfini);
        howToPlayButton.setOnClickListener(listenerTuto);
        leaderButton.setOnClickListener(listenerLeader);

        // Test commit



        int[] listId = {R.id.square1,R.id.square2,R.id.square3,R.id.square4,R.id.square5};
        for (int i : listId) {
            View barre = findViewById(i);
            drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.square);
            Random random = new Random();

            int r = 0,g = 0 ,b = 0;
            badColor = true;

            while (badColor) {
                r = (int) (random.nextDouble() * 255);
                g = (int) (random.nextDouble() * 255);
                b = (int) (random.nextDouble() * 255);

                badColor = Math.sqrt(Math.pow(r, 2) + Math.pow(g, 2) + Math.pow(b, 2)) <= 240;
            }

            drawable.setColorFilter(Color.rgb(r,g,b), PorterDuff.Mode.SRC_IN);
            barre.setBackground(drawable);




        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        };
        getOnBackPressedDispatcher().addCallback(this,callback);



        //Gestion du nom du joueur :

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        //Log.d("test",user.getDisplayName().toString());
        if (user == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.popup_name, null);
            builder.setView(dialogView);
            builder.setCancelable(false); //Empeche l'utilisateur de fermer la fenetre quand il clique à côté
            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //Permet de faire en sorte que le fond de la boite de dialog soit transparent
            dialog.show();

            playerNameEditText = dialogView.findViewById(R.id.editTextName);

            playerNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) { //On vérifie si entrée est pressé
                        nameValidate(dialogView);
                        return true;
                    }
                    return false;
                }
            });


        }


    }
    public void infiniMode() {
        classic = false;
        startGame();
    }

    public void classicMode() {
        classic = true;
        startGame();
    }
    public void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("classic?",classic);
        startActivity(intent);
        overridePendingTransition(R.anim.fromright_size_start,R.anim.toright_size_start);
    }

    public void labo() {
        Intent intent = new Intent(this, LaboActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fromright_size_start,R.anim.toright_size_start);
    }

    public void tutoMode() {
        Intent intent = new Intent(this, TutoActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fromright_size_start,R.anim.toright_size_start);
    }
    public void toLeaderBoard() {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fromright_size_start,R.anim.toright_size_start);
    }
    public void nameValidate(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        // Fermer le clavier virtuel si entrée est pressé

        /*
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        SharedPreferences sharedPreferences = getSharedPreferences("UserID", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String valueName = playerNameEditText.getText().toString();
        if (valueName.equals(""))
        {
            editor.putString("id", "Guest");
        } else {
            editor.putString("id",playerNameEditText.getText().toString());
        }

        editor.commit();*/
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Log.d("test",user.getUid());

                            SharedPreferences sharedPreferences = getSharedPreferences("UserID", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            String valueName = playerNameEditText.getText().toString();


                            if (valueName.equals("")) {
                                valueName = "Guest";
                            }
                            editor.putString("id",valueName);
                                /*
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(playerNameEditText.getText().toString())
                                        .build();
                                user.updateProfile(profileUpdates); //Attention requête ascincrone*/

                            Map<String, Object> userName = new HashMap<>();
                            userName.put("display_name",valueName);
                            db.collection("leaderboard").document(user.getUid()).set(userName, SetOptions.merge());

                            editor.commit();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("test","fail");
                        }
                    }
                });
        dialog.dismiss();


    }
}