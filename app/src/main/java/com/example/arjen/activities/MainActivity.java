package com.example.arjen.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.arjen.R;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.myTTS;

import java.util.Arrays;

public class MainActivity extends MenuActivity {
    private Button storyList, quizList;

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void findViews() {
        storyList = findViewById(R.id.storyList);
        quizList = findViewById(R.id.quizList);
    }

    @Override
    public void registerListeners() {
        storyList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), StoryList.class);
            id = null;
            quizId = null;
            startActivity(intent);
        });
        quizList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), QuizList.class);
            id = null;
            quizId = null;
            startActivity(intent);
        });
    }

    @Override
    public void fillData() {
        myTTS.initTTS(getApplicationContext());
        setupTTS();
    }

    @Override
    public void chooseOption() {
        Intent intent;
        if (currentSentence == 0) {
            intent = new Intent(getApplicationContext(), StoryList.class);
        } else {
            intent = new Intent(getApplicationContext(), QuizList.class);
        }
        id = null;
        quizId = null;
        startActivity(intent);
    }

    @Override
    public void setupTTS() {
        textToSpeak.clear();
        textToSpeak.addAll(Arrays.asList(getResources().getString(R.string.stories), getResources().getString(R.string.quizes)));
        readyToPlay = true;
    }

    @Override
    public void setRecyclerView() {

    }
}