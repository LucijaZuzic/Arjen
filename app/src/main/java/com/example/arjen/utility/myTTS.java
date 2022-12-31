package com.example.arjen.utility;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arjen.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class myTTS {
    private static TextToSpeech TTS;
    private static MenuActivity activitySpeaking;
    private static TextView showSpeaking;
    private static Context context;
    private static Locale myLocale = serbianLatinLocale();
    private static int currentSentence = 0, mode = TextToSpeech.QUEUE_FLUSH;
    private static List<String> textToSpeak = new ArrayList<String>();
    public static boolean isPaused = false;
    public static boolean initialized = false;
    public static boolean canContinue = true;

    public static void setActivitySpeaking(MenuActivity activitySpeaking, TextView showSpeaking) {
        myTTS.activitySpeaking = activitySpeaking;
        myTTS.showSpeaking = showSpeaking;
    }

    private static UtteranceProgressListener progressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
            canContinue = false;
            showSpeaking.setText(activitySpeaking.getResources().getString(R.string.showSpeaking) + " " + textToSpeak.get(currentSentence));
        }

        @Override
        public void onDone(String s) {
            canContinue = true;
            showSpeaking.setText(activitySpeaking.getResources().getString(R.string.showSpeaking) + " " + textToSpeak.get(currentSentence));
            currentSentence++;
            if (currentSentence >= textToSpeak.size()) {
                currentSentence = 0;
                showSpeaking.setText(activitySpeaking.getResources().getString(R.string.showSpeaking));
            } else {
                TTS.speak(textToSpeak.get(currentSentence), mode, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
                mode = TextToSpeech.QUEUE_ADD;
                showSpeaking.setText(activitySpeaking.getResources().getString(R.string.showSpeaking) + " " + textToSpeak.get(currentSentence));
            }
        }

        @Override
        public void onError(String s) {
            canContinue = true;
            TTS.stop();
            showSpeaking.setText(activitySpeaking.getResources().getString(R.string.showSpeaking) + " " + textToSpeak.get(currentSentence) + " REPEAT");
            if (currentSentence < textToSpeak.size()) {
                TTS.speak(textToSpeak.get(currentSentence), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
                mode = TextToSpeech.QUEUE_ADD;
            }
        }

        @Override
        public void onError(String s, int code) {
            canContinue = true;
            TTS.stop();
            showSpeaking.setText(activitySpeaking.getResources().getString(R.string.showSpeaking) + " " + textToSpeak.get(currentSentence) + " REPEAT " + code);
            if (currentSentence < textToSpeak.size()) {
                TTS.speak(textToSpeak.get(currentSentence), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
                mode = TextToSpeech.QUEUE_ADD;
            }
        }
    };

    public static void play() {
        isPaused = false;
        canContinue = true;
        speak(null, TextToSpeech.QUEUE_ADD);
    }

    public static void pause() {
        isPaused = true;
        canContinue = true;
        if (TTS != null) {
            TTS.stop();
        }
    }

    public static void stop() {
        textToSpeak.clear();
        currentSentence = 0;
        canContinue = true;
        if (TTS != null) {
            TTS.stop();
        }
    }

    public static void destroy() {
        initialized = false;
        if (TTS != null) {
            TTS.shutdown();
        }
    }

    public static void initTTS(Context newContext) {
        initialized = false;
        context = newContext;

        TTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    if (TTS.isLanguageAvailable(myLocale) != TextToSpeech.LANG_MISSING_DATA && TTS.isLanguageAvailable(myLocale) != TextToSpeech.LANG_NOT_SUPPORTED) {
                        TTS.setLanguage(serbianLatinLocale());
                    }
                    initialized = true;
                    TTS.setOnUtteranceProgressListener(progressListener);
                } else {
                }
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
        if (!canContinue) {
            myTTS.stop();
        }
        isPaused = false;
        if (activitySpeaking != null) {
            activitySpeaking.menuStartStopSetup();
        }
        if (queueMode == TextToSpeech.QUEUE_FLUSH) {
            textToSpeak.clear();
            currentSentence = 0;
        }
        if (text != null) {
            List<String> sentences = Arrays.asList(text.trim().split("[\\.,:;?!]"));
            for (int i = 0; i < sentences.size(); i++) {
                sentences.set(i, sentences.get(i).replace("\n", " "));
                sentences.set(i, sentences.get(i).replace("-", " "));
                sentences.set(i, sentences.get(i).replace("'", " "));
                sentences.set(i, sentences.get(i).replace("\"", " "));
                sentences.set(i, sentences.get(i).replaceAll("\\s+", " "));
                sentences.set(i, sentences.get(i).trim());
            }
            textToSpeak.addAll(sentences);
        }
        mode = queueMode;
        if (currentSentence < textToSpeak.size()) {
            TTS.speak(textToSpeak.get(currentSentence), mode, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
            mode = TextToSpeech.QUEUE_ADD;
        }
    }
}
