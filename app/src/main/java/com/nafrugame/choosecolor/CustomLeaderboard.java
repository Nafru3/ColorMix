package com.nafrugame.choosecolor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Objects;

public class CustomLeaderboard extends BaseAdapter {

    Context context;
    List<String> classementList;
    List<String> nameList;
    List<Double> scoreList;
    List<Integer> serieList;
    LayoutInflater inflater;


    public CustomLeaderboard(Context ctx, List<String> nameList, List<Double> scoreList,List<Integer> serieList, List<String> classementList) {
        this.context = ctx;
        this.nameList = nameList;
        this.scoreList = scoreList;
        this.classementList = classementList;
        this.serieList = serieList;
        this.inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_layout_leaderboard,null);
        TextView nameText = convertView.findViewById(R.id.namePlayerLeader);
        TextView classementText = convertView.findViewById(R.id.classementText);
        TextView scoreText = convertView.findViewById(R.id.scoreTextLeader);
        View circle = convertView.findViewById(R.id.circleLeaderboard);
        nameText.setText(nameList.get(position));
        classementText.setText(classementList.get(position));


        if (scoreList.isEmpty()) {
            scoreText.setText(String.valueOf( (int) serieList.get(position)));
        } else {
            scoreText.setText(String.valueOf(((int)(scoreList.get(position) * 10)) / 10.0));
        }


        //int classement = Integer.parseInt((String) classementText.getText());

        if (Objects.equals(classementList.get(position), "1")) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.circle);
            drawable.setColorFilter(ContextCompat.getColor(context, R.color.or), PorterDuff.Mode.SRC_IN);
            circle.setBackground(drawable);
        } else if (Objects.equals(classementList.get(position), "2")) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.circle);
            drawable.setColorFilter(ContextCompat.getColor(context, R.color.silver), PorterDuff.Mode.SRC_IN);
            circle.setBackground(drawable);
        } else if (Objects.equals(classementList.get(position), "3")) {
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.circle);
            drawable.setColorFilter(ContextCompat.getColor(context, R.color.bronze), PorterDuff.Mode.SRC_IN);
            circle.setBackground(drawable);
        }

        return convertView;
    }
}
