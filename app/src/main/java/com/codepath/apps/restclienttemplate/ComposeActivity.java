package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";

    EditText etCompose;
    Button btnSubmit;
    String tweetContent;
    public final Integer maxCount = 140;
    TwitterClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Toolbar toolbar = (Toolbar) findViewById(R.id.compose_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Compose");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etCompose = findViewById(R.id.etCompose);
        btnSubmit = findViewById(R.id.btnSubmit);
        client = TwitterApp.getRestClient(this);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry your tweet can not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > 140) {
                    Toast.makeText(ComposeActivity.this, "Sorry your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }

                publishComposedTweet(tweetContent);



                //Toast.makeText(ComposeActivity.this, "Tweet submitted successfully!", Toast.LENGTH_LONG).show();

            }
        });
        setupFloatingLabelError();
    }

    private void publishComposedTweet(String tweetContent) {
        client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Publish was successful!");
                try {
                    Tweet tweet = Tweet.fromJson(json.jsonObject);

                    Log.i(TAG, "The published tweet is: " + tweet.body);
                    Intent intent = new Intent();
                    // Pass relevant data back as a result
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, intent); // set result code and bundle data for response
                    finish(); // closes the activity, pass data to parent



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Publish not successful", throwable);
            }
        });
    }

    private void setupFloatingLabelError() {
        final TextInputLayout floatingTweetLabel = (TextInputLayout) findViewById(R.id.username_text_input_layout);
        floatingTweetLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                tweetContent = etCompose.getText().toString();
                if (tweetContent.length() > maxCount) {
                    floatingTweetLabel.setError(getString(R.string.aboveMaxCount));
                    floatingTweetLabel.setErrorEnabled(true);
                } else {
                    floatingTweetLabel.setErrorEnabled(false);
                }
            }



            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}