package com.example.arjen.utility;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class myTTS {
    private static TextToSpeech TTS;
    private static Context context;
    private static Locale myLocale = serbianLatinLocale();
    private static int currentSentence = 0, mode = TextToSpeech.QUEUE_FLUSH;
    private static List<String> textToSpeak = new ArrayList<String>();
    public static boolean isPaused = false;

    private static UtteranceProgressListener progressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
        }

        @Override
        public void onDone(String s) {
            currentSentence++;
            if (currentSentence >= textToSpeak.size()) {
                currentSentence = 0;
            }
        }

        @Override
        public void onError(String s) {

        }
    };

    public static void play() {
        isPaused = false;
        speak(null, TextToSpeech.QUEUE_ADD);
    }

    public static void pause() {
        isPaused = true;
        if (TTS != null) {
            TTS.stop();
        }
    }

    public static void stop() {
        if (TTS != null) {
            TTS.stop();
            textToSpeak.clear();
            currentSentence = 0;
        }
    }

    public static void initTTS(Context newContext) {
        context = newContext;
        TTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    if (TTS.isLanguageAvailable(myLocale) != TextToSpeech.LANG_MISSING_DATA && TTS.isLanguageAvailable(myLocale) != TextToSpeech.LANG_NOT_SUPPORTED) {
                        TTS.setLanguage(serbianLatinLocale());
                    }
                }
                TTS.setOnUtteranceProgressListener(progressListener);
            }
        });
    }

    private static Locale serbianLatinLocale(){
        Locale locale = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            for (Locale checkLocale : Locale.getAvailableLocales()) {
                if (checkLocale.getISO3Language().equals("srp") && checkLocale.getCountry().equals("LATN") && checkLocale.getVariant().equals("")) {
                    locale = checkLocale;
                }
            }
        } else {
            locale = new Locale.Builder().setLanguage("sr").setRegion("RS").setScript("Latn").build();
        }
        return locale;
    }

    public static void speak(String text, int queueMode) {
        if (queueMode == TextToSpeech.QUEUE_FLUSH) {
            textToSpeak.clear();
            currentSentence = 0;
        }
        if (text != null) {
            //List<String> words = Arrays.asList(text.trim().split("\\s+"));
            List<String> sentences = Arrays.asList(text.trim().split("\\."));
            textToSpeak.addAll(sentences);
        }
        mode = queueMode;
        int startingSentence = currentSentence;
        for (int i = startingSentence; i < textToSpeak.size(); i++) {
            if (!isPaused && TTS != null) {
                TTS.speak(textToSpeak.get(i), mode, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
                mode = TextToSpeech.QUEUE_ADD;
            }
        }
    }

}
