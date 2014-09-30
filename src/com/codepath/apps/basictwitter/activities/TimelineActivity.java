package com.codepath.apps.basictwitter.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApp;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.basictwitter.fragments.ComposeDialog;
import com.codepath.apps.basictwitter.fragments.ComposeDialog.ComposeDialogListener;
import com.codepath.apps.basictwitter.listeners.EndlessScrollListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends FragmentActivity implements
		ComposeDialogListener {
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private ListView lvTweets;
	private SwipeRefreshLayout swipeContainer;
	private ComposeDialog composeDialog;
	private User meUser = null;
	private long mSinceId = -1;
	private long mMaxId = -1;
	private final int mCount = 20;

	public Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApp.getRestClient();
		setupViews();
	}

	private void setupViews() {
		swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
		lvTweets.setAdapter(aTweets);
		
		// Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
            	// aTweets.clear();
            	// mSinceId = -1;
            	// mMaxId = -1;
            	customLoadMoreDataFromApi(mSinceId, -1);
            } 
        });

		// Attach the listener to the AdapterView onCreate
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your
				// AdapterView
				// customLoadMoreDataFromApi(page);
				customLoadMoreDataFromApi(-1, mMaxId);
			}
		});
		
		// Initialize feed
		customLoadMoreDataFromApi(-1, -1);
	}

	// Append more data into the adapter
	public void customLoadMoreDataFromApi(long sinceId, long maxId) {
		// This method probably sends out a network request and appends new data
		// items to your adapter.
		// Use the offset value and add it as a parameter to your API request to
		// retrieve paginated data.
		// Deserialize API response and then construct new objects to append to
		// the adapter
		// Check Internet connection
		if (!isNetworkAvailable()) {
			Toast.makeText(getApplicationContext(), "network unavailable",
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		final long tempSinceId = sinceId;
		final long tempMaxId = maxId;
		client.getHomeTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				Log.d("DEBUG", "getHomeTimeline() rsp:\n" + json.toString());
				ArrayList<Tweet> tweets = Tweet.fromJSONArray(json);
				

				if (tweets != null && !tweets.isEmpty()) {
					if (tempSinceId == -1 && tempMaxId == -1) {
						// first load, update tracking IDs
						aTweets.addAll(tweets);
						mSinceId = tweets.get(0).getUid();
						mMaxId = tweets.get(tweets.size() - 1).getUid() - 1;
					}
					else if (tempSinceId != -1) {
						// pull to refresh, update since_id
						for (int i = tweets.size() - 1; i >= 0; i--) {
							aTweets.insert(tweets.get(i), 0);
						}
						mSinceId = tweets.get(0).getUid();
					} else if (tempMaxId != -1) {
						// scroll down, update max_id
						aTweets.addAll(tweets);
						mMaxId = tweets.get(tweets.size() - 1).getUid() - 1;
					}
					
				}
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Toast.makeText(TimelineActivity.this, s, Toast.LENGTH_SHORT).show();
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s.toString());
			}
			
			@Override
			protected void handleFailureMessage(Throwable e, String s) {
				Toast.makeText(TimelineActivity.this, s, Toast.LENGTH_SHORT).show();
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s.toString());
			}
		}, mCount, tempSinceId, tempMaxId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photos, menu);
		// MenuItem composeItem = menu.findItem(R.id.action_compose);
		return true;
	}

	public void showComposeDialog(MenuItem mi) {
		final FragmentManager fm = getSupportFragmentManager();

		if (meUser == null) {
			client.getVerifyCredentials(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject json) {
					meUser = User.fromJSON(json);
					composeDialog = ComposeDialog.newInstance(meUser);
					composeDialog.show(fm, "fragment_compose");
				}

				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("DEBUG", e.toString());
					Log.d("DEBUG", s.toString());
				}
			});
		} else {
			composeDialog = ComposeDialog.newInstance(meUser);
			composeDialog.show(fm, "fragment_compose");
		}
	}

	public void onFinishComposeDialog(String status) {
		// POST tweet and refresh adapter/view
		client.postUpdate(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				Log.d("DEBUG", json.toString());
				Tweet tweet = Tweet.fromJSON(json);
				aTweets.insert(tweet, 0);
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s.toString());
			}
		}, status);

	}
}
