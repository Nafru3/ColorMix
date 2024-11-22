package com.nafrugame.choosecolor;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    ArrayList<Double> notes = new ArrayList<>();
    int nbrColor;
    int nbrColorMax;
    FragmentColor fragmentColor;
    boolean classic;
    boolean isWhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        classic = intent.getBooleanExtra("classic?",true);
        setContentView(R.layout.activity_game);
        nbrColor = 0;
        nbrColorMax = 10;
        newColor();



        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intentBack = new Intent(GameActivity.this, MainActivity.class);
                startActivity(intentBack);
                finishAfterTransition();
                overridePendingTransition(R.anim.fromleft_size_start,R.anim.toleft_size_start); // Transition dans l'autre sens
            }
        };
        getOnBackPressedDispatcher().addCallback(this,callback);
    }

    public void newColor() {
        Random random = new Random();
        int r = (int) (random.nextDouble() * 255);
        int g = (int) (random.nextDouble() * 255);
        int b = (int) (random.nextDouble() * 255);
        isWhite = Math.sqrt(Math.pow(r, 2) + Math.pow(g, 2) + Math.pow(b, 2)) <= 240;
        nbrColor++;
        fragmentColor = new FragmentColor(r,g,b,classic);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragmentColor).commit();
    }

    public void nextColor() {
        notes.add(fragmentColor.note);


        if (classic) {
            if (nbrColor < nbrColorMax) {
                newColor();
            } else {
                affichageEnd();
            }
        } else {
            if (fragmentColor.note >= 80) {
                newColor();
            } else {
                affichageEnd();
            }
        }


    }
    public void affichageEnd() {
        try {
            Intent intent = new Intent(this, FinalActivity.class);
            intent.putExtra("notesKey", notes.stream().mapToDouble(Double::doubleValue).toArray()); //Permet de
            intent.putExtra("colorMaxKey",notes.size());//Permet d'envoyer des paramètres
            intent.putExtra("classic",classic);
            startActivity(intent);

            //overridePendingTransition(R.anim.fromright_size_start,R.anim.toright_size_start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }

    }
    public void changeNavigationBarColor(int color) {
        Window window = getWindow();

        // Assurez-vous que la version Android est égale ou supérieure à Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(color);
        }
    }

    public void goToMenu(View v) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finishAfterTransition();
        overridePendingTransition(R.anim.fromleft_size_start,R.anim.toleft_size_start);
    }

}