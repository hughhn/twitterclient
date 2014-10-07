package com.codepath.apps.basictwitter.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.activities.ProfileActivity;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.codepath.apps.basictwitter.utils.ConnectivityHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
	private static class ViewHolder {
		ImageView ivProfileImg;
		TextView tvUsername;
		TextView tvBody;
		TextView tvCreatedAt;
	}

	private ConnectivityHelper connectivityHelper;
	private Context context;

	public TweetArrayAdapter(Context context,
			ConnectivityHelper connectivityHelper, List<Tweet> tweets) {
		super(context, 0, tweets);
		this.context = context;
		this.connectivityHelper = connectivityHelper;
	}

	// getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
	public static String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat,
				Locale.ENGLISH);
		sf.setLenient(true);

		String relativeDate = "";
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
					.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return relativeDate;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item
		Tweet tweet = getItem(position);

		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			viewHolder = new TweetArrayAdapter.ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.tweet_item, parent, false);
			viewHolder.ivProfileImg = (ImageView) convertView
					.findViewById(R.id.ivProfileImg);
			viewHolder.tvUsername = (TextView) convertView
					.findViewById(R.id.tvUsername);
			viewHolder.tvBody = (TextView) convertView
					.findViewById(R.id.tvBody);
			viewHolder.tvCreatedAt = (TextView) convertView
					.findViewById(R.id.tvCreatedAt);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.ivProfileImg.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		if (connectivityHelper.isNetworkAvailable()) {
			imageLoader.displayImage(tweet.getUser().getProfileImageUrl(),
					viewHolder.ivProfileImg);
		}
		viewHolder.tvUsername.setText(Html.fromHtml("<b>"
				+ tweet.getUser().getName() + "</b> " + "<i>@"
				+ tweet.getUser().getScreenName() + "</i>"));
		viewHolder.tvBody.setText(tweet.getBody());
		viewHolder.tvCreatedAt.setText(Html.fromHtml("<i>"
				+ getRelativeTimeAgo(tweet.getCreatedAt()) + "</i>"));

		viewHolder.ivProfileImg.setTag(tweet.getUser());
		viewHolder.ivProfileImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, ProfileActivity.class);
				User u = (User) v.getTag();
				i.putExtra("user", u);
				context.startActivity(i);
			}
		});

		return convertView;
	}
}
