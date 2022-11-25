package com.example.arjen.activities;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arjen.R;
import com.example.arjen.adapters.CustomAdapterPlayStoryQuestion;
import com.example.arjen.utility.Database;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.myTTS;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayStory extends MenuActivity {
    private Database.Stories.Story story;
    private TextView title;
    private TextView storyText;
    private ImageButton playTitle, playStoryText, navigate, back, addStory, storyList, deleteStory, newStory;
    private List<String> questions = new ArrayList<>();
    private RecyclerView questionRecyclerView;
    private int page = 1;
    private LinearLayout storyData, questionData;

    @Override
    public int getLayout() {
        return R.layout.activity_play_story;
    }

    @Override
    public void findViews() {
        title = findViewById(R.id.title);
        storyText = findViewById(R.id.storyText);
        playTitle = findViewById(R.id.playTitle);
        playStoryText = findViewById(R.id.playStoryText);
        navigate = findViewById(R.id.navigate);
        back = findViewById(R.id.back);
        addStory = findViewById(R.id.addStory);
        storyData = findViewById(R.id.storyData);
        questionData = findViewById(R.id.questionData);
        questionRecyclerView = findViewById(R.id.questionRecyclerView);
        storyList = findViewById(R.id.storyList);
        deleteStory = findViewById(R.id.deleteStory);
        newStory = findViewById(R.id.newStory);
    }

    @Override
    public void setRecyclerView() {
        recyclerView = questionRecyclerView;
    }

    @Override
    public void registerListeners() {
        newStory.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddStory.class);
            MenuActivity.id = null;
            MenuActivity.quizId = null;
            startActivity(intent);
        });
        navigate.setOnClickListener(v -> {
            if (page == 1) {
                page = 2;
                storyData.setVisibility(View.GONE);
                questionData.setVisibility(View.VISIBLE);
                navigate.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_back_24));
            } else {
                page = 1;
                storyData.setVisibility(View.VISIBLE);
                questionData.setVisibility(View.GONE);
                navigate.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_forward_24));
            }
        });
        back.setOnClickListener(v -> {
            if (story != null) {
                MenuActivity.id = story.id;
            }
            MenuActivity.quizId = null;
            onBackPressed();
        });
        addStory.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddStory.class);
            if (story != null) {
                MenuActivity.id = story.id;
            }
            MenuActivity.quizId = null;
            startActivity(intent);
        });
        playTitle.setOnClickListener(v -> myTTS.speak(title.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        playStoryText.setOnClickListener(v -> myTTS.speak(storyText.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        storyList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), StoryList.class);
            MenuActivity.id = null;
            MenuActivity.quizId = null;
            startActivity(intent);
        });
        deleteStory.setOnClickListener(v -> {
            if (story != null) {
                story.delete();
            }
            onBackPressed();
        });
    }

    @Override
    public void fillData() {
        if (MenuActivity.id != null) {
            story = Database.Stories.findId(id);
            addStory.setVisibility(View.VISIBLE);
            deleteStory.setVisibility(View.VISIBLE);
            if (story != null) {
                title.setText(story.title);
                storyText.setText(story.storyText);
                questions = story.questions;
            } else {
                Intent intent = new Intent(getApplicationContext(), StoryList.class);
                MenuActivity.id = null;
                MenuActivity.quizId = null;
                finish();
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), StoryList.class);
            MenuActivity.id = null;
            MenuActivity.quizId = null;
            finish();
            startActivity(intent);
        }
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CustomAdapterPlayStoryQuestion customAdapterPlayStoryQuestion = new CustomAdapterPlayStoryQuestion(questions, this);
        questionRecyclerView.setAdapter(customAdapterPlayStoryQuestion);
        TextView noResults = findViewById(R.id.noResults);
        if (Objects.requireNonNull(questionRecyclerView.getAdapter()).getItemCount() == 0) {
            questionRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            questionRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
        setupTTS();
    }

    @Override
    public void chooseOption() {
        playNext();
        nextNumber();
    }

    @Override
    public void setupTTS() {
        textToSpeak.clear();
        textToSpeak.add(getResources().getString(R.string.story_title) + " " + getResources().getString(R.string.is) + " " +  title.getText().toString());
        textToSpeak.add(getResources().getString(R.string.story_next) + "." +  storyText.getText().toString());
        for (int i = 0; i < questions.size(); i++) {
            textToSpeak.add((i + 1) + ". " + getResources().getString(R.string.question_substring) + " " + getResources().getString(R.string.is) + " " + questions.get(i)) ;
        }
        readyToPlay = true;
        currentSentence = 0;
        numClick = 0;
    }
}