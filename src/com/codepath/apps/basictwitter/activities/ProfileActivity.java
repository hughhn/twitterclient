package com.codepath.apps.basictwitter.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.fragments.ProfileHeaderFragment;
import com.codepath.apps.basictwitter.fragments.UserTimelineFragment;
import com.codepath.apps.basictwitter.models.User;

public class ProfileActivity extends FragmentActivity {
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		user = getIntent().getParcelableExtra("user");
		
		// Begin the transaction
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// Replace the container with the new fragment
		ft.replace(R.id.flProfileHeaderContainer, new ProfileHeaderFragment(user));
		ft.replace(R.id.flProfileContainer, new UserTimelineFragment(user));
		// Execute the changes specified
		ft.commit();
	}
}
