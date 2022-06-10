package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {

    ImageView detailuserPhoto;
    TextView detailuserScreenName;
    TextView detailuserTweet;
    ImageView detailmediaImage;
    TextView detailcreatedAt;
    TextView detailgoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));

        detailuserTweet = findViewById(R.id.detailuserTweet);
        detailuserScreenName = findViewById(R.id.detailuserScreenName);

        detailuserPhoto = findViewById(R.id.detailuserPhoto);
        detailmediaImage = findViewById(R.id.detailmediaImage);
        detailcreatedAt = findViewById(R.id.detailcreatedAt);
        detailgoBack = findViewById(R.id.detailGoBack);

        detailuserTweet.setText(tweet.body);
        detailuserScreenName.setText(tweet.user.screenName);
        detailcreatedAt.setText(tweet.notRelativeTime);
        if (tweet.mediaUrl == null) {
            detailmediaImage.setVisibility(View.GONE);
        } else{
            Glide.with(this).load(tweet.mediaUrl).into(detailmediaImage);

        }
        Glide.with(this).load(tweet.user.ProfileImageUrl).into(detailuserPhoto);

        detailgoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        detailmediaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, wholepictureActivity.class);
                intent.putExtra("mediaImage", tweet.mediaUrl);
                DetailsActivity.this.startActivity(intent);
            }
        });


    }
}