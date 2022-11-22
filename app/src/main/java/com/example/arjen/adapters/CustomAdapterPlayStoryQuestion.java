package com.example.arjen.adapters;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arjen.R;
import com.example.arjen.activities.PlayStory;
import com.example.arjen.activities.QuizList;
import com.example.arjen.utility.Database;
import com.example.arjen.utility.myTTS;

import java.util.List;

public class CustomAdapterPlayStoryQuestion extends RecyclerView.Adapter<CustomAdapterPlayStoryQuestion.ViewHolder> {

    private final List<String> questions;
    private final PlayStory playStory;

    public CustomAdapterPlayStoryQuestion(List<String> questions, PlayStory playStory) {
        this.questions = questions;
        this.playStory = playStory;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CustomAdapterPlayStoryQuestion.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.play_story_question_layout, viewGroup, false);

        return new CustomAdapterPlayStoryQuestion.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomAdapterPlayStoryQuestion.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        TextView questionNumber, questionText;
        ImageButton playQuestionText;

        questionNumber = viewHolder.getQuestionNumber();
        questionText = viewHolder.getQuestionText();
        playQuestionText = viewHolder.getPlayQuestionText();

        questionNumber.setText(Integer.toString(position + 1) + ". " + playStory.getString(R.string.question_substring));
        questionText.setText(questions.get(position));
        playQuestionText.setOnClickListener(v -> {
            myTTS.speak(questions.get(position), TextToSpeech.QUEUE_FLUSH);
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
        private final TextView questionNumber, questionText;
        private final ImageButton playQuestionText;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            questionNumber = view.findViewById(R.id.questionNumber);
            questionText = view.findViewById(R.id.questionText);
            playQuestionText = view.findViewById(R.id.playQuestionText);
        }

        public TextView getQuestionNumber() {
            return questionNumber;
        }

        public TextView getQuestionText() {
            return questionText;
        }

        public ImageButton getPlayQuestionText() {
            return playQuestionText;
        }
    }
}
