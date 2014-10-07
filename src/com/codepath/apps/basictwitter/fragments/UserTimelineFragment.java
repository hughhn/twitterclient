package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFragment extends TweetsListFragment {
	private User user;

	public UserTimelineFragment(User user) {
		this.user = user;
	}

	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.sinceIdKey = "user_since_id";
		super.maxIdKey = "user_max_id";
		super.onAttach(activity);
	}

	public void populateFeed(long sinceId, long maxId) {
		final long tempSinceId = sinceId;
		final long tempMaxId = maxId;
		
		client.getUserTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				Log.d("DEBUG", "getMentionsTimeline() rsp:\n" + json.toString());
				SharedPreferences.Editor editor = mSettings.edit();
				ArrayList<Tweet> tweets = Tweet.fromJSONArray(json);

				if (tweets != null && !tweets.isEmpty()) {
					if (tempSinceId == -1 && tempMaxId == -1) {
						// first load, update tracking IDs
						aTweets.addAll(tweets);
						editor.putLong(sinceIdKey, tweets.get(0).getUid());
						editor.putLong(maxIdKey, tweets.get(tweets.size() - 1)
								.getUid() - 1);
						editor.commit();
					} else if (tempSinceId != -1) {
						// pull to refresh, update since_id
						for (int i = tweets.size() - 1; i >= 0; i--) {
							aTweets.insert(tweets.get(i), 0);
						}
						editor.putLong(sinceIdKey, tweets.get(0).getUid());
						editor.commit();
					} else if (tempMaxId != -1) {
						// scroll down, update max_id
						aTweets.addAll(tweets);
						editor.putLong(maxIdKey, tweets.get(tweets.size() - 1)
								.getUid() - 1);
						editor.commit();
					}

					// Use AsyncTask to cache the tweets to local
					// ActiveAndroid
					// database
					// Caching method also takes care of deduping the users'
					// IDs.
					Tweet.saveItems(tweets);
				}
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s.toString());
			}

			@Override
			protected void handleFailureMessage(Throwable e, String s) {
				Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s.toString());
			}
		}, mCount, tempSinceId, tempMaxId, user.getUid());
	}
}
