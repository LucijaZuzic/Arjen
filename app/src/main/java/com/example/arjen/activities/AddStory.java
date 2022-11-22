package com.example.arjen.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arjen.utility.Database;
import com.example.arjen.R;
import com.example.arjen.adapters.CustomAdapterStoryQuestion;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.myTTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddStory extends MenuActivity {
    private ImageButton addStory, resetStory, navigate, playStory, deleteStory, playTitle, playStoryText, playQuestion, addQuestion, updateQuestion, resetQuestion, storyList, newStory;
    private LinearLayout storyData, questionData;
    private EditText title, storyText, questionText;
    private TextView modeText, noResults;
    private List<String> questions = new ArrayList<String>();
    private CustomAdapterStoryQuestion customAdapterStoryQuestion;
    private RecyclerView questionRecyclerView;
    private Database.Stories.Story story;
    private int question_to_edit = -1, page = 1;

    @Override
    public int getLayout() {
        return R.layout.activity_add_story;
    }

    @Override
    public void findViews() {
        addStory = findViewById(R.id.addStory);
        resetStory = findViewById(R.id.resetStory);
        navigate = findViewById(R.id.navigate);
        playStory = findViewById(R.id.playStory);
        deleteStory = findViewById(R.id.deleteStory);
        playTitle = findViewById(R.id.playTitle);
        playStoryText = findViewById(R.id.playStoryText);
        playQuestion = findViewById(R.id.playQuestion);
        addQuestion = findViewById(R.id.addQuestion);
        updateQuestion = findViewById(R.id.updateQuestion);
        resetQuestion = findViewById(R.id.resetQuestion);
        storyData = findViewById(R.id.storyData);
        questionData = findViewById(R.id.questionData);
        title = findViewById(R.id.title);
        storyText = findViewById(R.id.storyText);
        questionText = findViewById(R.id.questionText);
        modeText = findViewById(R.id.modeText);
        questionRecyclerView = findViewById(R.id.questionRecyclerView);
        noResults = findViewById(R.id.noResults);
        storyList = findViewById(R.id.storyList);
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
            id = null;
            quizId = null;
            startActivity(intent);
        });
        addStory.setOnClickListener(v -> {
            if (story != null) {
                story.update(title.getText().toString(), storyText.getText().toString(), questions);
            } else {
                Database.Stories.add(title.getText().toString(), storyText.getText().toString(), questions);
            }
            if (story != null) {
                id = story.id;
            }
            quizId = null;
            onBackPressed();
        });
        resetStory.setOnClickListener(v -> {
            if (story != null) {
                id = story.id;
            }
            quizId = null;
            onBackPressed();
        });
        navigate.setOnClickListener(v -> {
            if (page == 1) {
                page = 2;
                storyData.setVisibility(View.GONE);
                questionData.setVisibility(View.VISIBLE);
                navigate.setImageDrawable(getDrawable(R.drawable.ic_baseline_arrow_back_24));
            } else {
                page = 1;
                storyData.setVisibility(View.VISIBLE);
                questionData.setVisibility(View.GONE);
                navigate.setImageDrawable(getDrawable(R.drawable.ic_baseline_arrow_forward_24));
            }
        });
        playStory.setOnClickListener(v -> {
            if (story != null) {
                Intent intent = new Intent(getApplicationContext(), PlayStory.class);
                if (story != null) {
                    id = story.id;
                }
                quizId = null;
                startActivity(intent);
            }
        });
        deleteStory.setOnClickListener(v -> {
            if (story != null) {
                story.delete();
            }
            onBackPressed();
        });
        playTitle.setOnClickListener(v -> {
            myTTS.speak(title.getText().toString(), TextToSpeech.QUEUE_FLUSH);
        });
        playStoryText.setOnClickListener(v -> {
            myTTS.speak(storyText.getText().toString(), TextToSpeech.QUEUE_FLUSH);
        });
        playQuestion.setOnClickListener(v -> {
            myTTS.speak(questionText.getText().toString(), TextToSpeech.QUEUE_FLUSH);
        });
        addQuestion.setOnClickListener(v -> {
            insertQuestion();
        });
        updateQuestion.setOnClickListener(v -> {
            questions.set(question_to_edit, questionText.getText().toString());
            customAdapterStoryQuestion.notifyItemChanged(question_to_edit);
            questionRecyclerView.scrollToPosition(question_to_edit);
            question_to_edit = -1;
            addQuestion.setVisibility(View.VISIBLE);
            resetQuestion.setVisibility(View.GONE);
            updateQuestion.setVisibility(View.GONE);
            modeText.setText(getString(R.string.new_question));
        });
        resetQuestion.setOnClickListener(v -> {
            question_to_edit = -1;
            addQuestion.setVisibility(View.VISIBLE);
            resetQuestion.setVisibility(View.GONE);
            updateQuestion.setVisibility(View.GONE);
            modeText.setText(getString(R.string.new_question));
        });
        storyList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), StoryList.class);
            id = null;
            quizId = null;
            startActivity(intent);
        });
    }

    @Override
    public void fillData() {
        if (id != null) {
            story = Database.Stories.findId(id);
            playStory.setVisibility(View.VISIBLE);
            deleteStory.setVisibility(View.VISIBLE);
            if (story != null) {
                title.setText(story.title);
                storyText.setText(story.storyText);
                questions = story.questions;
            } else {
                Intent intent = new Intent(getApplicationContext(), StoryList.class);
                id = null;
                quizId = null;
                finish();
                startActivity(intent);
            }
        }
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        customAdapterStoryQuestion = new CustomAdapterStoryQuestion(questions, this);
        questionRecyclerView.setAdapter(customAdapterStoryQuestion);
        if (questionRecyclerView.getAdapter().getItemCount() == 0) {
            questionRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            questionRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
        setupTTS();
    }

    public void swapQuestions(int one ,int two) {
        Collections.swap(questions, one, two);
        customAdapterStoryQuestion.notifyItemChanged(one);
        customAdapterStoryQuestion.notifyItemChanged(two);
        questionRecyclerView.scrollToPosition(two);
        setupTTS();
    }

    public void editQuestion(int position) {
        questionText.setText(questions.get(position));
        question_to_edit = position;
        addQuestion.setVisibility(View.GONE);
        resetQuestion.setVisibility(View.VISIBLE);
        updateQuestion.setVisibility(View.VISIBLE);
        modeText.setText(getString(R.string.editing) + " " + (position + 1) + ". " + getString(R.string.question_substring));
        setupTTS();
    }

    public void removeQuestion(int position) {
        if (questions.size() != 0) {
            questions.remove(position);
            customAdapterStoryQuestion.notifyItemRemoved(position);
            customAdapterStoryQuestion.notifyItemRangeChanged(position, questions.size() - position);
            if (questionRecyclerView.getAdapter().getItemCount() == 0) {
                questionRecyclerView.setVisibility(View.GONE);
                noResults.setVisibility(View.VISIBLE);
            } else {
                questionRecyclerView.setVisibility(View.VISIBLE);
                noResults.setVisibility(View.GONE);
            }
        }
        setupTTS();
    }

    public void insertQuestion() {
        questions.add(questionText.getText().toString());
        customAdapterStoryQuestion.notifyItemInserted(questions.size() - 1);
        questionRecyclerView.scrollToPosition(questions.size() - 1);
        if (questionRecyclerView.getAdapter().getItemCount() == 0) {
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
    }
}