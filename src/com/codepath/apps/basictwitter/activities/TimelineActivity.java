package com.codepath.apps.basictwitter.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApp;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.fragments.ComposeDialog;
import com.codepath.apps.basictwitter.fragments.ComposeDialog.ComposeDialogListener;
import com.codepath.apps.basictwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.basictwitter.fragments.MentionsTimelineFragment;
import com.codepath.apps.basictwitter.listeners.SupportFragmentTabListener;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimelineActivity extends ActionBarActivity implements
		ComposeDialogListener {

	private ComposeDialog composeDialog;
	private TwitterClient client;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApp.getRestClient();
		setupTabs();
		loadSelfUser();
	}

	private void loadSelfUser() {
		if (user == null) {
			client.getVerifyCredentials(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject json) {
					user = User.fromJSON(json);
					user.save();
				}

				@Override
				public void onFailure(Throwable e, String s) {
					Log.d("DEBUG", e.toString());
					Log.d("DEBUG", s.toString());
				}
			});
		}
	}

	private void setupTabs() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab1 = actionBar
				.newTab()
				.setText("Home")
				.setIcon(R.drawable.ic_action_home)
				.setTag("HomeTimelineFragment")
				.setTabListener(
						new SupportFragmentTabListener<HomeTimelineFragment>(
								R.id.flContainer, this, "HomeTag",
								HomeTimelineFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
				.newTab()
				.setText("Mentions")
				.setIcon(R.drawable.ic_action_mentions)
				.setTag("MentionsTimelineFragment")
				.setTabListener(
						new SupportFragmentTabListener<MentionsTimelineFragment>(
								R.id.flContainer, this, "MentionsTag",
								MentionsTimelineFragment.class));
		actionBar.addTab(tab2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photos, menu);
		// MenuItem composeItem = menu.findItem(R.id.action_compose);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_compose:
			showComposeDialog();
			return true;
		case R.id.action_profile:
			onProfileView();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onProfileView() {
		if (user == null) {
			Toast.makeText(this, "account info not yet loaded!", Toast.LENGTH_SHORT).show();
			loadSelfUser();
			return;
		}
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra("user", user);
		startActivity(i);
	}

	public void showComposeDialog() {
		if (user == null) {
			Toast.makeText(this, "account info not yet loaded!", Toast.LENGTH_SHORT).show();
			loadSelfUser();
			return;
		}
		FragmentManager fm = getSupportFragmentManager();
		composeDialog = ComposeDialog.getInstance(user);
		composeDialog.show(fm, "fragment_compose");
	}

	public void onFinishComposeDialog(String status) {
		// POST tweet and refresh adapter/view
		client.postUpdate(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject json) {
				Log.d("DEBUG", json.toString());
				Tweet tweet = Tweet.fromJSON(json);
				HomeTimelineFragment homeFragment = (HomeTimelineFragment) getSupportFragmentManager()
						.findFragmentByTag("HomeTag");
				if (homeFragment != null) {
					homeFragment.insertAt(tweet, 0);
				} else {
					Log.d("DEBUG", "cannot find HomeTimeline");
				}
			}

			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("DEBUG", e.toString());
				Log.d("DEBUG", s.toString());
			}
		}, status);

	}
}
