package com.nafrugame.choosecolor;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.slider.Slider;

public class FragmentColor extends Fragment {
    int r;
    int g;
    int b;
    double note;
    boolean classic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_color, container, false);
    }
    public FragmentColor(int r, int g, int b, boolean classic) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.classic = classic;

    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View crossView = view.findViewById(R.id.crossView);

        ConstraintLayout layout = view.findViewById(R.id.background);
        Slider rSlider = view.findViewById(R.id.redSlider);
        Slider gSlider = view.findViewById(R.id.greenSlider);
        Slider bSlider = view.findViewById(R.id.blueSlider);

        TextView textNbrColor1 = view.findViewById(R.id.textNbrColor1);
        TextView textNbrColor2 = view.findViewById(R.id.textNbrColor2);
        TextView textRed = view.findViewById(R.id.textRed);
        TextView textGreen = view.findViewById(R.id.textGreen);
        TextView textBlue = view.findViewById(R.id.textBlue);
        TextView exText = view.findViewById(R.id.exTextColor);

        ImageView circleNote = view.findViewById(R.id.circleNote);

        Button submitButton = view.findViewById(R.id.submitButton);


        int couleur = Color.rgb(r,g,b);
        layout.setBackgroundColor(couleur);

        GameActivity activity = (GameActivity) getActivity();

        activity.changeStatusBarColor(couleur);
        activity.changeNavigationBarColor(couleur);


        boolean isWhite = activity.isWhite;

        if (isWhite) {

            textNbrColor1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black)); //ContextCompat.getColor permet de convertir R.color.white en int (genre rgb)
            textNbrColor2.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

            Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.circle);
            drawable.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            circleNote.setBackground(drawable);

            Drawable drawableCross = ContextCompat.getDrawable(requireContext(), R.drawable.cross_final);
            drawableCross.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            crossView.setBackground(drawableCross);

            textRed.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            textGreen.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            textBlue.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            submitButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            submitButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
            exText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        }



        if (classic) {
            textNbrColor1.setText(activity.nbrColor + "");
            textNbrColor2.setText("/" + activity.nbrColorMax + " ");
        } else {
            textNbrColor1.setText(activity.nbrColor + "");
            textNbrColor2.setText("");
        }

        crossView.setOnClickListener(v -> {
            ((GameActivity) getActivity()).goToMenu(crossView);
        });


        submitButton.setOnClickListener(v -> {
            /* //Creation d'une boite de dialogue
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.cardview_result);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false); //Quand on clique à coté, cela ne s'enlève pas
            dialog.show(); //Pour afficher le dialogue / Pour l'enlever : dialog.dismiss()
             */
            int rGuess = (int) (rSlider.getValue()*255);
            int gGuess = (int) (gSlider.getValue()*255);
            int bGuess = (int) (bSlider.getValue()*255);

            note = calculNote(rGuess,gGuess,bGuess,r,g,b);


            FragmentResult fragmentResult = new FragmentResult(rGuess,(int) gGuess,(int) bGuess,r,g,b,note);

            getActivity().getSupportFragmentManager()
                         .beginTransaction()
                         .replace(R.id.fragmentContainer,fragmentResult)
                         .commit();
        });

    }
    public double calculNote(int rGuess, int gGuess, int bGuess, int rReal, int gReal, int bReal) {
        double rNote = 100 - Math.abs(Math.pow(rGuess, (double) 3 /2) - Math.pow(rReal, (double) 3 /2)) / 40.7202;
        double gNote = 100 - Math.abs(Math.pow(gGuess, (double) 3 /2) - Math.pow(gReal, (double) 3 /2)) / 40.7202;
        double bNote = 100 - Math.abs(Math.pow(bGuess, (double) 3 /2) - Math.pow(bReal, (double) 3 /2)) / 40.7202;

        double rgNote = 100 - Math.abs(rGuess - gGuess - (rReal - gReal)) / 2.55;
        double gbNote = 100 - Math.abs(gGuess - bGuess - (gReal - bReal)) / 2.55;
        double brNote = 100 - Math.abs(bGuess - rGuess - (bReal - rReal)) / 2.55;

        return 0.8 * (rNote + gNote + bNote)/3 + 0.2 * (rgNote + gbNote + brNote)/3;
    }

}