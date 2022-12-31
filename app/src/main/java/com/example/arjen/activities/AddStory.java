package com.example.arjen.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arjen.utility.Database;
import com.example.arjen.R;
import com.example.arjen.adapters.CustomAdapterStoryQuestion;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.myTTS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AddStory extends MenuActivity {
    private ImageButton addStory, resetStory, navigate, playStory, deleteStory, playTitle, playStoryText, playQuestion, addQuestion, updateQuestion, resetQuestion, storyList, newStory;
    private LinearLayout storyData, questionData;
    private EditText title, storyText, questionText;
    private TextView modeText, noResults;
    private List<String> questions = new ArrayList<>();
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
    public void applyChanges() {
        if (story != null) {
            story.update(title.getText().toString(), storyText.getText().toString(), questions);
        } else {
            Database.Stories.add(title.getText().toString(), storyText.getText().toString(), questions);
        }
        instantBackPressed();
    }

    @Override
    public void registerListeners() {
        newStory.setOnClickListener(v -> {
            otherActivityBackPressed(AddStory.class, null, null);
        });
        addStory.setOnClickListener(v -> {
            confirmBackPressed();
        });
        resetStory.setOnClickListener(v -> {
            onBackPressed();
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
        playStory.setOnClickListener(v -> {
            if (story != null) {
                otherActivityBackPressed(PlayStory.class, null, story.id);
            }
        });
        deleteStory.setOnClickListener(v -> {
            if (story != null) {
                story.startDelete(this);
            }
        });
        playTitle.setOnClickListener(v -> myTTS.speak(title.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        playStoryText.setOnClickListener(v -> myTTS.speak(storyText.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        playQuestion.setOnClickListener(v -> myTTS.speak(questionText.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        addQuestion.setOnClickListener(v -> insertQuestion());
        updateQuestion.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(this.getApplicationContext().getResources().getString(R.string.confirm_change))
                    .setPositiveButton(this.getApplicationContext().getResources().getString(R.string.yes), editDialogClickListener)
                    .setNegativeButton(this.getApplicationContext().getResources().getString(R.string.no), editDialogClickListener).show();
        });
        resetQuestion.setOnClickListener(v -> {
            question_to_edit = -1;
            addQuestion.setVisibility(View.VISIBLE);
            resetQuestion.setVisibility(View.GONE);
            updateQuestion.setVisibility(View.GONE);
            modeText.setText(getString(R.string.new_question));
            questionText.setText("");
        });
        storyList.setOnClickListener(v -> {
            otherActivityBackPressed(StoryList.class, null, null);
        });
    }

    @Override
    public void fillData() {
        story = null;
        if (id != null) {
            story = Database.Stories.findId(id);
            playStory.setVisibility(View.VISIBLE);
            deleteStory.setVisibility(View.VISIBLE);
            if (story != null) {
                title.setText(story.title);
                storyText.setText(story.storyText);
                questions = story.questions;
            } else {
                finish();
                startWithNewId(StoryList.class, null, null);
            }
        }
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        customAdapterStoryQuestion = new CustomAdapterStoryQuestion(questions, this);
        questionRecyclerView.setAdapter(customAdapterStoryQuestion);
        if (Objects.requireNonNull(questionRecyclerView.getAdapter()).getItemCount() == 0) {
            questionRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            questionRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
    }

    public void swapQuestions(int one ,int two) {
        Collections.swap(questions, one, two);
        customAdapterStoryQuestion.notifyItemChanged(one);
        customAdapterStoryQuestion.notifyItemChanged(two);
        questionRecyclerView.scrollToPosition(two);
        setupTTS();
    }

    private DialogInterface.OnClickListener editDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    actuallyEditQuestion();

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    public void actuallyEditQuestion() {
        questions.set(question_to_edit, questionText.getText().toString());
        customAdapterStoryQuestion.notifyItemChanged(question_to_edit);
        questionRecyclerView.scrollToPosition(question_to_edit);
        question_to_edit = -1;
        addQuestion.setVisibility(View.VISIBLE);
        resetQuestion.setVisibility(View.GONE);
        updateQuestion.setVisibility(View.GONE);
        modeText.setText(getString(R.string.new_question));
        questionText.setText("");
        setupTTS();
    }

    public void editQuestion(int position) {
        questionText.setText(questions.get(position));
        question_to_edit = position;
        addQuestion.setVisibility(View.GONE);
        resetQuestion.setVisibility(View.VISIBLE);
        updateQuestion.setVisibility(View.VISIBLE);
        modeText.setText(getString(R.string.editing) + " " + (position + 1) + ". " + getString(R.string.question_substring));
    }

    private int somePosition;

    private DialogInterface.OnClickListener removeDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    actuallyRemoveQuestion(somePosition);

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    public void actuallyRemoveQuestion(int position) {
        if (questions.size() != 0) {
            questions.remove(position);
            customAdapterStoryQuestion.notifyItemRemoved(position);
            customAdapterStoryQuestion.notifyItemRangeChanged(position, questions.size() - position);
            if (Objects.requireNonNull(questionRecyclerView.getAdapter()).getItemCount() == 0) {
                questionRecyclerView.setVisibility(View.GONE);
                noResults.setVisibility(View.VISIBLE);
            } else {
                questionRecyclerView.setVisibility(View.VISIBLE);
                noResults.setVisibility(View.GONE);
            }
        }
        setupTTS();
    }

    public void removeQuestion(int position) {
        somePosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getApplicationContext().getResources().getString(R.string.question_delete))
                .setPositiveButton(this.getApplicationContext().getResources().getString(R.string.yes), removeDialogClickListener)
                .setNegativeButton(this.getApplicationContext().getResources().getString(R.string.no), removeDialogClickListener).show();
    }

    private DialogInterface.OnClickListener insertDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    actuallyInsertQuestion();

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    public void actuallyInsertQuestion() {
        questions.add(questionText.getText().toString());
        customAdapterStoryQuestion.notifyItemInserted(questions.size() - 1);
        questionRecyclerView.scrollToPosition(questions.size() - 1);
        if (Objects.requireNonNull(questionRecyclerView.getAdapter()).getItemCount() == 0) {
            questionRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            questionRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
        questionText.setText("");
        setupTTS();
    }

    public void insertQuestion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getApplicationContext().getResources().getString(R.string.confirm_change))
                .setPositiveButton(this.getApplicationContext().getResources().getString(R.string.yes), insertDialogClickListener)
                .setNegativeButton(this.getApplicationContext().getResources().getString(R.string.no), insertDialogClickListener).show();
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
            textToSpeak.add(getResources().getString(R.string.question) + " " + getResources().getString(R.string.is) + " " + questions.get(i)) ;
        }
        readyToPlay = true;
        currentSentence = 0;
        numClick = 0;
    }
}