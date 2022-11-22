package com.example.arjen.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
import com.example.arjen.adapters.CustomAdapterQuizQuestionOption;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.myTTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddQuestion extends MenuActivity {
    private ImageButton playTitle, playSubject, addQuestion, resetQuestion, navigate, playQuestion, deleteQuestion, playQuestionText, playOption, addOption, updateOption, resetOption, newQuestion;
    private LinearLayout quizData, questionData, optionData;
    private EditText questionText, optionText;
    private TextView title, subject, modeText, noResults;
    private List<String> options = new ArrayList<String>();
    private CustomAdapterQuizQuestionOption customAdapterQuizQuestionOption;
    private RecyclerView optionRecyclerView;
    private Database.Questions.Question question;
    private int option_to_edit = -1, page = 1, answer = -1;
    private Database.Quizes.Quiz quiz;

    @Override
    public int getLayout() {
        return R.layout.activity_add_question;
    }

    @Override
    public void findViews() {
        title = findViewById(R.id.title);
        subject = findViewById(R.id.subject);
        playTitle = findViewById(R.id.playTitle);
        playSubject = findViewById(R.id.playSubject);
        addQuestion = findViewById(R.id.addQuestion);
        resetQuestion = findViewById(R.id.resetQuestion);
        navigate = findViewById(R.id.navigate);
        playQuestion = findViewById(R.id.playQuestion);
        deleteQuestion = findViewById(R.id.deleteQuestion);
        playQuestionText = findViewById(R.id.playQuestionText);
        playOption = findViewById(R.id.playOption);
        addOption = findViewById(R.id.addOption);
        updateOption = findViewById(R.id.updateOption);
        resetOption = findViewById(R.id.resetOption);
        quizData = findViewById(R.id.quizData);
        questionData = findViewById(R.id.questionData);
        optionData = findViewById(R.id.optionData);
        questionText = findViewById(R.id.questionText);
        optionText = findViewById(R.id.optionText);
        modeText = findViewById(R.id.modeText);
        noResults = findViewById(R.id.noResults);
        optionRecyclerView = findViewById(R.id.optionRecyclerView);
        newQuestion = findViewById(R.id.newQuestion);
    }

    public void markAsAnswer(int position) {
        customAdapterQuizQuestionOption.notifyItemChanged(answer);
        answer = position;
        customAdapterQuizQuestionOption.notifyItemChanged(answer);
    }

    public int getAnswer() {
        return answer;
    }

    public void swapOptions(int one ,int two) {
        Collections.swap(options, one, two);
        if (answer == one) {
            answer = two;
        } else {
            if (answer == two) {
                answer = one;
            }
        }
        customAdapterQuizQuestionOption.notifyItemChanged(one);
        customAdapterQuizQuestionOption.notifyItemChanged(two);
        optionRecyclerView.scrollToPosition(two);
        setupTTS();
    }

    public void editOption(int position) {
        optionText.setText(options.get(position));
        option_to_edit = position;
        addOption.setVisibility(View.GONE);
        resetOption.setVisibility(View.VISIBLE);
        updateOption.setVisibility(View.VISIBLE);
        modeText.setText(getString(R.string.editing) + " " + (position + 1) + ". " + getString(R.string.option_substring));
        setupTTS();
    }

    public void removeOption(int position) {
        if (options.size() != 0) {
            options.remove(position);
            if (answer == position) {
                answer = -1;
            }
            customAdapterQuizQuestionOption.notifyItemRemoved(position);
            customAdapterQuizQuestionOption.notifyItemRangeChanged(position, options.size() - position);
            if (optionRecyclerView.getAdapter().getItemCount() == 0) {
                optionRecyclerView.setVisibility(View.GONE);
                noResults.setVisibility(View.VISIBLE);
            } else {
                optionRecyclerView.setVisibility(View.VISIBLE);
                noResults.setVisibility(View.GONE);
            }
        }
        textToSpeak.clear();
        textToSpeak.add(getResources().getString(R.string.question) + " " + getResources().getString(R.string.is) + " " +  questionText.getText().toString());
        for (int i = 0; i < options.size(); i++) {
            textToSpeak.add((i + 1) + ". " + getResources().getString(R.string.option_substring) + " " + getResources().getString(R.string.is) + " " + options.get(i)) ;
        }
        setupTTS();
    }

    public void insertOption() {
        options.add(optionText.getText().toString());
        customAdapterQuizQuestionOption.notifyItemInserted(options.size() - 1);
        optionRecyclerView.scrollToPosition(options.size() - 1);
        if (optionRecyclerView.getAdapter().getItemCount() == 0) {
            optionRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            optionRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
        textToSpeak.clear();
        textToSpeak.add(getResources().getString(R.string.question) + " " + getResources().getString(R.string.is) + " " +  questionText.getText().toString());
        for (int i = 0; i < options.size(); i++) {
            textToSpeak.add((i + 1) + ". " + getResources().getString(R.string.option_substring) + " " + getResources().getString(R.string.is) + " " + options.get(i)) ;
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
    public void setRecyclerView() {
        recyclerView = optionRecyclerView;
    }

    @Override
    public void registerListeners() {
        addQuestion.setOnClickListener(v -> {
            if (options.size() < 2) {
                Toast.makeText(this, getResources().getString(R.string.no_options), Toast.LENGTH_SHORT).show();
                myTTS.speak(getResources().getString(R.string.no_options), TextToSpeech.QUEUE_FLUSH);
                return;
            } else {
                if (answer == -1) {
                    Toast.makeText(this, getResources().getString(R.string.no_answer), Toast.LENGTH_SHORT).show();
                    myTTS.speak(getResources().getString(R.string.no_answer), TextToSpeech.QUEUE_FLUSH);
                    return;
                }
            }
            if (question != null) {
                question.update(questionText.getText().toString(), quizId, answer, options);
            } else {
                Database.Questions.add(questionText.getText().toString(), quizId, answer, options);
            }
            onBackPressed();
        });
        resetQuestion.setOnClickListener(v -> {
            onBackPressed();
        });
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
        playQuestion.setOnClickListener(v -> {
            if (question != null) {
                Intent intent = new Intent(getApplicationContext(), PlayQuestion.class);
                if (question != null) {
                    this.id = question.id;
                    quizId = question.quizId;
                }
                startActivity(intent);
            }
        });
        deleteQuestion.setOnClickListener(v -> {
            if (question != null) {
                question.delete();
            }
            onBackPressed();
        });
        playQuestionText.setOnClickListener(v -> {
            myTTS.speak(questionText.getText().toString(), TextToSpeech.QUEUE_FLUSH);
        });
        playOption.setOnClickListener(v -> {
            myTTS.speak(optionText.getText().toString(), TextToSpeech.QUEUE_FLUSH);
        });
        addOption.setOnClickListener(v -> {
            insertOption();
        });
        updateOption.setOnClickListener(v -> {
            options.set(option_to_edit, optionText.getText().toString());
            customAdapterQuizQuestionOption.notifyItemChanged(option_to_edit);
            optionRecyclerView.scrollToPosition(option_to_edit);
            option_to_edit = -1;
            addOption.setVisibility(View.VISIBLE);
            resetOption.setVisibility(View.GONE);
            updateOption.setVisibility(View.GONE);
            modeText.setText(getString(R.string.new_question));
        });
        resetOption.setOnClickListener(v -> {
            option_to_edit = -1;
            addOption.setVisibility(View.VISIBLE);
            resetOption.setVisibility(View.GONE);
            updateOption.setVisibility(View.GONE);
            modeText.setText(getString(R.string.new_option));
        });
        newQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddQuestion.class);
            id = null;
            if (question != null) {
                quizId = question.quizId;
            }
            startActivity(intent);
        });
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
                playQuestion.setVisibility(View.VISIBLE);
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
            }
        }
        optionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        customAdapterQuizQuestionOption = new CustomAdapterQuizQuestionOption(options, this);
        optionRecyclerView.setAdapter(customAdapterQuizQuestionOption);
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