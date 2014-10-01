package com.codepath.apps.basictwitter.models;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Users")
public class User extends Model implements Parcelable {
	// This is how you avoid duplicates based on a unique ID
	@Column(name = "uid")
	private long uid;

	@Column(name = "name")
	private String name;

	@Column(name = "screen_name")
	private String screenName;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeLong(uid);
		dest.writeString(screenName);
		dest.writeString(profileImageUrl);
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	private User(Parcel in) {
		this.name = in.readString();
		this.uid = in.readLong();
		this.screenName = in.readString();
		this.profileImageUrl = in.readString();
	}

	public User() {
		// normal actions performed by class, it's still a normal object!
	}

	public static User fromJSON(JSONObject jsonObject) {
		User user = new User();

		try {
			user.name = jsonObject.getString("name");
			user.uid = jsonObject.getLong("id");
			user.screenName = jsonObject.getString("screen_name");
			user.profileImageUrl = jsonObject.getString("profile_image_url");
		} catch (JSONException e) {
			e.printStackTrace();
			// return null;
		}

		return user;
	}

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	// Record Finders
	public static User byId(long id) {
		User user = null;

		try {
			user = new Select().from(User.class).where("id = ?", id)
					.executeSingle();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;
	}

	// Record Finders
	public static User byUid(long id) {
		User user = null;

		try {
			user = new Select().from(User.class).where("uid = ?", id)
					.executeSingle();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;
	}

	public static List<User> recentItems() {
		return new Select().from(User.class).orderBy("uid DESC").limit("300")
				.execute();
	}
}
