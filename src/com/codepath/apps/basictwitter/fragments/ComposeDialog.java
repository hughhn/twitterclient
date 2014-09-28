package com.codepath.apps.basictwitter.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import com.codepath.apps.basictwitter.models.Tweet;
import com.codepath.apps.basictwitter.models.User;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeDialog extends DialogFragment {
	private ComposeDialogListener listener;
	private User user;
	private int charCount = 140;
	ImageView ivDelete;
	TextView tvCharCount;
	ImageView ivProfileImgCompose;
	TextView tvUsernameCompose;
	TextView tvScreennameCompose;
	EditText etTweet;
	Button btnTweet;

	public ComposeDialog() {
		// Empty constructor required for DialogFragment
	}

	public static ComposeDialog newInstance(User user) {
		ComposeDialog frag = new ComposeDialog();
		Bundle args = new Bundle();
		args.putParcelable("user", user);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.compose_fragment, container);

		// retrieve user passed by main activity
		user = getArguments().getParcelable("user");

		ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
		tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);
		ivProfileImgCompose = (ImageView) view.findViewById(R.id.ivProfileImgCompose);
		tvUsernameCompose = (TextView) view.findViewById(R.id.tvUsernameCompose);
		tvScreennameCompose = (TextView) view.findViewById(R.id.tvScreennameCompose);
		etTweet = (EditText) view.findViewById(R.id.etTweet);
		btnTweet = (Button) view.findViewById(R.id.btnTweet);
		
		tvCharCount.setText(String.valueOf(charCount));
		ivProfileImgCompose.setImageResource(android.R.color.transparent);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(user.getProfileImageUrl(), ivProfileImgCompose);
		tvUsernameCompose.setText(Html.fromHtml("<b>" + user.getName() + "</b>"));
		tvScreennameCompose.setText(Html.fromHtml("<i>@" + user.getScreenName() + "</i>"));

		ivDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		etTweet.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				charCount -= (count - before);
				tvCharCount.setText(String.valueOf(charCount));
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
