package com.example.arjen.activities;

import android.content.Intent;
import android.widget.Button;

import com.example.arjen.R;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.myTTS;

import java.util.Arrays;

public class MainActivity extends MenuActivity {
    private Button storyList, quizList, questionList, numbersSound, numbersWords, numbersWordsSound;

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void findViews() {
        storyList = findViewById(R.id.storyList);
        quizList = findViewById(R.id.quizList);
        questionList = findViewById(R.id.questionList);
        numbersWords = findViewById(R.id.numbersWords);
        numbersSound = findViewById(R.id.numbersSound);
        numbersWordsSound = findViewById(R.id.numbersWordsSound);
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
        questionList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), QuestionList.class);
            id = null;
            quizId = null;
            startActivity(intent);
        });
        numbersSound.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Numbers.class);
            id = null;
            quizId = null;
            intent.putExtra("words", 0);
            startActivity(intent);
        });
        numbersWords.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Numbers.class);
            id = null;
            quizId = null;
            intent.putExtra("words", 1);
            startActivity(intent);
        });
        numbersWordsSound.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Numbers.class);
            id = null;
            quizId = null;
            startActivity(intent);
        });
    }

    @Override
    public void fillData() {
    }

    @Override
    public void chooseOption() {
        Intent intent;
        if (currentSentence == 0) {
            intent = new Intent(getApplicationContext(), StoryList.class);
        } else {
            if (currentSentence == 1) {
                intent = new Intent(getApplicationContext(), QuizList.class);
            } else {
                if (currentSentence == 2) {
                    intent = new Intent(getApplicationContext(), QuestionList.class);
                } else {
                    intent = new Intent(getApplicationContext(), Numbers.class);
                    if (currentSentence == 3) {
                        intent.putExtra("words", 0);
                    }
                    if (currentSentence == 4) {
                        intent.putExtra("words", 1);
                    }
                }
            }
        }
        id = null;
        quizId = null;
        startActivity(intent);
    }

    @Override
    public void setupTTS() {
        textToSpeak.clear();
        textToSpeak.addAll(Arrays.asList(
                getResources().getString(R.string.stories),
                getResources().getString(R.string.quizes),
                getResources().getString(R.string.questions),
                getResources().getString(R.string.numbers_sound),
                getResources().getString(R.string.numbers_words),
                getResources().getString(R.string.numbers_words_sound)));
        readyToPlay = true;
        currentSentence = 0;
        numClick = 0;
    }

    @Override
    public void setRecyclerView() {

    }
}