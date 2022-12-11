package com.example.arjen.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.arjen.utility.Database;
import com.example.arjen.R;
import com.example.arjen.adapters.CustomAdapterQuizList;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.interfaces.ShowListInterface;

import java.util.Objects;

public class QuizList extends MenuActivity implements ShowListInterface {
    private ImageButton addQuiz, startSearch;
    private RecyclerView quizesRecyclerView;
    private EditText searchQuiz;
    private CheckBox checkBoxTitle, checkBoxSubject;
    private TextView noResults;

    @Override
    public int getLayout() {
        return R.layout.activity_quiz_list;
    }

    @Override
    public void findViews() {
        addQuiz = findViewById(R.id.addQuiz);
        checkBoxTitle = findViewById(R.id.checkBoxTitle);
        checkBoxSubject = findViewById(R.id.checkBoxSubject);
        startSearch = findViewById(R.id.startSearch);
        searchQuiz = findViewById(R.id.searchQuiz);
        quizesRecyclerView = findViewById(R.id.quizesRecyclerView);
        noResults = findViewById(R.id.noResults);
    }

    @Override
    public void registerListeners() {
        checkBoxTitle.setOnCheckedChangeListener((v1, v2) -> checkBoxSubject.setChecked(!checkBoxTitle.isChecked()));
        checkBoxSubject.setOnCheckedChangeListener((v1, v2) -> checkBoxTitle.setChecked(!checkBoxSubject.isChecked()));
        startSearch.setOnClickListener(v -> {
            if (checkBoxTitle.isChecked()) {
                Database.Quizes.getWithTitle(searchQuiz.getText().toString(), this);
            } else {
                Database.Quizes.getWithSubject(searchQuiz.getText().toString(), this);
            }
        });
        addQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddQuiz.class);
            id = null;
            quizId = null;
            startActivity(intent);
        });
    }

    @Override
    public void fillData() {
        quizesRecyclerView.setVisibility(View.GONE);
        noResults.setVisibility(View.VISIBLE);
        Database.Quizes.get(this);
        quizesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void showList() {
        CustomAdapterQuizList customAdapterQuizList = new CustomAdapterQuizList(this);
        quizesRecyclerView.setAdapter(customAdapterQuizList);
        if (Objects.requireNonNull(quizesRecyclerView.getAdapter()).getItemCount() == 0) {
            quizesRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            quizesRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
        textToSpeak.clear();
        for (Database.Quizes.Quiz quiz: Database.Quizes.quizList) {
            textToSpeak.add(getResources().getString(R.string.quiz_title) + " " + getResources().getString(R.string.is) + " " + quiz.title + "."
                    + getResources().getString(R.string.subject) + " " + getResources().getString(R.string.is) + " " + quiz.subject + ".");
        }
        setupTTS();
    }

    public void playQuiz(String id) {
        Intent intent = new Intent(getApplicationContext(), PlayQuiz.class);
        MenuActivity.id = id;
        quizId = null;
        startActivity(intent);
    }

    public void editQuiz(String id) {
        Intent intent = new Intent(getApplicationContext(), AddQuiz.class);
        MenuActivity.id = id;
        quizId = null;
        startActivity(intent);
    }

    public void deleteQuiz(int position) {
        Objects.requireNonNull(quizesRecyclerView.getAdapter()).notifyItemRemoved(position);
        quizesRecyclerView.getAdapter().notifyItemRangeChanged(position, Database.Quizes.quizList.size() - position);
        setupTTS();
    }

    @Override
    public void chooseOption() {
        playQuiz(Database.Quizes.quizList.get(currentSentence).id);
    }

    @Override
    public void setupTTS() {
        textToSpeak.clear();
        for (Database.Quizes.Quiz quiz: Database.Quizes.quizList) {
            textToSpeak.add(getResources().getString(R.string.quiz_title) + " " + getResources().getString(R.string.is) + " " + quiz.title + "." + getResources().getString(R.string.subject) + " " + getResources().getString(R.string.is) + " " + quiz.subject + ".");
        }
        readyToPlay = true;
        currentSentence = 0;
        numClick = 0;
    }

    @Override
    public void setRecyclerView() {
        recyclerView = quizesRecyclerView;
    }
}