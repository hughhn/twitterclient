package com.codepath.apps.basictwitter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.net.ParseException;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
	public TweetArrayAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
	}

	public static Date getTwitterDate(String date) {
		Date created = null;

		try {
			final String TWITTER = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
			SimpleDateFormat sf = new SimpleDateFormat(TWITTER, Locale.ENGLISH);
			sf.setLenient(true);
			created = sf.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
			created = null;
		}
		return created;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item
		Tweet tweet = getItem(position);
		
		View v;
		if (convertView == null) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			v = inflator.inflate(R.layout.tweet_item, parent, false);
		} else {
			v = convertView;
		}
		
		ImageView ivProfileImg = (ImageView) v.findViewById(R.id.ivProfileImg);
		TextView tvUsername = (TextView) v.findViewById(R.id.tvUsername);
		TextView tvBody = (TextView) v.findViewById(R.id.tvBody);
		TextView tvCreatedAt = (TextView) v.findViewById(R.id.tvCreatedAt);
		ivProfileImg.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImg);
		tvUsername.setText(Html.fromHtml("<b>" + tweet.getUser().getName() + "</b> " + "<i>@" + tweet.getUser().getScreenName() + "</i>"));
		tvBody.setText(tweet.getBody());
		// "Tue Aug 28 21:16:23 +0000 2012"
		tvCreatedAt.setText(Html.fromHtml("<i>"
				+ DateUtils.getRelativeTimeSpanString(
						getTwitterDate(tweet.getCreatedAt()).getTime(), System.currentTimeMillis(),
						DateUtils.SECOND_IN_MILLIS) + "</i>"));
		return v;
	}
}
