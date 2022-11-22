package com.example.arjen.adapters;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.arjen.activities.AddStory;
import com.example.arjen.R;
import com.example.arjen.utility.myTTS;

import java.util.List;

public class CustomAdapterStoryQuestion extends RecyclerView.Adapter<CustomAdapterStoryQuestion.ViewHolder> {

    private final List<String> questions;
    private final AddStory addStory;

    public CustomAdapterStoryQuestion(List<String> questions, AddStory addStory) {
        this.questions = questions;
        this.addStory = addStory;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.story_question_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        TextView questionNumber;
        TextView question;
        ImageButton playQuestion, editQuestion, deleteQuestion, moveUp, moveDown;

        questionNumber = viewHolder.getQuestionNumber();
        question = viewHolder.getQuestion();
        playQuestion = viewHolder.getPlayQuestion();
        editQuestion = viewHolder.getEditQuestion();
        deleteQuestion = viewHolder.getDeleteQuestion();
        moveUp = viewHolder.getMoveUp();
        moveDown = viewHolder.getMoveDown();
        questionNumber.setText(Integer.toString(position + 1) + ". " + addStory.getString(R.string.question_substring));
        question.setText(questions.get(position));
        playQuestion.setOnClickListener(v -> {
            myTTS.speak(question.getText().toString(), TextToSpeech.QUEUE_FLUSH);
        });
        editQuestion.setOnClickListener(v -> {
            addStory.editQuestion(position);
        });
        deleteQuestion.setOnClickListener(v -> {
            addStory.removeQuestion(position);
        });
        moveDown.setEnabled(position != questions.size() - 1);
        moveDown.setOnClickListener(v -> {
            if (position != questions.size() - 1) {
                addStory.swapQuestions(position, position + 1);
            }
        });
        moveUp.setEnabled(position != 0);
        moveUp.setOnClickListener(v -> {
            if (position != 0) {
                addStory.swapQuestions(position, position - 1);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return questions.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionNumber, question;
        private final ImageButton playQuestion, editQuestion, deleteQuestion, moveUp, moveDown;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            questionNumber = view.findViewById(R.id.questionNumber);
            question = view.findViewById(R.id.question);
            playQuestion = view.findViewById(R.id.playQuestion);
            editQuestion = view.findViewById(R.id.editQuestion);
            deleteQuestion = view.findViewById(R.id.deleteQuestion);
            moveUp = view.findViewById(R.id.moveUp);
            moveDown = view.findViewById(R.id.moveDown);
        }

        public TextView getQuestionNumber() {
            return questionNumber;
        }

        public TextView getQuestion() {
            return question;
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

        public ImageButton getMoveUp() {
            return moveUp;
        }

        public ImageButton getMoveDown() {
            return moveDown;
        }
    }
}