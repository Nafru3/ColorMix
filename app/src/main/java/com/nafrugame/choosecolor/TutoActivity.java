package com.nafrugame.choosecolor;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

public class TutoActivity extends AppCompatActivity {

    Drawable drawable;
    boolean badColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuto);

        int[] listId = {R.id.square20,R.id.square21,R.id.square22};
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
        OnBackPressedCallback callback = new OnBackPressedCallback(true) { //Gestion du retour (écran joli)
            @Override
            public void handleOnBackPressed() {
                finishAfterTransition();
                overridePendingTransition(R.anim.fromleft_size_start,R.anim.toleft_size_start); // Transition dans l'autre sens
            }
        };
        getOnBackPressedDispatcher().addCallback(this,callback);
    }
    public void goToMenu(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAfterTransition();
        overridePendingTransition(R.anim.fromleft_size_start,R.anim.toleft_size_start);
    }
}