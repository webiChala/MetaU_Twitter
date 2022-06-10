package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class wholepictureActivity extends AppCompatActivity {

    ImageView wholePicture;
    TextView goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wholepicture);
        goBack = findViewById(R.id.goBack);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String mediaImage = getIntent().getStringExtra("mediaImage");
        Log.i("Whole", mediaImage);
        wholePicture = findViewById(R.id.mediaWhole);

        if (mediaImage == null) {
            wholePicture.setVisibility(View.GONE);
        } else{
            Glide.with(this).load(mediaImage).into(wholePicture);
        }


    }
}