package com.codepath.apps.restclienttemplate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG = "TimelineActivity";
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    Button logout_button;
    FloatingActionButton fab;
    SwipeRefreshLayout swipeContainer;
    ImageView profileLogo;
    ImageView twitter_icon;
    private EndlessRecyclerViewScrollListener scrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.timeline_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Home");
        client = TwitterApp.getRestClient(this);

        logout_button = findViewById(R.id.logout_button);
        fab = findViewById(R.id.timeline_fab);
        profileLogo = findViewById(R.id.timeline_icon);
        rvTweets = findViewById(R.id.rvTweets);
        tweets= new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        rvTweets.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(llm);
        twitter_icon = findViewById(R.id.twitter_icon);
        final int[] state =  new int[1];

        Glide.with(this).load("https://www.nicepng.com/png/full/4-40303_see-here-new-2018-twitter-logo-black-and.png").into(twitter_icon);
        rvTweets.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
                state[0] = newState;

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0 && (state[0] == 0 || state[0] == 2) ) {
                    toolbar.setVisibility(View.GONE);
                } else if (dy < -10) {
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });



        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutButton();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                editActivityResultLauncher.launch(intent);
                //startActivity(intent);

//                Toast.makeText(TimelineActivity.this, "Compose", Toast.LENGTH_SHORT).show();
//                Log.i("TimelineActivity", "Compose");
            }
        });
        //fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                tweets.clear();
                populateHometimeline(null);

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        getUserProfile();
        populateHometimeline(null);

        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                //loadNextDataFromApi(page);
                Tweet lastDisplayedTweet = tweets.get(tweets.size() - 1);
                String maxId = lastDisplayedTweet.id;
                populateHometimeline(maxId);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//            case R.id.logout:
//
//                onLogoutButton();
//                return super.onOptionsItemSelected(item);
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    void onLogoutButton() {
        // forget who's logged in
        TwitterApp.getRestClient(this).clearAccessToken();

        // navigate backwards to Login screen
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
        client.clearAccessToken();
        finish();
    }

    private void getUserProfile() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;

                try {
                    String imageUrl = jsonObject.getString("profile_image_url_https");
                    Log.i(TAG, "User profile: " + imageUrl);
                    Glide.with(TimelineActivity.this).load(imageUrl).circleCrop().into(profileLogo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "User profile failed", throwable);
            }
        });
    }

    private void populateHometimeline(String maxId) {

        client.getHomeTimeline(maxId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "OnSuccess!");
                JSONArray jsonArray = json.jsonArray;
                Log.i(TAG, "Json: " + json);


                //tweets.add(new Tweet());
                //tweets.addAll(Tweet.fromJsonArray(jsonArray));
                try {
                    //adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));

                    //tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    swipeContainer.setRefreshing(false);
//                    Log.i(TAG, "Tweets: " + tweets);
//                    if (tweets.size() > 0){
//                        adapter.notifyDataSetChanged();
//
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "OnFailure! " + statusCode, throwable);
            }
        });
    }

    ActivityResultLauncher<Intent> editActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // If the user comes back to this activity from EditActivity
                    // with no error or cancellation
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();
                        // Get the data passed from EditActivity
                        Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
                        tweets.add(0, tweet);
                        adapter.notifyItemInserted(0);
                        rvTweets.smoothScrollToPosition(0);

                    }
                        // Get the data passed from EditActivity
                        //String editedString = data.getExtras().getString("newString");
                }

            });
}