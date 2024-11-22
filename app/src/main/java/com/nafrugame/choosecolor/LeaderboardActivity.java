package com.nafrugame.choosecolor;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LeaderboardActivity extends AppCompatActivity {
    List<String> classementList;
    List<String> nameList;
    List<String> idList;
    List<Double> scoreList;
    List<Integer> serieList;
    ListView listView;
    FirebaseFirestore db;
    Button scoreButton;
    Button averageButton;
    Button serieButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = findViewById(R.id.listViewLeaderboard);
        //FirebaseAuth mAuth;
        scoreButton = findViewById(R.id.bestScoreButton);
        averageButton = findViewById(R.id.bestAverageButton);
        serieButton = findViewById(R.id.bestSerieButton);
        AtomicReference<Button> lastButton = new AtomicReference<>(scoreButton);

        db = FirebaseFirestore.getInstance();

        classementList = new ArrayList<>();
        nameList = new ArrayList<>();
        idList = new ArrayList<>();
        scoreList = new ArrayList<>();
        serieList = new ArrayList<>();
        MaterialButtonToggleGroup toggleButton = findViewById(R.id.toggleButton);

        serieButton.setBackgroundColor(getResources().getColor(R.color.white));
        averageButton.setBackgroundColor(getResources().getColor(R.color.white));
        scoreButton.setBackgroundColor(getResources().getColor(R.color.red));
        afficheLeaderBoard("best_score");

        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                reinitialiseListView();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
                listView.setAdapter(adapter);
                if (isChecked) {
                    if (checkedId == R.id.bestScoreButton) {
                        afficheLeaderBoard("best_score");
                        scoreButton.setBackgroundColor(getResources().getColor(R.color.red));
                    } else if (checkedId == R.id.bestAverageButton) {
                        afficheLeaderBoard("best_average");
                        averageButton.setBackgroundColor(getResources().getColor(R.color.green));
                    } else {
                        afficheLeaderBoard("best_series");
                        serieButton.setBackgroundColor(getResources().getColor(R.color.blue));
                    }
                } else {
                    if (checkedId == R.id.bestScoreButton) {
                        scoreButton.setBackgroundColor(getResources().getColor(R.color.white));
                    } else if (checkedId == R.id.bestAverageButton) {
                        averageButton.setBackgroundColor(getResources().getColor(R.color.white));
                    } else {
                        serieButton.setBackgroundColor(getResources().getColor(R.color.white));
                    }


                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) { //Gestion du retour (écran joli)
            @Override
            public void handleOnBackPressed() {
                finishAfterTransition();
                overridePendingTransition(R.anim.fromleft_size_start, R.anim.toleft_size_start); // Transition dans l'autre sens
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
/*
        scoreButton.setOnClickListener(v -> {
            if (scoreButton != lastButton.get()) {
                reinitialiseListView();
                lastButton.set(scoreButton);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
                listView.setAdapter(adapter);
                afficheLeaderBoard("best_score");
            }
        });

        averageButton.setOnClickListener(v -> {
            if (averageButton != lastButton.get()) {
                reinitialiseListView();
                lastButton.set(averageButton);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
                listView.setAdapter(adapter);
                afficheLeaderBoard("best_average");
            }
        });

        serieButton.setOnClickListener(v -> {
            if (serieButton != lastButton.get()) {
                reinitialiseListView();
                lastButton.set(serieButton);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
                listView.setAdapter(adapter);
                afficheLeaderBoard("best_series");
            }
        });
        scoreButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        afficheLeaderBoard("best_score");*/


    }

    public void reinitialiseListView() {
        scoreButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    public void afficheLeaderBoard(String best_list) {
        mAuth = FirebaseAuth.getInstance();
        db.collection("leaderboard")
                .orderBy(best_list, Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int classement = 1;
                            classementList = new ArrayList<>();
                            nameList = new ArrayList<>();
                            scoreList = new ArrayList<>();
                            serieList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                classementList.add(String.valueOf(classement));
                                if (best_list.equals("best_series")) {
                                    if (document.getData().get(best_list) instanceof Double) {
                                        serieList.add((int) Math.floor((Double) document.getData().get(best_list)));
                                    } else if (document.getData().get(best_list) instanceof Long) {
                                        serieList.add((int) Math.floor((Long) document.getData().get(best_list)));
                                    }


                                    //Serielist doit prendre que des entiers : Mais problème, document.getData renvoie parfois entier, parfois double parfois long
                                /*
                                try {
                                    serieList.add((int) Math.floor((Double) document.getData().get(best_list))   );
                                } catch (NullPointerException e) {
                                    serieList.add((int) Math.floor((Long) document.getData().get(best_list))   );
                                }*/
                                } else {
                                    scoreList.add((Double) document.getData().get(best_list));
                                }
                                nameList.add((String) document.getData().get("display_name"));
                                classement++;
                            }
                            CustomLeaderboard adapter = new CustomLeaderboard(getApplicationContext(), nameList, scoreList, serieList, classementList);
                            listView.setAdapter(adapter);
                        }
                    }
                });
    }

    public void goToMenu(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAfterTransition();
        overridePendingTransition(R.anim.fromleft_size_start, R.anim.toleft_size_start);
    }

}