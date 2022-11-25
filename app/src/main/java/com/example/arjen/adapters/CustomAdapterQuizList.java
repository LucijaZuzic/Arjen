package com.example.arjen.adapters;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arjen.utility.Database;
import com.example.arjen.activities.QuizList;
import com.example.arjen.R;
import com.example.arjen.utility.myTTS;

public class CustomAdapterQuizList extends RecyclerView.Adapter<CustomAdapterQuizList.ViewHolder> {
    private final QuizList quizList;

    public CustomAdapterQuizList(QuizList quizList) {
        this.quizList = quizList;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CustomAdapterQuizList.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.quiz_layout, viewGroup, false);

        return new CustomAdapterQuizList.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomAdapterQuizList.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        TextView title, subject;
        ImageButton playQuizTitle, playQuiz, editQuiz, deleteQuiz;

        title = viewHolder.getTitle();
        subject = viewHolder.getSubject();
        playQuizTitle = viewHolder.getPlayQuizTitle();
        playQuiz = viewHolder.getPlayQuiz();
        editQuiz = viewHolder.getEditQuiz();
        deleteQuiz = viewHolder.getDeleteQuiz();

        Database.Quizes.Quiz quiz = Database.Quizes.quizList.get(position);

        title.setText(quiz.title);
        subject.setText(quiz.subject);
        playQuizTitle.setOnClickListener(v -> myTTS.speak(quiz.title + " " + quiz.subject, TextToSpeech.QUEUE_FLUSH));
        playQuiz.setOnClickListener(v -> quizList.playQuiz(quiz.id));
        editQuiz.setOnClickListener(v -> quizList.editQuiz(quiz.id));
        deleteQuiz.setOnClickListener(v -> {
            quiz.delete();
            quizList.deleteQuiz(position);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Database.Quizes.quizList.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, subject;
        private final ImageButton playQuizTitle, playQuiz, editQuiz, deleteQuiz;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            title = view.findViewById(R.id.title);
            subject = view.findViewById(R.id.subject);
            playQuizTitle = view.findViewById(R.id.playQuizTitle);
            playQuiz = view.findViewById(R.id.playQuiz);
            editQuiz = view.findViewById(R.id.editQuiz);
            deleteQuiz = view.findViewById(R.id.deleteQuiz);
        }

        public TextView getTitle() {
            return title;
        }

        public TextView getSubject() {
            return subject;
        }

        public ImageButton getPlayQuizTitle() {
            return playQuizTitle;
        }

        public ImageButton getPlayQuiz() {
            return playQuiz;
        }

        public ImageButton getEditQuiz() {
            return editQuiz;
        }

        public ImageButton getDeleteQuiz() {
            return deleteQuiz;
        }
    }
}
