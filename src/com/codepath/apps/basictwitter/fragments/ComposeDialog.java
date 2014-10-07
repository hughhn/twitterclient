package com.codepath.apps.basictwitter.fragments;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.basictwitter.R;
import com.codepath.apps.basictwitter.TwitterApp;
import com.codepath.apps.basictwitter.TwitterClient;
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeDialog extends DialogFragment {
	private ComposeDialogListener listener;
	private int charCount = 140;
	ImageView ivDelete;
	TextView tvCharCount;
	ImageView ivProfileImgCompose;
	TextView tvUsernameCompose;
	TextView tvScreennameCompose;
	EditText etTweet;
	Button btnTweet;
	private TwitterClient client;
	private static ComposeDialog dialog = null;

	public ComposeDialog() {
		// Empty constructor required for DialogFragment
	}

	public static ComposeDialog getInstance(User user) {
		if (dialog == null) {
			dialog = new ComposeDialog();
		}
		Bundle args = new Bundle();
		args.putParcelable("user", user);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		client = TwitterApp.getRestClient();

		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.compose_fragment, container);
		
		User user = getArguments().getParcelable("user");

		ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
		tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);
		ivProfileImgCompose = (ImageView) view
				.findViewById(R.id.ivProfileImgCompose);
		tvUsernameCompose = (TextView) view
				.findViewById(R.id.tvUsernameCompose);
		tvScreennameCompose = (TextView) view
				.findViewById(R.id.tvScreennameCompose);
		etTweet = (EditText) view.findViewById(R.id.etTweet);
		btnTweet = (Button) view.findViewById(R.id.btnTweet);

		tvCharCount.setText(String.valueOf(charCount));
		ivProfileImgCompose.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		if (user != null) {
			imageLoader.displayImage(user.getProfileImageUrl(),
					ivProfileImgCompose);
			tvUsernameCompose.setText(Html.fromHtml("<b>" + user.getName()
					+ "</b>"));
			tvScreennameCompose.setText(Html.fromHtml("<i>@"
					+ user.getScreenName() + "</i>"));
		}

		ivDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		etTweet.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				charCount -= (count - before);
				tvCharCount.setText(String.valueOf(charCount));
				if (charCount < 0) {
					btnTweet.setEnabled(false);
				} else if (!btnTweet.isEnabled()) {
					btnTweet.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		btnTweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String status = etTweet.getText().toString();
				listener.onFinishComposeDialog(status);
				dismiss();
			}
		});

		return view;
	}

	public interface ComposeDialogListener {
		void onFinishComposeDialog(String status);
	}

	// Store the listener (activity) that will have events fired once the
	// fragment is attached
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof ComposeDialogListener) {
			listener = (ComposeDialogListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement ComposeDialog.ComposeDialogListener");
		}
	}
}
