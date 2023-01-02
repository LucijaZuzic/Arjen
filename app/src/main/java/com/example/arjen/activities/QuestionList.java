package com.example.arjen.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arjen.R;
import com.example.arjen.adapters.CustomAdapterQuizQuestionList;
import com.example.arjen.utility.Database;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.interfaces.QuizInterface;
import com.example.arjen.utility.interfaces.ShowListInterface;
import com.example.arjen.utility.interfaces.ShowListSecondPartInterface;

import java.util.Objects;

public class QuestionList extends MenuActivity implements ShowListInterface, ShowListSecondPartInterface, QuizInterface {
    private ImageButton addQuiz, startSearch;
    private RecyclerView questionsRecyclerView;
    private EditText searchQuestion;
    private CheckBox checkBoxTitle, checkBoxSubject, checkBoxQuestion;
    private TextView noResults;

    @Override
    public int getLayout() {
        return R.layout.activity_question_list;
    }

    @Override
    public void findViews() {
        addQuiz = findViewById(R.id.addQuiz);
        checkBoxTitle = findViewById(R.id.checkBoxTitle);
        checkBoxSubject = findViewById(R.id.checkBoxSubject);
        checkBoxQuestion = findViewById(R.id.checkBoxQuestion);
        startSearch = findViewById(R.id.startSearch);
        searchQuestion = findViewById(R.id.searchQuestion);
        questionsRecyclerView = findViewById(R.id.questionsRecyclerView);
        noResults = findViewById(R.id.noResults);
    }

    @Override
    public void registerListeners() {
        checkBoxTitle.setOnCheckedChangeListener((v1, v2) -> {
            if (checkBoxTitle.isChecked()) {
                checkBoxSubject.setChecked(false);
                checkBoxQuestion.setChecked(false);
            } else {
                if (!checkBoxTitle.isChecked() && !checkBoxSubject.isChecked() && !checkBoxQuestion.isChecked()) {
                    checkBoxTitle.setChecked(true);
                }
            }
        });
        checkBoxSubject.setOnCheckedChangeListener((v1, v2) -> {
            if (checkBoxSubject.isChecked()) {
                checkBoxTitle.setChecked(false);
                checkBoxQuestion.setChecked(false);
            } else {
                if (!checkBoxTitle.isChecked() && !checkBoxSubject.isChecked() && !checkBoxQuestion.isChecked()) {
                    checkBoxTitle.setChecked(true);
                }
            }
        });
        checkBoxQuestion.setOnCheckedChangeListener((v1, v2) -> {
            if (checkBoxQuestion.isChecked()) {
                checkBoxTitle.setChecked(false);
                checkBoxSubject.setChecked(false);
            } else {
                if (!checkBoxTitle.isChecked() && !checkBoxSubject.isChecked() && !checkBoxQuestion.isChecked()) {
                    checkBoxTitle.setChecked(true);
                }
            }
        });
        startSearch.setOnClickListener(v -> {
            if (checkBoxTitle.isChecked()) {
                Database.Quizes.getWithTitle(searchQuestion.getText().toString(), this); 
            } else {
                if (checkBoxSubject.isChecked()) {
                    Database.Quizes.getWithSubject(searchQuestion.getText().toString(), this); 
                } else {
                    Database.Questions.getWithText(searchQuestion.getText().toString(), this);
                }
            }
        });
        addQuiz.setOnClickListener(v -> {
            startWithNewId(AddQuiz.class, null, null);
        });
    }

    @Override
    public void fillData() {
        questionsRecyclerView.setVisibility(View.GONE);
        noResults.setVisibility(View.VISIBLE);
        Database.Quizes.get(this);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void chooseOption() {
        playQuestion(Database.Questions.questionList.get(currentSentence).id);
    }

    @Override
    public void setupTTS() {
        textToSpeak.clear();
        int index = 1;
        for (Database.Questions.Question question: Database.Questions.questionList) {
            Database.Quizes.Quiz parent = null;
            for (Database.Quizes.Quiz quiz: Database.Quizes.quizList) {
                if (Objects.equals(quiz.id, question.quizId)) {
                    parent = quiz;
                    break;
                }
            }
            if (parent != null) {
                textToSpeak.add(getResources().getString(R.string.question) + " " + getResources().getString(R.string.is) + " " + question.questionText + ". " + getResources().getString(R.string.quiz_title) + " " + getResources().getString(R.string.is) + " " + parent.title + ". " + getResources().getString(R.string.subject) + " " + getResources().getString(R.string.is) + " " + parent.subject + "." );
            } else {
                textToSpeak.add(getResources().getString(R.string.question) + " " + getResources().getString(R.string.is) + " " + question.questionText + ". " + getResources().getString(R.string.quiz_title) + " " + getResources().getString(R.string.is) + getResources().getString(R.string.unknown) + ". " + getResources().getString(R.string.subject) + " " + getResources().getString(R.string.is) + getResources().getString(R.string.unknown) + ".");
            }
            index += 1;
        }
        readyToPlay = true;
        currentSentence = 0;
        numClick = 0;
    }

    @Override
    public void setRecyclerView() {
        recyclerView = questionsRecyclerView;
    }

    @Override
    public void showList() {
        if (checkBoxTitle.isChecked() || checkBoxSubject.isChecked()) {
            Database.Questions.getForQuizes(this);
        } else {
            showListSecondPart();
        }
    }

    @Override
    public void showListSecondPart() {
        CustomAdapterQuizQuestionList customAdapterQuizQuestionList = new CustomAdapterQuizQuestionList(this);
        questionsRecyclerView.setAdapter(customAdapterQuizQuestionList);
        if (Objects.requireNonNull(questionsRecyclerView.getAdapter()).getItemCount() == 0) {
            questionsRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            questionsRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
        setupTTS();
    }

    @Override
    public void playQuestion(String id) {
        for (Database.Questions.Question question: Database.Questions.questionList) {
            if (Objects.equals(question.id, id)) {
                startWithNewId(PlayQuestion.class, question.id, question.quizId);
                return;
            }
        }
    }

    @Override
    public void editQuestion(String id) {
        for (Database.Questions.Question question: Database.Questions.questionList) {
            if (Objects.equals(question.id, id)) {
                startWithNewId(AddQuestion.class, question.id, question.quizId);
                return;
            }
        }
    }

    @Override
    public void deleteQuestion(int position) {
        Objects.requireNonNull(questionsRecyclerView.getAdapter()).notifyItemRemoved(position);
        questionsRecyclerView.getAdapter().notifyItemRangeChanged(position, Database.Questions.questionList.size() - position);
        if (questionsRecyclerView.getAdapter().getItemCount() == 0) {
            questionsRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            questionsRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
        setupTTS();
    }
}