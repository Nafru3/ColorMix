package com.nafrugame.choosecolor;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FinalActivity extends AppCompatActivity {

    ArrayList<Double> notes = new ArrayList<>();
    double[] notesTable;
    int colorMax;
    boolean classic;
    Drawable drawable;
    boolean badColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        int[] listId = {R.id.square10, R.id.square11, R.id.square12, R.id.square13, R.id.square14};
        for (int i : listId) {
            View barre = findViewById(i);
            drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.square);
            Random random = new Random();

            int r = 0, g = 0, b = 0;
            badColor = true;

            while (badColor) {
                r = (int) (random.nextDouble() * 255);
                g = (int) (random.nextDouble() * 255);
                b = (int) (random.nextDouble() * 255);

                badColor = Math.sqrt(Math.pow(r, 2) + Math.pow(g, 2) + Math.pow(b, 2)) <= 240;
            }
            drawable.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.SRC_IN);
            barre.setBackground(drawable);
        }


        TextView textNbrColor = findViewById(R.id.textNbrColor);

        Button menuReplay = findViewById(R.id.restartButton);
        menuReplay.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("classic?", classic);
            startActivity(intent);
            finishAfterTransition();
            overridePendingTransition(R.anim.fromleft_size_start, R.anim.toleft_size_start);

        });
        TextView textInf80 = findViewById(R.id.textInf80);
        Button menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finishAfterTransition();
            overridePendingTransition(R.anim.fromleft_size_start, R.anim.toleft_size_start);
        });


        Intent intent = getIntent();
        if (intent != null) {
            notesTable = intent.getDoubleArrayExtra("notesKey");
            // Convertir le tableau en ArrayList
            assert notesTable != null;
            for (double element : notesTable) {
                notes.add(element);
            }

            colorMax = intent.getIntExtra("colorMaxKey", 1);// 0 est la valeur par défaut si la clé n'est pas trouvée
            classic = intent.getBooleanExtra("classic", true);
            TextView finalText = findViewById(R.id.finalText);
            double somme = 0;
            for (double i : notes) {
                somme += i;
            }

            String averageStr = String.format("%.1f", somme/colorMax);
            finalText.setText("Your average is " + averageStr + "%");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            Map<String, Object> userHash = new HashMap<>();

            if (!classic) {
                textNbrColor.setText("You reached " + colorMax + " Colors");
                userHash.put("best_series", colorMax);

                if (user != null) {
                    final FirebaseUser finalUser = user;
                    db.collection("leaderboard")
                        .document(user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().getData().get("best_series") == null) {
                                        db.collection("leaderboard").document(finalUser.getUid()).set(userHash, SetOptions.merge());
                                    } else if (colorMax > (Long) task.getResult().getData().get("best_series")) {
                                        db.collection("leaderboard").document(finalUser.getUid()).set(userHash, SetOptions.merge());
                                    }
                                }
                            }
                        });
                }
            } else {
                textInf80.setText("");
                userHash.put("best_average", somme / colorMax);
                user = mAuth.getCurrentUser();
                if (user != null) {
                    final FirebaseUser finalUser = user;
                    final Double finalSomme = somme;
                    db.collection("leaderboard")
                        .document(user.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    /*
                                    if (finalSomme / colorMax > (Double) task.getResult().getData().get("best_average") || task.getResult().getData().get("best_average") == null) {
                                        db.collection("leaderboard").document(finalUser.getUid()).set(userHash, SetOptions.merge());
                                        //Log.d("test",task.getResult().getData().get("best_average").toString());
                                    }*/

                                    if (task.getResult().getData().get("best_average") == null) {
                                        db.collection("leaderboard").document(finalUser.getUid()).set(userHash, SetOptions.merge());
                                    } else if (finalSomme / colorMax > (Long) task.getResult().getData().get("best_average")) {
                                        db.collection("leaderboard").document(finalUser.getUid()).set(userHash, SetOptions.merge());
                                    }
                                }
                            }
                        });
                }
            }

            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    Intent intentBack = new Intent(FinalActivity.this, MainActivity.class);
                    startActivity(intentBack);
                    finishAfterTransition();
                    overridePendingTransition(R.anim.fromleft_size_start, R.anim.toleft_size_start); // Transition dans l'autre sens
                }
            };
            getOnBackPressedDispatcher().addCallback(this, callback);
        }
    }
}