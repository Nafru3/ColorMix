package com.nafrugame.choosecolor;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class FragmentResult extends Fragment {
    int rGuess;
    int gGuess;
    int bGuess;
    int rAnswer;
    int gAnswer;
    int bAnswer;
    double note;

    public FragmentResult(int rGuess, int gGuess, int bGuess, int rAnswer, int gAnswer, int bAnswer, double note) {
        this.rGuess = rGuess;
        this.gGuess = gGuess;
        this.bGuess = bGuess;
        this.rAnswer = rAnswer;
        this.gAnswer = gAnswer;
        this.bAnswer = bAnswer;
        this.note = note;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Slider sliderGuessRed = view.findViewById(R.id.sliderGuessRed);
        Slider sliderGuessGreen = view.findViewById(R.id.sliderGuessGreen);
        Slider sliderGuessBlue = view.findViewById(R.id.sliderGuessBlue);

        Slider sliderAnswerRed = view.findViewById(R.id.sliderResponseRed);
        Slider sliderAnswerGreen = view.findViewById(R.id.sliderResponseGreen);
        Slider sliderAnswerBlue = view.findViewById(R.id.sliderResponseBlue);

        View guessRectangle = view.findViewById(R.id.guessRectangle);
        View triangle1 = view.findViewById(R.id.triangle1);
        View triangle2 = view.findViewById(R.id.triangle2);
        View answerRectangle = view.findViewById(R.id.correctRectangle);

        View circle = view.findViewById(R.id.circle);
        View trait = view.findViewById(R.id.trait);
        View crossView = view.findViewById(R.id.crossView);

        TextView textNote = view.findViewById(R.id.textNote);
        TextView textResults = view.findViewById(R.id.textResults);
        TextView textGuess = view.findViewById(R.id.textGuess);
        TextView textAnswer = view.findViewById(R.id.textAnswer);

        Button nextButton = view.findViewById(R.id.nextButton);
        GameActivity activity = (GameActivity) getActivity();

        activity.changeStatusBarColor(Color.rgb(rGuess,gGuess,bGuess));
        activity.changeNavigationBarColor(Color.rgb(rAnswer,gAnswer,bAnswer));

        if (activity.isWhite) {
            textAnswer.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            nextButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
            nextButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

            //changement de couleur de la barre du milieu
            textNote.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

            Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.square);
            drawable.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            trait.setBackground(drawable);

            drawable = ContextCompat.getDrawable(requireContext(), R.drawable.circle);
            drawable.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            circle.setBackground(drawable);
        }

        boolean isWhiteGuess = Math.sqrt(Math.pow(rGuess, 2) + Math.pow(gGuess, 2) + Math.pow(bGuess, 2)) <= 240;

        if (isWhiteGuess) {

            Drawable drawableCross = ContextCompat.getDrawable(requireContext(), R.drawable.cross_final);
            drawableCross.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN);
            crossView.setBackground(drawableCross);

            textResults.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            textGuess.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        }

        crossView.setOnClickListener(v -> {
            ((GameActivity) getActivity()).goToMenu(crossView);
        });

        nextButton.setOnClickListener(v -> {
            activity.nextColor();
        });

        sliderGuessRed.setValue((float) rGuess/255);
        sliderGuessGreen.setValue((float) gGuess/255);
        sliderGuessBlue.setValue((float) bGuess/255);
        sliderAnswerRed.setValue((float) rAnswer/255);
        sliderAnswerGreen.setValue((float) gAnswer/255);
        sliderAnswerBlue.setValue((float) bAnswer/255);
        textNote.setText(" 0% ");


        guessRectangle.setBackgroundColor(Color.rgb(rGuess,gGuess,bGuess));
        answerRectangle.setBackgroundColor(Color.rgb(rAnswer,gAnswer,bAnswer));
        // Obtenez une référence à votre vecteur drawable
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.trianglefinal);

        // Changez la couleur en utilisant la méthode setColorFilter
        drawable.setColorFilter(Color.rgb(rAnswer,gAnswer,bAnswer), PorterDuff.Mode.SRC_IN);
        triangle1.setBackground(drawable);

        drawable = ContextCompat.getDrawable(requireContext(), R.drawable.trianglefinal);
        drawable.setColorFilter(Color.rgb(rGuess,gGuess,bGuess), PorterDuff.Mode.SRC_IN);
        triangle2.setBackground(drawable);
        //triangle2.setBackgroundColor(Color.rgb(rGuess,gGuess,bGuess));

        //Ajouter Note dans la base de donnée de Firebase

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Map<String, Object> userHash = new HashMap<>();
        userHash.put("best_score", note);


        if (user != null) {
            db.collection("leaderboard")
            .document(user.getUid())
            .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.getData() != null) {
                                    if (task.getResult().getData().get("best_score") == null) {
                                        db.collection("leaderboard").document(user.getUid()).set(userHash, SetOptions.merge());
                                    } else if (note > (Double) task.getResult().getData().get("best_score")) {
                                        db.collection("leaderboard").document(user.getUid()).set(userHash, SetOptions.merge());
                                    }
                                }
                            }
                        }
                    });
            /*db.collection("leaderboard").document(user.getUid()).set(userHash, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       Log.d("test","sucess");
                   }
               }
            ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("test","fail");
                        }
                    });*/
        }

        //user.updateProfile()



        // Utilisez un Handler pour ajouter une pause d'une seconde
        Handler handler = new Handler();

        for (int i = 1; i < 10*note; i++) {
            final double finali = i/10.0;
            handler.postDelayed(() -> {
                if (finali < 100) {
                    textNote.setText( " " + finali + "%");
                } else {
                    textNote.setText( finali + "%");
                }
            }, i); // delai
        }

    }

}