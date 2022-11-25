package com.example.arjen.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.arjen.utility.Database;
import com.example.arjen.R;
import com.example.arjen.adapters.CustomAdapterStoryList;
import com.example.arjen.utility.MenuActivity;
import com.example.arjen.utility.interfaces.ShowListInterface;

import java.util.Objects;

public class StoryList extends MenuActivity implements ShowListInterface {
    private ImageButton addStory, startSearch;
    private RecyclerView storiesRecyclerView;
    private EditText searchStory;
    private TextView noResults;

    @Override
    public int getLayout() {
        return R.layout.activity_story_list;
    }

    @Override
    public void findViews() {
        addStory = findViewById(R.id.addStory);
        storiesRecyclerView = findViewById(R.id.storiesRecyclerView);
        startSearch = findViewById(R.id.startSearch);
        searchStory = findViewById(R.id.searchStory);
        noResults = findViewById(R.id.noResults);
    }

    @Override
    public void setRecyclerView() {
        recyclerView = storiesRecyclerView;
    }

    @Override
    public void registerListeners() {
        addStory.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AddStory.class);
            id = null;
            quizId = null;
            startActivity(intent);
        });
        startSearch.setOnClickListener(v -> Database.Stories.getWithTitle(searchStory.getText().toString(), this));
    }

    @Override
    public void fillData() {
        Database.Stories.get(this);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void showList() {
        CustomAdapterStoryList customAdapterStoryList = new CustomAdapterStoryList(this);
        storiesRecyclerView.setAdapter(customAdapterStoryList);
        if (Objects.requireNonNull(storiesRecyclerView.getAdapter()).getItemCount() == 0) {
            storiesRecyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {
            storiesRecyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
        setupTTS();
    }

    public void playStory(String id) {
        Intent intent = new Intent(getApplicationContext(), PlayStory.class);
        MenuActivity.id = id;
        quizId = null;
        startActivity(intent);
    }

    public void editStory(String id) {
        Intent intent = new Intent(getApplicationContext(), AddStory.class);
        MenuActivity.id = id;
        quizId = null;
        startActivity(intent);
    }

    public void deleteStory(int position) {
        Objects.requireNonNull(storiesRecyclerView.getAdapter()).notifyItemRemoved(position);
        storiesRecyclerView.getAdapter().notifyItemRangeChanged(position, Database.Stories.storyList.size() - position);
        setupTTS();
    }

    @Override
    public void chooseOption() {
        playStory(Database.Stories.storyList.get(currentSentence).id);
    }

    @Override
    public void setupTTS() {
        textToSpeak.clear();
        for (Database.Stories.Story story: Database.Stories.storyList) {
            textToSpeak.add(getResources().getString(R.string.story_title) + " " + getResources().getString(R.string.is) + " " + story.title);
        }
        readyToPlay = true;
        currentSentence = 0;
        numClick = 0;
    }
}