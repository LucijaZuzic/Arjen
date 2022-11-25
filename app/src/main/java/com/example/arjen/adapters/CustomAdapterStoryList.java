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
import com.example.arjen.R;
import com.example.arjen.activities.StoryList;
import com.example.arjen.utility.myTTS;

public class CustomAdapterStoryList extends RecyclerView.Adapter<CustomAdapterStoryList.ViewHolder> {

    private final StoryList storyList;

    public CustomAdapterStoryList(StoryList storyList) {
        this.storyList = storyList;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CustomAdapterStoryList.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.story_layout, viewGroup, false);

        return new CustomAdapterStoryList.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomAdapterStoryList.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        TextView storyTitle;
        ImageButton playTitle, playStory, editStory, deleteStory;

        storyTitle = viewHolder.getStoryTitle();
        playTitle = viewHolder.getPlayTitle();
        playStory = viewHolder.getPlayStory();
        editStory = viewHolder.getEditStory();
        deleteStory = viewHolder.getDeleteStory();

        Database.Stories.Story story = Database.Stories.storyList.get(position);

        storyTitle.setText(story.title);
        playTitle.setOnClickListener(v -> myTTS.speak(story.title, TextToSpeech.QUEUE_FLUSH));
        playStory.setOnClickListener(v -> storyList.playStory(story.id));
        editStory.setOnClickListener(v -> storyList.editStory(story.id));
        deleteStory.setOnClickListener(v -> {
            story.delete();
            storyList.deleteStory(position);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Database.Stories.storyList.size();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView storyTitle;
        private final ImageButton playTitle, playStory, editStory, deleteStory;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            storyTitle = view.findViewById(R.id.storyTitle);
            playTitle = view.findViewById(R.id.playTitle);
            playStory = view.findViewById(R.id.playStory);
            editStory = view.findViewById(R.id.editStory);
            deleteStory = view.findViewById(R.id.deleteStory);
        }

        public TextView getStoryTitle() {
            return storyTitle;
        }

        public ImageButton getPlayTitle() {
            return playTitle;
        }

        public ImageButton getPlayStory() {
            return playStory;
        }

        public ImageButton getEditStory() {
            return editStory;
        }

        public ImageButton getDeleteStory() {
            return deleteStory;
        }
    }
}
