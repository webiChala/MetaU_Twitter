package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter  extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView userPhoto;
        TextView userScreenName;
        TextView userTweet;
        ImageView mediaImage;
        TextView createdAt;
        ImageButton ibFavorited;
        TextView tvFavoriteCount;
        TextView username;
        ImageButton ibReply;
        //ImageView timeline_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userTweet = (TextView) itemView.findViewById(R.id.userTweet);
            userScreenName = itemView.findViewById(R.id.userScreenName);
            userTweet = itemView.findViewById(R.id.userTweet);
            userPhoto = itemView.findViewById(R.id.userPhoto);
            mediaImage = itemView.findViewById(R.id.mediaImage);
            createdAt = itemView.findViewById(R.id.createdAt);
            ibFavorited = itemView.findViewById(R.id.ibFavorite);
            tvFavoriteCount = itemView.findViewById(R.id.favoriteCount);
            username = itemView.findViewById(R.id.username);
            ibReply = itemView.findViewById(R.id.ibReply);

            itemView.setOnClickListener(this);
            //timeline_icon = itemView.findViewById(R.id.timeline_icon);
        }
        public void bind(Tweet tweet) {
            userScreenName.setText(tweet.user.screenName);
            userTweet.setText(tweet.body);
            tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
            createdAt.setText(tweet.createdAt);
            username.setText(tweet.user.name);

            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ComposeActivity.class);
                    intent.putExtra("tweet_to_reply_to", Parcels.wrap(tweet));
//                    intent.putExtra("should_reply_to_tweet", true);
//                    intent.putExtra("id_of_tweet_to_reply_to", tweet.id);
//                    intent.putExtra("screenname_of_tweet_to_reply_to", tweet.user.screenName);
                    ((Activity) context).startActivityForResult(intent, Activity.RESULT_OK);
                    //context.startActivity(intent);
                }
            });
            Glide.with(context).load(tweet.user.ProfileImageUrl).circleCrop().into(userPhoto);

            if (tweet.isfavorited){
                Drawable newImage = context.getDrawable(R.drawable.ic_vector_heart);
                ibFavorited.setImageDrawable(newImage);
            } else{
                Drawable newImage = context.getDrawable(R.drawable.ic_vector_heart_stroke);
                ibFavorited.setImageDrawable(newImage);
            }

            if (tweet.mediaUrl.equals("null")) {
                mediaImage.setVisibility(View.GONE);
            } else{
                Glide.with(context).load(tweet.mediaUrl).transform(new RoundedCorners(20)).into(mediaImage);
                mediaImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, wholepictureActivity.class);
                        intent.putExtra("mediaImage", tweet.mediaUrl);
                        context.startActivity(intent);
                    }
                });
            }

            ibFavorited.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!tweet.isfavorited) {


                        tweet.isfavorited = true;
                        Drawable newImage = context.getDrawable(R.drawable.ic_vector_heart);
                        ibFavorited.setImageDrawable(newImage);
                        tweet.favoriteCount += 1;
                        tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));

                        TwitterApp.getRestClient(context).favorite(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "This should be favprited!");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("adapter", "favorite not successful!");
                            }
                        });
                    } else{
                        TwitterApp.getRestClient(context).unfavorite(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("adapter", "This should be favprited!");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("adapter", "unfavorite not successful!");
                            }
                        });
                        tweet.isfavorited = false;
                        Drawable newImage = context.getDrawable(R.drawable.ic_vector_heart_stroke);
                        ibFavorited.setImageDrawable(newImage);

                        tweet.favoriteCount -= 1;
                        tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                    }
                }
            });

            //Glide.with(context).load(tweet.user.ProfileImageUrl).circleCrop().into(timeline_icon);

        }


        @Override
        public void onClick(View view) {
            Tweet tweet = tweets.get(this.getAdapterPosition());
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("Tweet", Parcels.wrap(tweet));
            context.startActivity(intent);

        }
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }
}
