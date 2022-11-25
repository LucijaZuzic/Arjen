package com.example.arjen.activities;


import android.content.Intent;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import com.example.arjen.R;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.myTTS;

import java.util.Random;

public class Numbers extends MenuActivity {
    private TextView modeTextView, numberTextView, numberCurrentTextView;
    private Integer currentNumber = 0, numberToGuess = 0;
    private MediaPlayer musicDing;
    private boolean answered = false;

    @Override
    protected void onStop() {
        super.onStop();
        if (musicDing != null && musicDing.isPlaying()) {
            musicDing.stop();
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_numbers;
    }

    @Override
    public void findViews() {
        modeTextView = findViewById(R.id.modeTextView);
        numberTextView = findViewById(R.id.numberTextView);
        numberCurrentTextView = findViewById(R.id.numberCurrentTextView);
    }

    @Override
    public void registerListeners() {

    }

    @Override
    public void fillData() {
        answered = false;
        currentNumber = 0;
        Random randomNew = new Random();
        numberToGuess = randomNew.nextInt(5) + 1;
        Intent intent = getIntent();
        int words;
        if (intent.hasExtra("words")) {
            words = intent.getExtras().getInt("words");
        } else {
            words = randomNew.nextInt(2);
        }
        numberTextView.setText(R.string.number_to_guess + ": " + numberToGuess);
        numberTextView.setText(R.string.number_input + ": 0");
        if (words == 1) {
            modeTextView.setText(getResources().getString(R.string.numbers_words));
            myTTS.speak(numberToGuess.toString(), TextToSpeech.QUEUE_FLUSH);
        } else {
            modeTextView.setText(getResources().getString(R.string.numbers_sound));
            musicDing = MediaPlayer.create(this, R.raw.on);
            final int[] numberPlayed = {1};
            musicDing.start();
            musicDing.setOnCompletionListener(v -> {
                if (numberPlayed[0] < numberToGuess) {
                    musicDing.start();
                }
                numberPlayed[0]++;
            });
        }
    }

    @Override
    public void playNext() {
        if (answered) {
            return;
        }
        currentNumber++;
        numberCurrentTextView.setText(R.string.number_input + ": " + currentNumber);
        myTTS.speak(currentNumber.toString(), TextToSpeech.QUEUE_FLUSH);
    }

    @Override
    public void chooseOption() {
        answered = true;
        if (currentNumber.equals(numberToGuess)) {
            myTTS.speak(getResources().getString(R.string.correct), TextToSpeech.QUEUE_FLUSH);
            if (musicDing != null && musicDing.isPlaying()) {
                musicDing.stop();
            }
            musicDing = MediaPlayer.create(this, R.raw.success);
        } else {
            myTTS.speak(getResources().getString(R.string.incorrect), TextToSpeech.QUEUE_FLUSH);
            if (musicDing != null && musicDing.isPlaying()) {
                musicDing.stop();
            }
            musicDing = MediaPlayer.create(this, R.raw.failure);
        }
        musicDing.start();
        musicDing.setOnCompletionListener(v -> fillData());
    }

    @Override
    public void setupTTS() {
        textToSpeak.add(numberToGuess.toString());
        readyToPlay = true;
    }

    @Override
    public void setRecyclerView() {

    }
}