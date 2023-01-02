package com.example.arjen.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arjen.R;
import com.example.arjen.activities.AddQuestion;
import com.example.arjen.activities.AddQuiz;
import com.example.arjen.activities.AddStory;
import com.example.arjen.activities.MainActivity;
import com.example.arjen.activities.Numbers;
import com.example.arjen.activities.PlayQuestion;
import com.example.arjen.activities.PlayQuiz;
import com.example.arjen.activities.PlayStory;
import com.example.arjen.activities.QuestionList;
import com.example.arjen.activities.QuizList;
import com.example.arjen.activities.StoryList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MenuActivity extends AppCompatActivity {
    public static boolean playAllActive = true;
    private Menu menu;
    public List<String> textToSpeak = new ArrayList<String>();
    public int currentSentence = 0;
    private float x1, x2, y1, y2;
    static final int MIN_DISTANCE = 150;
    public boolean readyToPlay = false;
    public String id, quizId;
    public int numClick = 0;
    public int scrollPosition = 0;
    public ImageButton previousScroll, nextScroll;
    public RecyclerView recyclerView;
    private LinearLayout bottom;
    private Boolean blocked = false;
    public static MediaPlayer musicDing;
    private boolean timeout = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this instanceof MainActivity) {
            myTTS.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        myTTS.stop();
        if (musicDing != null  && musicDing.isPlaying()) {
            musicDing.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout allData = findViewById(R.id.allData);
        LinearLayout progressBar = findViewById(R.id.progressBar);
        allData.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        if (!myTTS.initialized) {
            myTTS.initTTS(getApplicationContext());
        }
        this.showSpeaking = findViewById(R.id.showSpeaking);
        myTTS.setActivitySpeaking(this, showSpeaking);
        musicDing = MediaPlayer.create(this, R.raw.new_activity);
        musicDing.start();
        musicDing.setOnCompletionListener(v -> {
            if (!(this instanceof Numbers)) {
                myTTS.speak(activityToText(), TextToSpeech.QUEUE_FLUSH);
            }
            fillData();
            allData.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            setupTTS();
            menuStartStopSetup();
            menuPlayModeSetup();
        });
    }

    public String activityToText() {
        if (this.getClass().equals(AddQuestion.class)) {
            return getResources().getString(R.string.add_question);
        }
        if (this.getClass().equals(AddQuiz.class)) {
            return getResources().getString(R.string.add_quiz);
        }
        if (this.getClass().equals(AddStory.class)) {
            return getResources().getString(R.string.add_story);
        }
        if (this.getClass().equals(PlayQuestion.class)) {
            return getResources().getString(R.string.play_question);
        }
        if (this.getClass().equals(PlayQuiz.class)) {
            return getResources().getString(R.string.play_quiz);
        }
        if (this.getClass().equals(PlayStory.class)) {
            return getResources().getString(R.string.play_story);
        }
        if (this.getClass().equals(QuizList.class)) {
            return getResources().getString(R.string.quiz_list);
        }
        if (this.getClass().equals(StoryList.class)) {
            return getResources().getString(R.string.story_list);
        }
        if (this.getClass().equals(QuestionList.class)) {
            return getResources().getString(R.string.question_list);
        }
        if (this.getClass().equals(Numbers.class)) {
            int words = 2;
            Intent intent = getIntent();
            if (intent.hasExtra("words")) {
                words = intent.getExtras().getInt("words");
            }
            if (words == 1) {
                return getResources().getString(R.string.numbers_words);
            }
            if (words == 0) {
                return getResources().getString(R.string.numbers_sound);
            }
            if (words == 2) {
                return getResources().getString(R.string.numbers_words_sound);
            }
            return getResources().getString(R.string.numbers);
        }
        if (this.getClass().equals(MainActivity.class)) {
            return getResources().getString(R.string.main_activity);
        }
        return "";
    }

    private TextView showSpeaking;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            id = intent.getExtras().getString("id");
        }
        if (intent.hasExtra("quizId")) {
            quizId = intent.getExtras().getString("quizId");
        }
        findViews();
        setRecyclerView();
        previousScroll = findViewById(R.id.previousScroll);
        nextScroll = findViewById(R.id.nextScroll);
        registerListeners();
        if (previousScroll != null && recyclerView != null) {
            previousScroll.setOnClickListener(v -> {
                LinearLayoutManager myLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (myLayoutManager != null) {
                    scrollPosition = myLayoutManager.findFirstVisibleItemPosition();
                }
                if (scrollPosition >= 1) {
                    scrollPosition -= 1;
                }
                recyclerView.scrollToPosition(scrollPosition);
            });
        }
        if (nextScroll != null && recyclerView != null) {
            nextScroll.setOnClickListener(v -> {
                LinearLayoutManager myLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (myLayoutManager != null) {
                    scrollPosition = myLayoutManager.findLastVisibleItemPosition();
                }
                if (scrollPosition < recyclerView.getAdapter().getItemCount() - 1) {
                    scrollPosition += 1;
                }
                recyclerView.scrollToPosition(scrollPosition);
            });
        }
        bottom = this.findViewById(R.id.bottom);
        final ViewGroup viewGroup = this.findViewById(R.id.mainLayout);
        getAllChildren(viewGroup);

        // ContentView is the root view of the layout of this activity/fragment
        viewGroup.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        viewGroup.getWindowVisibleDisplayFrame(r);
                        int screenHeight = viewGroup.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!isKeyboardShowing) {
                                isKeyboardShowing = true;
                                onKeyboardVisibilityChanged(true);
                            }
                        }
                        else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                onKeyboardVisibilityChanged(false);
                            }
                        }
                    }
                });
    }

    public void nextNumber() {
        currentSentence++;
        if (currentSentence >= textToSpeak.size()) {
            currentSentence = 0;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void getAllChildren(ViewGroup viewGroup) {
        viewGroup.setOnTouchListener((v, event) -> {
            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    y1 = event.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();
                        double xDist = x1 - x2;
                        double yDist = y1 - y2;
                        if (Math.abs(xDist) > MIN_DISTANCE && Math.abs(xDist) > Math.abs(yDist)) {
                            if (playAllActive && readyToPlay && textToSpeak.size() != 0 && myTTS.canContinue && !timeout && (musicDing == null || !musicDing.isPlaying())) {
                                if (x2 > x1) {
                                    chooseOption();
                                    numClick++;
                                } else {
                                    playNext();
                                }
                                timeout = true;
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        timeout = false;
                                    }
                                }, 1000);
                            }
                        }
                        if (Math.abs(yDist) > MIN_DISTANCE && Math.abs(yDist) > Math.abs(xDist)) {
                            if (y2 > y1) {
                                onBackPressed();
                            } else {
                                if (myTTS.isPaused) {
                                    myTTS.play();
                                } else {
                                    Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.stopped), Toast.LENGTH_SHORT).show();
                                    myTTS.pause();
                                }
                                menuStartStopSetup();
                            }
                        }
                    break;
            }
            return true;
            //return super.onTouchEvent(event);
        });
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                getAllChildren((ViewGroup) child);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;
        menuStartStopSetup();
        menuPlayModeSetup();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                if (this instanceof AddQuiz || this instanceof AddQuestion || this instanceof AddStory) {
                    otherActivityBackPressed(MainActivity.class, null, null);
                } else {
                    startWithNewId(MainActivity.class, null, null);
                }
                return true;
            case R.id.playAll:
                playAllActive = !playAllActive;
                if (playAllActive) {
                    myTTS.speak(getResources().getString(R.string.play_all_active), TextToSpeech.QUEUE_FLUSH);
                } else {
                    myTTS.speak(getResources().getString(R.string.play_all_inactive), TextToSpeech.QUEUE_FLUSH);
                }
                menuPlayModeSetup();
                return true;
            case R.id.pause:
                if (myTTS.isPaused) {
                    myTTS.play();
                } else {
                    Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.stopped), Toast.LENGTH_SHORT).show();
                    myTTS.pause();
                }
                menuStartStopSetup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void menuPlayModeSetup() {
        if (menu == null) {
            return;
        }
        if (playAllActive) {
            menu.getItem(1).setIcon(getDrawable(R.drawable.ic_baseline_ads_click_24));
        } else {
            menu.getItem(1).setIcon(getDrawable(R.drawable.ic_baseline_swipe_24));
        }
    }

    public void menuStartStopSetup() {
        if (menu == null) {
            return;
        }
        if (myTTS.isPaused) {
            menu.getItem(2).setIcon(getDrawable(R.drawable.ic_baseline_play_arrow_24));
        } else {
            menu.getItem(2).setIcon(getDrawable(R.drawable.ic_baseline_pause_24));
        }
    }

    public abstract int getLayout();

    public abstract void findViews();

    public abstract void registerListeners();

    public abstract void fillData();

    public abstract void chooseOption();

    public abstract void setupTTS();

    public abstract void setRecyclerView();

    public void playNext() {
        if (numClick > 0) {
            nextNumber();
        }
        myTTS.speak(textToSpeak.get(currentSentence), TextToSpeech.QUEUE_FLUSH);
        numClick++;
    }

    boolean isKeyboardShowing = false;
    void onKeyboardVisibilityChanged(boolean opened) {
        if (isKeyboardShowing) {
            bottom.setVisibility(View.GONE);
        } else {
            bottom.setVisibility(View.VISIBLE);
        }

    }

    private DialogInterface.OnClickListener backDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    instantBackPressed();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private DialogInterface.OnClickListener changeDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    applyChanges();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private Class listActivity;
    private String newId, newQuizId;

    public void startWithNewId(Class listActivity, String newId, String newQuizId) {
        this.listActivity = listActivity;
        this.newQuizId = newQuizId;
        this.newId = newId;
        Intent intent = new Intent(getApplicationContext(), listActivity);
        intent.putExtra("id", newId);
        intent.putExtra("quizId", newQuizId);
        startActivity(intent);
    }

    private DialogInterface.OnClickListener listDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    startWithNewId(listActivity, newId, newQuizId);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (this instanceof AddQuiz || this instanceof AddQuestion || this instanceof AddStory) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(this.getApplicationContext().getResources().getString(R.string.go_back))
                    .setPositiveButton(this.getApplicationContext().getResources().getString(R.string.yes), backDialogClickListener)
                    .setNegativeButton(this.getApplicationContext().getResources().getString(R.string.no), backDialogClickListener).show();
        } else {
            super.onBackPressed();
        }
    }

    public void instantBackPressed() {
        super.onBackPressed();
    }

    public void confirmBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getApplicationContext().getResources().getString(R.string.confirm_change))
                .setPositiveButton(this.getApplicationContext().getResources().getString(R.string.yes), changeDialogClickListener)
                .setNegativeButton(this.getApplicationContext().getResources().getString(R.string.no), changeDialogClickListener).show();
    }

    public void otherActivityBackPressed(Class listActivity, String newQuizId, String newId) {
        this.listActivity = listActivity;
        this.newQuizId = newQuizId;
        this.newId = newId;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getApplicationContext().getResources().getString(R.string.go_back))
                .setPositiveButton(this.getApplicationContext().getResources().getString(R.string.yes), listDialogClickListener)
                .setNegativeButton(this.getApplicationContext().getResources().getString(R.string.no), listDialogClickListener).show();
    }

    public void applyChanges() {

    }
}
