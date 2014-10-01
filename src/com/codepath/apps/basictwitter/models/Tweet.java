package com.codepath.apps.basictwitter.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;


@Table(name = "Tweets")
public class Tweet extends Model {
	@Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;

	@Column(name = "body")
	private String body;

	@Column(name = "created_at")
	private String createdAt;

	@Column(name = "user", onUpdate = ForeignKeyAction.RESTRICT, onDelete = ForeignKeyAction.CASCADE)
	private User user;

	public Date setDateFromString(String date) {
		Date returnedDate = null;
		try {
			SimpleDateFormat sf = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
			sf.setLenient(true);
			returnedDate = sf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return returnedDate;
	}

	public static Tweet fromJSON(JSONObject jsonObject) {
		Tweet tweet = new Tweet();

		try {
			tweet.body = jsonObject.getString("text");
			tweet.uid = jsonObject.getLong("id");
			tweet.createdAt = jsonObject.getString("created_at");
			tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return tweet;
	}

	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User u) {
		this.user = u;
	}

	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject tweetJson = null;
			try {
				tweetJson = jsonArray.getJSONObject(i);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			Tweet tweet = Tweet.fromJSON(tweetJson);
			if (tweet != null) {
				tweets.add(tweet);
			}
		}

		return tweets;
	}

	// Record Finders
	public static Tweet byId(long id) {
		return new Select().from(Tweet.class).where("uid = ?", id)
				.executeSingle();
	}

	public static List<Tweet> recentItems(int count) {
		List<Tweet> tweets = null;

		try {
			tweets = new Select().from(Tweet.class).orderBy("uid DESC")
					.limit(String.valueOf(count)).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tweets;
	}

	public static void saveItems(List<Tweet> tweets) {
		new CacheSaveAsyncTask().execute(tweets);
	}
}

// Async Task for saving Tweet(s)
class CacheSaveAsyncTask extends AsyncTask<List<Tweet>, Void, Void> {
	public CacheSaveAsyncTask() {
		
	}
	
	@Override
	protected Void doInBackground(List<Tweet>... params) {
		List<Tweet> tweets = params[0];
		for (int i = 0; i < tweets.size(); i++) {
			Tweet tweet = tweets.get(i);
			// check if user already exists
			User u = User.byUid(tweet.getUser().getUid());
			if (u == null) {
				tweet.getUser().save();
			} else {
				tweet.setUser(u);
			}
			tweet.save();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		
	}
}
