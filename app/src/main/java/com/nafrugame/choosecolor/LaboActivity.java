package com.nafrugame.choosecolor;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

public class LaboActivity extends AppCompatActivity {
    int couleur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labo);

        Slider rSlider = findViewById(R.id.redLaboSlider);
        Slider gSlider = findViewById(R.id.greenLaboSlider);
        Slider bSlider = findViewById(R.id.blueLaboSlider);
        View back = findViewById(R.id.background);
        back.setBackgroundColor(Color.rgb(0,0,0));

        Button menuButton = findViewById(R.id.laboMenuButton);
        TextView viewR = findViewById(R.id.textRedLabo);
        TextView viewG = findViewById(R.id.textGreenLabo);
        TextView viewB = findViewById(R.id.textBlueLabo);
        TextView exText = findViewById(R.id.exTextLabo);


        Slider.OnChangeListener listener = (slider, value, fromUser) -> {
            couleur = Color.rgb(rSlider.getValue(),gSlider.getValue(),bSlider.getValue());
            changeStatusBarColorLabo(couleur);
            changeNavigationBarColor(couleur);
            back.setBackgroundColor(couleur);
            if (Math.sqrt(Math.pow(rSlider.getValue() * 255, 2) + Math.pow(gSlider.getValue() * 255, 2) + Math.pow(bSlider.getValue() * 255, 2)) <= 240) {
                viewR.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                viewG.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                viewB.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                exText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                menuButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                menuButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

            } else {
                viewR.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                viewG.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                viewB.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                exText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                menuButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                menuButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            }
        };

        rSlider.addOnChangeListener(listener);
        gSlider.addOnChangeListener(listener);
        bSlider.addOnChangeListener(listener);

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
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finishAfterTransition();
        overridePendingTransition(R.anim.fromleft_size_start,R.anim.toleft_size_start);
    }
    private void changeStatusBarColorLabo(int color) {
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
}