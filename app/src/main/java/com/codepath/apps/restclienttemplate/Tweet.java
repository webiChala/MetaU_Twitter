package com.codepath.apps.restclienttemplate;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public String mediaUrl;
    public String id;
    public String notRelativeTime;
    public boolean isfavorited;
    public int favoriteCount;
    public int retweetedCount;

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {


        if (jsonObject.has("retweeted_status")){
            return null;
        }

        Tweet tweet = new Tweet();
        //tweet.body = jsonObject.getString("text");
        tweet.notRelativeTime = jsonObject.getString("created_at");
        tweet.createdAt = tweet.getRelativeTimeAgo(tweet.notRelativeTime);
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getString("id_str");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.isfavorited = jsonObject.getBoolean("favorited");
        tweet.retweetedCount = jsonObject.getInt("retweet_count");

        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }

        if (jsonObject.getJSONObject("entities").has("media")){
            tweet.mediaUrl = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url_https");
            Log.i("Tweet", tweet.mediaUrl);
        } else{
            tweet.mediaUrl = "null";
        }

        //jsonObject.getJSONObject("entities");
        return tweet;
    }
    public Tweet(){

    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        Log.i("JsonArray: " , String.valueOf(jsonArray.getJSONObject(0)));
        Log.i("length", String.valueOf(jsonArray.length()));
        for (int i=0; i<jsonArray.length(); i++){
            Tweet newTweet = fromJson(jsonArray.getJSONObject(i));
            //Log.i("Count", fromJson(jsonArray.getJSONObject(i)).body);
            if (newTweet!=null) {
                tweets.add(newTweet);
            }
        }
        Log.i("Tweets", String.valueOf(tweets));

        return tweets;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
