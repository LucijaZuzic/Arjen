package com.example.arjen.adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arjen.R;
import com.example.arjen.activities.AddQuiz;
import com.example.arjen.activities.QuestionList;
import com.example.arjen.utility.Database;
import com.example.arjen.utility.interfaces.QuizInterface;
import com.example.arjen.utility.myTTS;

public class CustomAdapterQuizQuestionList extends RecyclerView.Adapter<CustomAdapterQuizQuestionList.ViewHolder> {

    private final QuizInterface addQuiz;

    public CustomAdapterQuizQuestionList(QuizInterface addQuiz) {
        this.addQuiz = addQuiz;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CustomAdapterQuizQuestionList.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.quiz_question_layout, viewGroup, false);

        return new CustomAdapterQuizQuestionList.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomAdapterQuizQuestionList.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        TextView questionText;
        ImageButton playQuestionText, playQuestion, editQuestion, deleteQuestion;

        questionText = viewHolder.getQuestionTitle();
        playQuestionText = viewHolder.getPlayQuestionText();
        playQuestion = viewHolder.getPlayQuestion();
        editQuestion = viewHolder.getEditQuestion();
        deleteQuestion = viewHolder.getDeleteQuestion();

        Database.Questions.Question question = Database.Questions.questionList.get(position);

        questionText.setText(question.questionText);
        playQuestionText.setOnClickListener(v -> myTTS.speak(question.questionText, TextToSpeech.QUEUE_FLUSH));
        if (addQuiz instanceof AddQuiz || addQuiz instanceof QuestionList) {
            deleteQuestion.setVisibility(View.VISIBLE);
            editQuestion.setVisibility(View.VISIBLE);
        }
        playQuestion.setOnClickListener(v -> addQuiz.playQuestion(question.id));
        editQuestion.setOnClickListener(v -> addQuiz.editQuestion(question.id));
        deleteQuestion.setOnClickListener(v -> {
            question.startDelete(addQuiz, position);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Database.Questions.questionList.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionText;
        private final ImageButton playQuestionText, playQuestion, editQuestion, deleteQuestion;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            questionText = view.findViewById(R.id.questionText);
            playQuestionText = view.findViewById(R.id.playQuestionText);
            playQuestion = view.findViewById(R.id.playQuestion);
            editQuestion = view.findViewById(R.id.editQuestion);
            deleteQuestion = view.findViewById(R.id.deleteQuestion);
        }

        public TextView getQuestionTitle() {
            return questionText;
        }

        public ImageButton getPlayQuestionText() {
            return playQuestionText;
        }

        public ImageButton getPlayQuestion() {
            return playQuestion;
        }

        public ImageButton getEditQuestion() {
            return editQuestion;
        }

        public ImageButton getDeleteQuestion() {
            return deleteQuestion;
        }
    }
}
