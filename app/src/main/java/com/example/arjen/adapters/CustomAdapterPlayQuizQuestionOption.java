package com.example.arjen.adapters;

import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arjen.R;
import com.example.arjen.activities.PlayQuestion;
import com.example.arjen.utility.myTTS;

import java.util.List;

public class CustomAdapterPlayQuizQuestionOption extends RecyclerView.Adapter<CustomAdapterPlayQuizQuestionOption.ViewHolder> {

    private final List<String> options;
    private final PlayQuestion playQuestion;

    public CustomAdapterPlayQuizQuestionOption(List<String> options, PlayQuestion playQuestion) {
        this.options = options;
        this.playQuestion = playQuestion;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CustomAdapterPlayQuizQuestionOption.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.play_quiz_question_option_layout, viewGroup, false);

        return new CustomAdapterPlayQuizQuestionOption.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomAdapterPlayQuizQuestionOption.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        TextView optionNumber, optionText;
        ImageButton playOptionText;

        optionNumber = viewHolder.getOptionNumber();
        optionText = viewHolder.getOptionText();
        playOptionText = viewHolder.getPlayOptionText();

        optionNumber.setText((position + 1) + ". " + playQuestion.getString(R.string.option_substring));
        optionText.setText(options.get(position));
        if (playQuestion.getAnswer() == position) {
            optionText.setTypeface(null, Typeface.BOLD);
        } else {
            optionText.setTypeface(null, Typeface.NORMAL);
        }
        playOptionText.setOnClickListener(v -> myTTS.speak(options.get(position), TextToSpeech.QUEUE_FLUSH));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return options.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView optionNumber, optionText;
        private final ImageButton playOptionText;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            optionNumber = view.findViewById(R.id.optionNumber);
            optionText = view.findViewById(R.id.optionText);
            playOptionText = view.findViewById(R.id.playOptionText);
        }

        public TextView getOptionNumber() {
            return optionNumber;
        }

        public TextView getOptionText() {
            return optionText;
        }

        public ImageButton getPlayOptionText() {
            return playOptionText;
        }
    }
}
