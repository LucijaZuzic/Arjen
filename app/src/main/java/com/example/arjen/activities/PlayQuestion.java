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
import com.example.arjen.adapters.CustomAdapterPlayQuizQuestionOption;
import com.example.arjen.utility.Database;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.myTTS;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayQuestion extends MenuActivity {
    private ImageButton playTitle, playSubject, navigate, back, playQuestionText, addQuestion, newQuestion, deleteQuestion, questionList;
    private LinearLayout quizData, questionData, optionData;
    private List<String> options = new ArrayList<>();
    private RecyclerView optionRecyclerView;
    private Database.Questions.Question question;
    private int page = 1, answer = -1;
    private TextView title, subject, questionText, noResults;

    @Override
    public int getLayout() {
        return R.layout.activity_play_question;
    }

    @Override
    public void findViews() {
        title = findViewById(R.id.title);
        subject = findViewById(R.id.subject);
        playTitle = findViewById(R.id.playTitle);
        playSubject = findViewById(R.id.playSubject);
        navigate = findViewById(R.id.navigate);
        back = findViewById(R.id.back);
        playQuestionText = findViewById(R.id.playQuestionText);
        addQuestion = findViewById(R.id.addQuestion);
        quizData = findViewById(R.id.quizData);
        questionData = findViewById(R.id.questionData);
        optionData = findViewById(R.id.optionData);
        questionText = findViewById(R.id.questionText);
        noResults = findViewById(R.id.noResults);
        optionRecyclerView = findViewById(R.id.optionRecyclerView);
        newQuestion = findViewById(R.id.newQuestion);
        deleteQuestion = findViewById(R.id.deleteQuestion);
        questionList = findViewById(R.id.questionList);
    }

    @Override
    public void setRecyclerView() {
        recyclerView = optionRecyclerView;
    }

    @Override
    public void registerListeners() {
        navigate.setOnClickListener(v -> {
            if (page == 1) {
                page = 2;
                quizData.setVisibility(View.GONE);
                questionData.setVisibility(View.GONE);
                optionData.setVisibility(View.VISIBLE);
                navigate.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_back_24));
            } else {
                page = 1;
                quizData.setVisibility(View.VISIBLE);
                questionData.setVisibility(View.VISIBLE);
                optionData.setVisibility(View.GONE);
                navigate.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_forward_24));
            }
        });
        playTitle.setOnClickListener(v -> myTTS.speak(title.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        playSubject.setOnClickListener(v -> myTTS.speak(subject.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        back.setOnClickListener(v -> onBackPressed());
        addQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),AddQuestion.class);
            if (question != null) {
                MenuActivity.id = question.id;
                MenuActivity.quizId = question.quizId;
            }
            startActivity(intent);
        });
        playQuestionText.setOnClickListener(v -> myTTS.speak(questionText.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        newQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddQuestion.class);
            MenuActivity.id = null;
            if (question != null) {
                MenuActivity.quizId = question.quizId;
            }
            startActivity(intent);
        });
        deleteQuestion.setOnClickListener(v -> {
            if (question != null) {
                question.delete();
            }
            onBackPressed();
        });
        questionList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PlayQuiz.class);
            if (question != null) {
                MenuActivity.id = question.quizId;
            }
            MenuActivity.quizId = null;
            startActivity(intent);
        });
    }

    public int getAnswer() {
        return answer;
    }

    @Override
    public void fillData() {
        if (MenuActivity.quizId == null) {
            Intent intent = new Intent(getApplicationContext(), QuizList.class);
            MenuActivity.id = null;
            MenuActivity.quizId = null;
            finish();
            startActivity(intent);
        } else {
            Database.Quizes.Quiz quiz = Database.Quizes.findId(quizId);
            if (quiz != null) {
                title.setText(quiz.title);
                subject.setText(quiz.subject);
            } else {
                Intent intent = new Intent(getApplicationContext(), QuizList.class);
                MenuActivity.id = null;
                MenuActivity.quizId = null;
                finish();
                startActivity(intent);
            }
            if (MenuActivity.id != null) {
                question = Database.Questions.findId(MenuActivity.id);
                addQuestion.setVisibility(View.VISIBLE);
                deleteQuestion.setVisibility(View.VISIBLE);
                if (question != null) {
                    questionText.setText(question.questionText);
                    MenuActivity.quizId = question.quizId;
                    answer = question.answer;
                    options = question.options;
                } else {
                    Intent intent = new Intent(getApplicationContext(), QuizList.class);
                    MenuActivity.id = null;
                    MenuActivity.quizId = null;
                    finish();
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), PlayQuiz.class);
                MenuActivity.id = MenuActivity.quizId;
                MenuActivity.quizId = null;
                finish();
                startActivity(intent);
            }
        }
        optionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CustomAdapterPlayQuizQuestionOption customAdapterPlayQuizQuestionOption = new CustomAdapterPlayQuizQuestionOption(options, this);
        optionRecyclerView.setAdapter(customAdapterPlayQuizQuestionOption);
        if (Objects.requireNonNull(optionRecyclerView.getAdapter()).getItemCount() == 0) {
            optionRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            optionRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
        setupTTS();
    }

    @Override
    public void chooseOption() {
        if (currentSentence > 2) {
            if (currentSentence - 3 == answer) {
                myTTS.speak(getResources().getString(R.string.correct), TextToSpeech.QUEUE_FLUSH);
            } else {
                myTTS.speak(getResources().getString(R.string.incorrect), TextToSpeech.QUEUE_FLUSH);
            }
        } else {
            playNext();
        }
    }

    @Override
    public void setupTTS() {
        textToSpeak.clear();
        textToSpeak.add(getResources().getString(R.string.question) + " " + getResources().getString(R.string.is) + " " +  questionText.getText().toString());
        textToSpeak.add(getResources().getString(R.string.quiz_title) + " " + getResources().getString(R.string.is) + " " + title.getText().toString() + ".");
        textToSpeak.add(getResources().getString(R.string.subject) + " " + getResources().getString(R.string.is) + " " + subject.getText().toString() + ".");
        for (int i = 0; i < options.size(); i++) {
            textToSpeak.add((i + 1) + ". " + getResources().getString(R.string.option_substring) + " " + getResources().getString(R.string.is) + " " + options.get(i) + ".") ;
        }
        readyToPlay = true;
        currentSentence = 0;
        numClick = 0;
    }
}