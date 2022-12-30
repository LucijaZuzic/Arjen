package com.example.arjen.activities;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arjen.R;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.myTTS;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

public class Numbers extends MenuActivity {
    private TextView modeTextView, numberTextView, numberCurrentTextView;
    private Integer currentNumber = 0, numberToGuess = 0;
    private boolean answered = false;
    private LinearLayout numberMarkers, currentNumberMarkers;
    private int words;

    @Override
    public int getLayout() {
        return R.layout.activity_numbers;
    }

    @Override
    public void findViews() {
        modeTextView = findViewById(R.id.modeTextView);
        numberTextView = findViewById(R.id.numberTextView);
        numberCurrentTextView = findViewById(R.id.numberCurrentTextView);
        numberMarkers = findViewById(R.id.numberMarkers);
        currentNumberMarkers = findViewById(R.id.currentNumberMarkers);
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
        setNumberMarkers();
        Intent intent = getIntent();
        if (intent.hasExtra("words")) {
            words = intent.getExtras().getInt("words");
        } else {
            words = randomNew.nextInt(2);
        }
        numberTextView.setText("" + numberToGuess);
        numberCurrentTextView.setText("0");
        if (words == 1) {
            modeTextView.setText(getResources().getString(R.string.numbers_words));
        } else {
            modeTextView.setText(getResources().getString(R.string.numbers_sound));
        }
        myTTS.speak(activityToText(), TextToSpeech.QUEUE_FLUSH);
        Handler handler = new Handler();
        Context me = this;
        handler.postDelayed(new Runnable() {
            public void run() {
                if (words == 1) {
                    myTTS.speak(numberToGuess.toString(), TextToSpeech.QUEUE_FLUSH);
                } else {
                    musicDing = MediaPlayer.create(me, R.raw.on);
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
        }, 2500);
    }

    @Override
    public void playNext() {
        if (answered) {
            return;
        }
        currentNumber++;
        activateNumberMarkers();
        numberCurrentTextView.setText("" + currentNumber);
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
        musicDing.setOnCompletionListener(v -> {
            fillData();
        });
    }

    public void setNumberMarkers() {
        currentNumberMarkers.removeAllViews();
        numberMarkers.removeAllViews();
        for (int i = 0; i < numberToGuess; i++) {
            numberMarkers.addView(createNumberMarker(false));
        }
    }

    public void activateNumberMarkers() {
        currentNumberMarkers.removeAllViews();
        for (int i = 0; i < Math.min(currentNumber, numberToGuess); i++) {
            currentNumberMarkers.addView(createNumberMarker(false));
        }
        if (numberToGuess < currentNumber) {
            ((FloatingActionButton) currentNumberMarkers.getChildAt(currentNumberMarkers.getChildCount() - 1)).setImageDrawable(getDrawable(R.drawable.ic_baseline_add_24));
        }
    }

    public FloatingActionButton createNumberMarker(boolean enabled) {
        FloatingActionButton numberMarker = new FloatingActionButton(this);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.setMargins(10, 25, 10, 25);
        numberMarker.setLayoutParams(linearLayoutParams);
        numberMarker.setEnabled(enabled);
        return numberMarker;
    }

    @Override
    public void setupTTS() {
        textToSpeak.clear();
        textToSpeak.add(numberToGuess.toString());
        readyToPlay = true;
        currentSentence = 0;
        numClick = 0;
    }

    @Override
    public void setRecyclerView() {

    }
}