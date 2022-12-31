package com.example.arjen.activities;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.arjen.adapters.CustomAdapterQuizQuestionList;
import com.example.arjen.utility.Database;
import com.example.arjen.R;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.interfaces.QuizInterface;
import com.example.arjen.utility.interfaces.ShowListInterface;
import com.example.arjen.utility.myTTS;

import java.util.Objects;

public class AddQuiz extends MenuActivity implements ShowListInterface, QuizInterface {
    private ImageButton addQuiz, resetQuiz, navigate, playQuiz, addQuestion, deleteQuiz, playTitle, playSubject, quizList, newQuiz;
    private LinearLayout quizData, questionData;
    private EditText title, subject;
    private Database.Quizes.Quiz quiz;
    private int page = 1;
    private RecyclerView questionRecyclerView;
    private TextView noResults;

    @Override
    public int getLayout() {
        return R.layout.activity_add_quiz;
    }

    @Override
    public void findViews() {
        addQuiz = findViewById(R.id.addQuiz);
        resetQuiz = findViewById(R.id.resetQuiz);
        navigate = findViewById(R.id.navigate);
        playQuiz = findViewById(R.id.playQuiz);
        addQuestion = findViewById(R.id.addQuestion);
        deleteQuiz = findViewById(R.id.deleteQuiz);
        playTitle = findViewById(R.id.playTitle);
        playSubject = findViewById(R.id.playSubject);
        quizData = findViewById(R.id.quizData);
        questionData = findViewById(R.id.questionData);
        title = findViewById(R.id.title);
        subject = findViewById(R.id.subject);
        noResults = findViewById(R.id.noResults);
        quizList = findViewById(R.id.quizList);
        newQuiz = findViewById(R.id.newQuiz);
    }

    @Override
    public void setRecyclerView() {
        recyclerView = questionRecyclerView;
    }

    @Override
    public void applyChanges() {
        if (quiz != null) {
            quiz.update(title.getText().toString(), subject.getText().toString());
        } else {
            Database.Quizes.add(title.getText().toString(), subject.getText().toString());
        }
        instantBackPressed();
    }

    @Override
    public void registerListeners() {
        newQuiz.setOnClickListener(v -> {
            otherActivityBackPressed(AddQuiz.class, null, null);
        });
        addQuiz.setOnClickListener(v -> {
            confirmBackPressed();
        });
        resetQuiz.setOnClickListener(v -> {
            onBackPressed();
        });
        addQuestion.setOnClickListener(v -> {
            if (quiz != null) {
                otherActivityBackPressed(AddQuestion.class, quiz.id, null);
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
        playQuiz.setOnClickListener(v -> {
            if (quiz != null) {
                otherActivityBackPressed(PlayQuiz.class, quiz.id, quiz.id);
            }
        });
        deleteQuiz.setOnClickListener(v -> {
            if (quiz != null) {
                quiz.startDelete(this);
            }
        });
        playTitle.setOnClickListener(v -> myTTS.speak(title.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        playSubject.setOnClickListener(v -> myTTS.speak(subject.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        quizList.setOnClickListener(v -> {
            otherActivityBackPressed(QuizList.class, null, null);
        });
    }

    @Override
    public void fillData() {
        quiz = null;
        if (id != null) {
            quiz = Database.Quizes.findId(id);
            Database.Questions.getForQuiz(id, this);
            playQuiz.setVisibility(View.VISIBLE);
            deleteQuiz.setVisibility(View.VISIBLE);
            addQuestion.setVisibility(View.VISIBLE);
            if (quiz != null) {
                title.setText(quiz.title);
                subject.setText(quiz.subject);
            } else {
                finish();
                startWithNewId(QuizList.class, null, null);
            }
        }
        questionRecyclerView = findViewById(R.id.questionRecyclerView);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        CustomAdapterQuizQuestionList customAdapterQuizQuestionList = new CustomAdapterQuizQuestionList(this);
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
        if (quiz != null) {
            otherActivityBackPressed(PlayQuestion.class, quiz.id, id);
        }
    }

    public void editQuestion(String id) {
        if (quiz != null) {
            otherActivityBackPressed(AddQuestion.class, quiz.id, id);
        }
    }

    public void deleteQuestion(int position) {
        Objects.requireNonNull(questionRecyclerView.getAdapter()).notifyItemRemoved(position);
        questionRecyclerView.getAdapter().notifyItemRangeChanged(position, Database.Questions.questionList.size() - position);
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