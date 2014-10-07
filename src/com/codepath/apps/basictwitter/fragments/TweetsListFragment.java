package com.codepath.apps.basictwitter.fragments;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApp;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.adapters.TweetArrayAdapter;
import com.codepath.apps.basictwitter.listeners.EndlessScrollListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.utils.ConnectivityHelper;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TweetsListFragment extends Fragment {

	protected ArrayList<Tweet> tweets;
	protected ArrayAdapter<Tweet> aTweets;
	protected ListView lvTweets;
	protected SwipeRefreshLayout swipeContainer;
	protected final int mCount = 20;
	protected ConnectivityHelper connectivityHelper;
	protected TwitterClient client;
	protected SharedPreferences mSettings;
	protected String sinceIdKey = "since_id";
	protected String maxIdKey = "max_id";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tweets = new ArrayList<Tweet>();
		connectivityHelper = new ConnectivityHelper(getActivity());
		aTweets = new TweetArrayAdapter(getActivity(), connectivityHelper,
				tweets);
		mSettings = getActivity().getSharedPreferences("Settings", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		client = TwitterApp.getRestClient();
		// Inflate the layout
		View v = inflater.inflate(R.layout.fragment_tweets_list, container,
				false);
		// Assign view references

		setupViews(v);

		// Return view
		return v;
	}

	private void setupViews(View v) {
		swipeContainer = (SwipeRefreshLayout) v
				.findViewById(R.id.swipeContainer);
		lvTweets = (ListView) v.findViewById(R.id.lvTweets);
		lvTweets.setAdapter(aTweets);

		// Setup refresh listener which triggers new data loading
		swipeContainer.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Your code to refresh the list here.
				// Make sure you call swipeContainer.setRefreshing(false)
				// once the network request has completed successfully.
				long sinceId = mSettings.getLong(sinceIdKey, -1);
				customLoadMoreDataFromApi(sinceId, -1);
			}
		});

		// Attach the listener to the AdapterView onCreate
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your
				// AdapterView
				long maxId = mSettings.getLong(maxIdKey, -1);
				customLoadMoreDataFromApi(-1, maxId);
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

		// Initial load tweets from cache, if there is any
		/*if (sinceId == -1 && maxId == -1) {
			ArrayList<Tweet> tweets = (ArrayList<Tweet>) Tweet.recentItems(200);
			if (tweets != null && !tweets.isEmpty()) {
				aTweets.addAll(tweets);
				sinceId = tweets.get(0).getUid();
				SharedPreferences.Editor editor = mSettings.edit();
				editor.putLong(sinceIdKey, sinceId);
				editor.commit();
			}
		}*/

		// Check Internet connection
		if (!connectivityHelper.isNetworkAvailable()) {
			Toast.makeText(getActivity().getApplicationContext(),
					"network unavailable", Toast.LENGTH_SHORT).show();
			return;
		}

		populateFeed(sinceId, maxId);
	}

	public void populateFeed(long sinceId, long maxId) {
		swipeContainer.setRefreshing(false);
	}

	public void insertAt(Tweet tweet, int index) {
		aTweets.insert(tweet, index);
	}

	public void addAll(ArrayList<Tweet> tweets) {
		aTweets.addAll(tweets);
	}
}
