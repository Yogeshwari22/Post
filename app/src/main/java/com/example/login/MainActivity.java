package com.example.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements PopupMenu.OnMenuItemClickListener{


    private View progressBar, progressBarLayout, progressBarLabel, fromLayout;//progress bar
    private RecyclerView cardsRV;
    private View createPostBTN;
    private CardsRecyclerView adapter;
    private ImageView reloadBTN, profileBTN, optionsBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardsRV = findViewById(R.id.cardsRecyclerView);
        createPostBTN = findViewById(R.id.createPostButton);
        reloadBTN = findViewById(R.id.reloadButton);
        profileBTN = findViewById(R.id.profileButton);
        optionsBTN = findViewById(R.id.optionsBTN);
        adapter = new CardsRecyclerView();

        //progress bar
        progressBar = findViewById(R.id.ma_progressBar);
        progressBarLayout = findViewById(R.id.ma_progressLayout);
        progressBarLabel = findViewById(R.id.ma_progressLabel);
        fromLayout = findViewById(R.id.mainActivityLayout);

        showProgress(true);
        reloadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();
            }
        });

        profileBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Profile.class));
            }
        });

        Backendless.Data.of(Post.class).find(new AsyncCallback<List<Post>>() {
            @Override
            public void handleResponse(List<Post> response) {
                adapter.setDataList((ArrayList<Post>) response);
                showProgress(false);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                showProgress(false);
                Toast.makeText(MainActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        cardsRV.setAdapter(adapter);
        cardsRV.setLayoutManager(new LinearLayoutManager(this));

        createPostBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreatePost.class));
            }
        });


        optionsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.inflate(R.menu.main_activity_menu);
                popup.setOnMenuItemClickListener(MainActivity.this);
                popup.show();
            }
        });

    }

    private void reload() {
        showProgress(true);
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setSortBy("created");
        Backendless.Data.of(Post.class).find(queryBuilder, new AsyncCallback<List<Post>>() {
            @Override
            public void handleResponse(List<Post> response) {
                adapter.setDataList((ArrayList<Post>) response);
                showProgress(false);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                showProgress(false);
                Toast.makeText(MainActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //progress bar
    public void showProgress(boolean show) {
        fromLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        progressBarLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBarLabel.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutOP :
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void response) {
                        Toast.makeText(MainActivity.this, "logOut", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(MainActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                MainActivity.this.finish();
                Intent intent = new Intent(MainActivity.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();
                break;

            case R.id.readLaterOP:
                startActivity(new Intent(MainActivity.this, ReadLater.class));
        }
        return false;
    }
}

