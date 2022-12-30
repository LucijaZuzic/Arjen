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
import com.example.arjen.adapters.CustomAdapterQuizQuestionList;
import com.example.arjen.utility.Database;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.interfaces.QuizInterface;
import com.example.arjen.utility.interfaces.ShowListInterface;
import com.example.arjen.utility.myTTS;

import java.util.Objects;

public class PlayQuiz extends MenuActivity implements ShowListInterface, QuizInterface {
    private Database.Quizes.Quiz quiz;
    private TextView title, subject, noResults;
    private ImageButton playTitle, playSubject, navigate, back, addQuiz, quizList, deleteQuiz, newQuiz;
    private CustomAdapterQuizQuestionList customAdapterQuizQuestionList;
    private RecyclerView questionRecyclerView;
    private int page = 1;
    private LinearLayout quizData, questionData;

    @Override
    public void setRecyclerView() {
        recyclerView = questionRecyclerView;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_play_quiz;
    }

    @Override
    public void findViews() {
        setContentView(R.layout.activity_play_quiz);
        title = findViewById(R.id.title);
        subject = findViewById(R.id.subject);
        playTitle = findViewById(R.id.playTitle);
        playSubject = findViewById(R.id.playSubject);
        navigate = findViewById(R.id.navigate);
        back = findViewById(R.id.back);
        addQuiz = findViewById(R.id.addQuiz);
        quizData = findViewById(R.id.quizData);
        questionData = findViewById(R.id.questionData);
        quizList = findViewById(R.id.quizList);
        deleteQuiz = findViewById(R.id.deleteQuiz);
        newQuiz = findViewById(R.id.newQuiz);
    }

    @Override
    public void registerListeners() {
        newQuiz.setOnClickListener(v -> {
            if (quiz != null) {
                Intent intent = new Intent(getApplicationContext(), AddQuiz.class);
                id = null;
                quizId = null;
                startActivity(intent);
            }
        });
        navigate.setOnClickListener(v -> {
            if (page == 1) {
                page = 2;
                quizData.setVisibility(View.GONE);
                questionData.setVisibility(View.VISIBLE);
                navigate.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_back_24));
            } else {
                page = 1;
                quizData.setVisibility(View.VISIBLE);
                questionData.setVisibility(View.GONE);
                navigate.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_forward_24));
            }
        });
        back.setOnClickListener(v -> {
            if (quiz != null) {
                onBackPressed();
            }
        });
        addQuiz.setOnClickListener(v -> {
            if (quiz != null) {
                Intent intent = new Intent(getApplicationContext(), AddQuiz.class);
                startActivity(intent);
            }
        });
        playTitle.setOnClickListener(v -> myTTS.speak(title.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        playSubject.setOnClickListener(v -> myTTS.speak(subject.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        quizList.setOnClickListener(v -> {
            if (quiz != null) {
                Intent intent = new Intent(getApplicationContext(), QuizList.class);
                id = null;
                quizId = null;
                startActivity(intent);
            }
        });
        deleteQuiz.setOnClickListener(v -> {
            if (quiz != null) {
                quiz.startDelete(this);
            }
        });
    }

    @Override
    public void fillData() {
        quiz = null;
        if (id != null) {
            quiz = Database.Quizes.findId(id);
            Database.Questions.getForQuiz(id, this);
            addQuiz.setVisibility(View.VISIBLE);
            deleteQuiz.setVisibility(View.VISIBLE);
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
        } else {
            Intent intent = new Intent(getApplicationContext(), QuizList.class);
            id = null;
            quizId = null;
            finish();
            startActivity(intent);
        }
        questionRecyclerView = findViewById(R.id.questionRecyclerView);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        customAdapterQuizQuestionList = new CustomAdapterQuizQuestionList(this);
        questionRecyclerView.setAdapter(customAdapterQuizQuestionList);
        noResults = findViewById(R.id.noResults);
    }

    @Override
    public void chooseOption() {
        if (currentSentence > 1) {
            playQuestion(Database.Questions.questionList.get(currentSentence - 2).id);
        } else {
            playNext();
        }
    }

    @Override
    public void showList() {
        customAdapterQuizQuestionList = new CustomAdapterQuizQuestionList(this);
        questionRecyclerView.setAdapter(customAdapterQuizQuestionList);
        if (Objects.requireNonNull(questionRecyclerView.getAdapter()).getItemCount() == 0) {
            questionRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            questionRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
        setupTTS();
    }

    public void playQuestion(String id) {
        Intent intent = new Intent(getApplicationContext(), PlayQuestion.class);
        MenuActivity.id = id;
        if (quiz != null) {
            MenuActivity.quizId = quiz.id;
        }
        startActivity(intent);
    }

    public void editQuestion(String id) {
        Intent intent = new Intent(getApplicationContext(), AddQuestion.class);
        MenuActivity.id = id;
        if (quiz != null) {
            MenuActivity.quizId = quiz.id;
        }
        startActivity(intent);
    }

    public void deleteQuestion(int position) {
        Objects.requireNonNull(questionRecyclerView.getAdapter()).notifyItemRemoved(position);
        questionRecyclerView.getAdapter().notifyItemRangeChanged(position, Database.Questions.questionList.size() - position);
        textToSpeak.clear();
        if (quiz != null) {
            textToSpeak.add(quiz.title);
            textToSpeak.add(quiz.subject);
        }
        for (Database.Questions.Question question: Database.Questions.questionList) {
            textToSpeak.add(question.questionText);
        }
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
    public void setupTTS() {
        textToSpeak.clear();
        textToSpeak.add(getResources().getString(R.string.quiz_title) + " " + getResources().getString(R.string.is) + " " + title.getText().toString() + ".");
        textToSpeak.add(getResources().getString(R.string.subject) + " " + getResources().getString(R.string.is) + " " + subject.getText().toString() + ".");
        for (int i = 0; i < Database.Questions.questionList.size(); i++) {
            textToSpeak.add(getResources().getString(R.string.question) + " " + getResources().getString(R.string.is) + " " + Database.Questions.questionList.get(i).questionText) ;
        }
        readyToPlay = true;
        currentSentence = 0;
        numClick = 0;
    }
}