package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

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


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userPhoto;
        TextView userScreenName;
        TextView userTweet;
        ImageView mediaImage;
        TextView createdAt;
        //ImageView timeline_icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userTweet = (TextView) itemView.findViewById(R.id.userTweet);
            userScreenName = itemView.findViewById(R.id.userScreenName);
            userTweet = itemView.findViewById(R.id.userTweet);
            userPhoto = itemView.findViewById(R.id.userPhoto);
            mediaImage = itemView.findViewById(R.id.mediaImage);
            createdAt = itemView.findViewById(R.id.createdAt);
            //timeline_icon = itemView.findViewById(R.id.timeline_icon);
        }
        public void bind(Tweet tweet) {
            userScreenName.setText(tweet.user.screenName);
            userTweet.setText(tweet.body);
            Glide.with(context).load(tweet.user.ProfileImageUrl).circleCrop().into(userPhoto);

            if (tweet.mediaUrl.equals("null")) {
                mediaImage.setVisibility(View.GONE);
            } else{
                Glide.with(context).load(tweet.mediaUrl).into(mediaImage);
            }
            createdAt.setText(tweet.createdAt);
            //Glide.with(context).load(tweet.user.ProfileImageUrl).circleCrop().into(timeline_icon);

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
