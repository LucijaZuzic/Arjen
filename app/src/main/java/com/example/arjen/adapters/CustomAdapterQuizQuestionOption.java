package com.example.arjen.adapters;

import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.arjen.activities.AddQuestion;
import com.example.arjen.R;
import com.example.arjen.utility.myTTS;

import java.util.List;

public class CustomAdapterQuizQuestionOption extends RecyclerView.Adapter<CustomAdapterQuizQuestionOption.ViewHolder> {

    private final List<String> options;
    private final AddQuestion addQuestion;

    public CustomAdapterQuizQuestionOption(List<String> options, AddQuestion addQuestion) {
        this.options = options;
        this.addQuestion = addQuestion;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.quiz_question_option_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        TextView optionNumber;
        TextView option;
        ImageButton playOption, markAsAnswer, editOption, deleteOption, moveUp, moveDown;

        optionNumber = viewHolder.getOptionNumber();
        option = viewHolder.getOption();
        playOption = viewHolder.getPlayOption();
        markAsAnswer = viewHolder.getMarkAsAnswer();
        editOption = viewHolder.getEditOptionn();
        deleteOption = viewHolder.getDeleteOption();
        moveUp = viewHolder.getMoveUp();
        moveDown = viewHolder.getMoveDown();
        optionNumber.setText(Integer.toString(position + 1) + ". " + addQuestion.getString(R.string.option_substring));
        option.setText(options.get(position));
        playOption.setOnClickListener(v -> {
            myTTS.speak(option.getText().toString(), TextToSpeech.QUEUE_FLUSH);
        });
        if (addQuestion.getAnswer() == position) {
            markAsAnswer.setImageDrawable(addQuestion.getDrawable(R.drawable.ic_baseline_check_24));
            option.setTypeface(null, Typeface.BOLD);
        } else {
            markAsAnswer.setImageDrawable(addQuestion.getDrawable(R.drawable.ic_baseline_close_24));
            option.setTypeface(null, Typeface.NORMAL);
        }
        markAsAnswer.setOnClickListener(v -> {
            addQuestion.markAsAnswer(position);
            markAsAnswer.setImageDrawable(addQuestion.getDrawable(R.drawable.ic_baseline_check_24));
            option.setTypeface(null, Typeface.BOLD);
        });
        editOption.setOnClickListener(v -> {
            addQuestion.editOption(position);
        });
        deleteOption.setOnClickListener(v -> {
            addQuestion.removeOption(position);
        });
        moveDown.setEnabled(position != options.size() - 1);
        moveDown.setOnClickListener(v -> {
            if (position != options.size() - 1) {
                addQuestion.swapOptions(position, position + 1);
            }
        });
        moveUp.setEnabled(position != 0);
        moveUp.setOnClickListener(v -> {
            if (position != 0) {
                addQuestion.swapOptions(position, position - 1);
            }
        });
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
        private final TextView optionNumber, option;
        private final ImageButton playOption, markAsAnswer, editOption, deleteOption, moveUp, moveDown;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            optionNumber = view.findViewById(R.id.optionNumber);
            option = view.findViewById(R.id.option);
            playOption = view.findViewById(R.id.playOption);
            markAsAnswer = view.findViewById(R.id.markAsAnswer);
            editOption = view.findViewById(R.id.editOption);
            deleteOption = view.findViewById(R.id.deleteOption);
            moveUp = view.findViewById(R.id.moveUp);
            moveDown = view.findViewById(R.id.moveDown);
        }

        public TextView getOptionNumber() {
            return optionNumber;
        }

        public TextView getOption() {
            return option;
        }

        public ImageButton getPlayOption() {
            return playOption;
        }

        public ImageButton getMarkAsAnswer() {
            return markAsAnswer;
        }

        public ImageButton getEditOptionn() {
            return editOption;
        }

        public ImageButton getDeleteOption() {
            return deleteOption;
        }

        public ImageButton getMoveUp() {
            return moveUp;
        }

        public ImageButton getMoveDown() {
            return moveDown;
        }
    }
}