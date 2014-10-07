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

	public ProfileHeaderFragment(User user) {
		this.user = user;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		populateProfileInfo(user);
		return v;
	}

	private void populateProfileInfo(User u) {
		if (u == null) return;
		
		getActivity().getActionBar().setTitle("@" + u.getScreenName());
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
