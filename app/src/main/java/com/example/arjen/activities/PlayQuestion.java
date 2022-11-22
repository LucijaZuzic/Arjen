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

import com.example.arjen.R;
import com.example.arjen.adapters.CustomAdapterPlayQuizQuestionOption;
import com.example.arjen.adapters.CustomAdapterQuizQuestionOption;
import com.example.arjen.utility.Database;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.myTTS;

import java.util.ArrayList;
import java.util.List;

public class PlayQuestion extends MenuActivity {
    private ImageButton playTitle, playSubject, navigate, back, playQuestionText, addQuestion, newQuestion, deleteQuestion;
    private LinearLayout quizData, questionData, optionData;
    private List<String> options = new ArrayList<String>();
    private CustomAdapterPlayQuizQuestionOption customAdapterPlayQuizQuestionOption;
    private RecyclerView optionRecyclerView;
    private Database.Questions.Question question;
    private int page = 1, answer = -1;
    private TextView title, subject, questionText, noResults;
    private Database.Quizes.Quiz quiz;

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
                navigate.setImageDrawable(getDrawable(R.drawable.ic_baseline_arrow_back_24));
            } else {
                page = 1;
                quizData.setVisibility(View.VISIBLE);
                questionData.setVisibility(View.VISIBLE);
                optionData.setVisibility(View.GONE);
                navigate.setImageDrawable(getDrawable(R.drawable.ic_baseline_arrow_forward_24));
            }
        });
        back.setOnClickListener(v -> {
            onBackPressed();
        });
        addQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),AddQuestion.class);
            if (question != null) {
                id = question.id;
                quizId = question.quizId;
            }
            startActivity(intent);
        });
        playQuestionText.setOnClickListener(v -> {
            myTTS.speak(questionText.getText().toString(), TextToSpeech.QUEUE_FLUSH);
        });
        newQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddQuestion.class);
            id = null;
            if (question != null) {
                quizId = question.quizId;
            }
            startActivity(intent);
        });
        deleteQuestion.setOnClickListener(v -> {
            if (question != null) {
                question.delete();
            }
            onBackPressed();
        });
    }

    public int getAnswer() {
        return answer;
    }

    @Override
    public void fillData() {
        if (quizId == null) {
            Intent intent = new Intent(getApplicationContext(), QuizList.class);
            id = null;
            quizId = null;
            finish();
            startActivity(intent);
        } else {
            quiz = Database.Quizes.findId(quizId);
            if (quiz != null) {
                title.setText(quiz.title);
                subject.setText(quiz.subject);
            } else {
                Intent intent = new Intent(getApplicationContext(), QuizList.class);
                id = null;
                quizId = null;
                finish();
                startActivity(intent);
            }
            if (id != null) {
                question = Database.Questions.findId(id);
                addQuestion.setVisibility(View.VISIBLE);
                deleteQuestion.setVisibility(View.VISIBLE);
                if (question != null) {
                    questionText.setText(question.questionText);
                    quizId = question.quizId;
                    answer = question.answer;
                    options = question.options;
                } else {
                    Intent intent = new Intent(getApplicationContext(), QuizList.class);
                    id = null;
                    quizId = null;
                    finish();
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), PlayQuiz.class);
                id = null;
                quizId = null;
                finish();
                startActivity(intent);
            }
        }
        optionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        customAdapterPlayQuizQuestionOption = new CustomAdapterPlayQuizQuestionOption(options, this);
        optionRecyclerView.setAdapter(customAdapterPlayQuizQuestionOption);
        if (optionRecyclerView.getAdapter().getItemCount() == 0) {
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
        textToSpeak.add(getResources().getString(R.string.subject) + " " + getResources().getString(R.string.is) + " " + subject.getText().toString());
        for (int i = 0; i < options.size(); i++) {
            textToSpeak.add((i + 1) + ". " + getResources().getString(R.string.option_substring) + " " + getResources().getString(R.string.is) + " " + options.get(i)) ;
        }
        readyToPlay = true;
    }
}