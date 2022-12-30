package com.example.arjen.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AddQuestion extends MenuActivity {
    private ImageButton questionList, playTitle, playSubject, addQuestion, resetQuestion, navigate, playQuestion, deleteQuestion, playQuestionText, playOption, addOption, updateOption, resetOption, newQuestion;
    private LinearLayout quizData, questionData, optionData;
    private EditText questionText, optionText;
    private TextView title, subject, modeText, noResults;
    private List<String> options = new ArrayList<>();
    private CustomAdapterQuizQuestionOption customAdapterQuizQuestionOption;
    private RecyclerView optionRecyclerView;
    private Database.Questions.Question question;
    private int option_to_edit = -1, page = 1, answer = -1;

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
        questionList = findViewById(R.id.questionList);
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

    private DialogInterface.OnClickListener editDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    actuallyEditOption(somePosition);

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    public void actuallyEditOption(int position) {
        optionText.setText(options.get(position));
        option_to_edit = position;
        addOption.setVisibility(View.GONE);
        resetOption.setVisibility(View.VISIBLE);
        updateOption.setVisibility(View.VISIBLE);
        modeText.setText(getString(R.string.editing) + " " + (position + 1) + ". " + getString(R.string.option_substring_editing));
        setupTTS();
    }

    public void editOption(int position) {
        somePosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getApplicationContext().getResources().getString(R.string.confirm_change))
                .setPositiveButton(this.getApplicationContext().getResources().getString(R.string.yes), editDialogClickListener)
                .setNegativeButton(this.getApplicationContext().getResources().getString(R.string.no), editDialogClickListener).show();
    }

    private int somePosition;

    private DialogInterface.OnClickListener removeDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    actuallyRemoveOption(somePosition);

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    public void actuallyRemoveOption(int position) {
        if (options.size() != 0) {
            options.remove(position);
            if (answer == position) {
                answer = -1;
            }
            customAdapterQuizQuestionOption.notifyItemRemoved(position);
            customAdapterQuizQuestionOption.notifyItemRangeChanged(position, options.size() - position);
            if (Objects.requireNonNull(optionRecyclerView.getAdapter()).getItemCount() == 0) {
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
            textToSpeak.add(getResources().getString(R.string.option) + " " + getResources().getString(R.string.is) + " " + options.get(i)) ;
        }
        setupTTS();
    }

    public void removeOption(int position) {
        somePosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getApplicationContext().getResources().getString(R.string.option_delete))
                .setPositiveButton(this.getApplicationContext().getResources().getString(R.string.yes), removeDialogClickListener)
                .setNegativeButton(this.getApplicationContext().getResources().getString(R.string.no), removeDialogClickListener).show();
    }

    private DialogInterface.OnClickListener insertDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    actuallyInsertOption();

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    public void actuallyInsertOption() {
        options.add(optionText.getText().toString());
        customAdapterQuizQuestionOption.notifyItemInserted(options.size() - 1);
        optionRecyclerView.scrollToPosition(options.size() - 1);
        if (Objects.requireNonNull(optionRecyclerView.getAdapter()).getItemCount() == 0) {
            optionRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            optionRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
        textToSpeak.clear();
        textToSpeak.add(getResources().getString(R.string.question) + " " + getResources().getString(R.string.is) + " " +  questionText.getText().toString());
        for (int i = 0; i < options.size(); i++) {
            textToSpeak.add(getResources().getString(R.string.option) + " " + getResources().getString(R.string.is) + " " + options.get(i)) ;
        }
        setupTTS();
    }

    public void insertOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getApplicationContext().getResources().getString(R.string.confirm_change))
                .setPositiveButton(this.getApplicationContext().getResources().getString(R.string.yes), insertDialogClickListener)
                .setNegativeButton(this.getApplicationContext().getResources().getString(R.string.no), insertDialogClickListener).show();
    }

    @Override
    public void chooseOption() {
        if (currentSentence > 2) {
            if (currentSentence - 3 == answer) {
                if (myTTS.isPaused) {
                    Toast.makeText(this, getResources().getString(R.string.correct), Toast.LENGTH_SHORT).show();
                } else {
                    myTTS.speak(getResources().getString(R.string.correct), TextToSpeech.QUEUE_FLUSH);
                }
                musicDing.stop();
                musicDing = MediaPlayer.create(this, R.raw.success);
                musicDing.start();
            } else {
                if (myTTS.isPaused) {
                    Toast.makeText(this, getResources().getString(R.string.incorrect), Toast.LENGTH_SHORT).show();
                } else {
                    myTTS.speak(getResources().getString(R.string.incorrect), TextToSpeech.QUEUE_FLUSH);
                }
                musicDing.stop();
                musicDing = MediaPlayer.create(this, R.raw.failure);
                musicDing.start();
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
    public void applyChanges() {
        if (options.size() < 2) {
            if (myTTS.isPaused) {
                Toast.makeText(this, getResources().getString(R.string.no_options), Toast.LENGTH_SHORT).show();
            } else {
                myTTS.speak(getResources().getString(R.string.no_options), TextToSpeech.QUEUE_FLUSH);
            }
            return;
        } else {
            if (answer == -1) {
                if (myTTS.isPaused) {
                    Toast.makeText(this, getResources().getString(R.string.no_answer), Toast.LENGTH_SHORT).show();
                } else {
                    myTTS.speak(getResources().getString(R.string.no_answer), TextToSpeech.QUEUE_FLUSH);
                }
                return;
            }
        }
        if (question != null) {
            question.update(questionText.getText().toString(), quizId, answer, options);
        } else {
            Database.Questions.add(questionText.getText().toString(), quizId, answer, options);
        }
        instantBackPressed();
    }

    @Override
    public void registerListeners() {
        addQuestion.setOnClickListener(v -> {
            if (question != null) {
                confirmBackPressed();
            }
        });
        resetQuestion.setOnClickListener(v -> {
            if (question != null) {
                onBackPressed();
            }
        });
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
        playQuestion.setOnClickListener(v -> {
            if (question != null) {
                otherActivityBackPressed(PlayQuestion.class, question.quizId, question.id);
            }
        });
        deleteQuestion.setOnClickListener(v -> {
            if (question != null) {
                question.startDelete(this);
            }
        });
        playQuestionText.setOnClickListener(v -> myTTS.speak(questionText.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        playOption.setOnClickListener(v -> myTTS.speak(optionText.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        playTitle.setOnClickListener(v -> myTTS.speak(title.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        playSubject.setOnClickListener(v -> myTTS.speak(subject.getText().toString(), TextToSpeech.QUEUE_FLUSH));
        addOption.setOnClickListener(v -> insertOption());
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
            if (question != null) {
                otherActivityBackPressed(AddQuestion.class, question.quizId, null);
            }
        });
        questionList.setOnClickListener(v -> {
            if (question != null) {
                otherActivityBackPressed(PlayQuiz.class, question.quizId, question.quizId);
            }
        });
    }

    @Override
    public void fillData() {
        question = null;
        if (quizId == null) {
            Intent intent = new Intent(getApplicationContext(), QuizList.class);
            id = null;
            quizId = null;
            finish();
            startActivity(intent);
        } else {
            Database.Quizes.Quiz quiz = Database.Quizes.findId(quizId);
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
                    id = question.quizId;
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
        if (Objects.requireNonNull(optionRecyclerView.getAdapter()).getItemCount() == 0) {
            optionRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            optionRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
    }

    @Override
    public void setupTTS() {
        textToSpeak.clear();
        textToSpeak.add(getResources().getString(R.string.question) + " " + getResources().getString(R.string.is) + " " +  questionText.getText().toString());
        textToSpeak.add(getResources().getString(R.string.quiz_title) + " " + getResources().getString(R.string.is) + " " + title.getText().toString() + ".");
        textToSpeak.add(getResources().getString(R.string.subject) + " " + getResources().getString(R.string.is) + " " + subject.getText().toString());
        for (int i = 0; i < options.size(); i++) {
            textToSpeak.add(getResources().getString(R.string.option) + " " + getResources().getString(R.string.is) + " " + options.get(i) + ".") ;
        }
        readyToPlay = true;
        currentSentence = 0;
        numClick = 0;
    }
}