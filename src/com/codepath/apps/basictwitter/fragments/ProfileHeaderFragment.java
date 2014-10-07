package com.codepath.apps.basictwitter.fragments;

import javax.crypto.spec.IvParameterSpec;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApp;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileHeaderFragment extends Fragment {
	private User user;
	TextView tvName;
	TextView tvTagline;
	TextView tvFollowers;
	TextView tvFollowing;
	ImageView ivProfileImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadProfileInfo();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// Inflate the layout
		View v = inflater.inflate(R.layout.fragment_profile_header, container,
				false);
		tvName = (TextView) v.findViewById(R.id.tvName);
		tvTagline = (TextView) v.findViewById(R.id.tvTagline);
		tvFollowers = (TextView) v.findViewById(R.id.tvFollowers);
		tvFollowing = (TextView) v.findViewById(R.id.tvFollowing);
		ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
		return v;
	}

	public void loadProfileInfo() {
		TwitterApp.getRestClient().getVerifyCredentials(
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject json) {
						user = User.fromJSON(json);
						getActivity().getActionBar().setTitle(
								"@" + user.getScreenName());
						populateProfileInfo(user);
					}

					@Override
					public void onFailure(Throwable e, String s) {
						Log.d("DEBUG", e.toString());
						Log.d("DEBUG", s.toString());
					}
				});
	}

	private void populateProfileInfo(User u) {
		if (ivProfileImage != null) {
			tvName.setText(u.getName());
			tvTagline.setText(u.getTagline());
			tvFollowers.setText(u.getFollowersCount() + " Followers");
			tvFollowing.setText(u.getFollowingCount() + " Following");
			ImageLoader.getInstance().displayImage(u.getProfileImageUrl(),
					ivProfileImage);
		}
	}
}
