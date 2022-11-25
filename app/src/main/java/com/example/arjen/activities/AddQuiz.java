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
    public void registerListeners() {
        newQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddQuiz.class);
            MenuActivity.id = null;
            MenuActivity.quizId = null;
            startActivity(intent);
        });
        addQuiz.setOnClickListener(v -> {
            if (quiz != null) {
                quiz.update(title.getText().toString(), subject.getText().toString());
            } else {
                Database.Quizes.add(title.getText().toString(), subject.getText().toString());
            }
            onBackPressed();
        });
        resetQuiz.setOnClickListener(v -> onBackPressed());
        addQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddQuestion.class);
            MenuActivity.id = null;
            if (quiz != null) {
                MenuActivity.quizId = quiz.id;
            }
            startActivity(intent);
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
                Intent intent = new Intent(getApplicationContext(), PlayQuiz.class);
                if (quiz != null) {
                    MenuActivity.id = quiz.id;
                    MenuActivity.quizId = quiz.id;
                }
                startActivity(intent);
            }
        });
        deleteQuiz.setOnClickListener(v -> {
            if (quiz != null) {
                quiz.delete();
            }
            onBackPressed();
        });
        playTitle.setOnClickListener(v -> myTTS.speak(title.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        playSubject.setOnClickListener(v -> myTTS.speak(subject.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        quizList.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), QuizList.class);
            MenuActivity.id = null;
            MenuActivity.quizId = null;
            startActivity(intent);
        });
    }

    @Override
    public void fillData() {
        if (MenuActivity.id != null) {
            quiz = Database.Quizes.findId(id);
            Database.Questions.getForQuiz(id, this);
            playQuiz.setVisibility(View.VISIBLE);
            deleteQuiz.setVisibility(View.VISIBLE);
            addQuestion.setVisibility(View.VISIBLE);
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
        Intent intent = new Intent(getApplicationContext(), PlayQuestion.class);
        MenuActivity.id = id;
        if (quiz != null) {
            quizId = quiz.id;
        }
        startActivity(intent);
    }

    public void editQuestion(String id) {
        Intent intent = new Intent(getApplicationContext(), AddQuestion.class);
        MenuActivity.id = id;
        if (quiz != null) {
            quizId = quiz.id;
        }
        startActivity(intent);
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
        textToSpeak.add(getResources().getString(R.string.subject) + " " + getResources().getString(R.string.is) + " " + subject.getText().toString());
        for (int i = 0; i < Database.Questions.questionList.size(); i++) {
            textToSpeak.add((i + 1) + ". " + getResources().getString(R.string.question_substring) + " " + getResources().getString(R.string.is) + " " + Database.Questions.questionList.get(i).questionText) ;
        }
        readyToPlay = true;
        currentSentence = 0;
        numClick = 0;
    }
}