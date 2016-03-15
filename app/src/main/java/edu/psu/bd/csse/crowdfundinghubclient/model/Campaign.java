package edu.psu.bd.csse.crowdfundinghubclient.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Zach on 1/31/2016.
 */
public class Campaign implements Parcelable {

    public static ArrayList<Campaign> campaigns = new ArrayList<>();

    public static final String TYPE_DONATION = "Donation";
    public static final String TYPE_REWARDS  = "Rewards";
    public static final String TYPE_EQUITY   = "Equity";

    private String title;
    private String url;
    private String type;

    public Campaign() { }

    public Campaign(String title, String url, String type) {
        this.setTitle(title);
        this.setUrl(url);
        this.setType(type);
    }

    /**
     * Private contructor for Android OS 'Parcel' usage
     * Implementing Parcelable allows us to pass Lists of Campaign objects
     * across intents (basically)
     * @param in
     */
    private Campaign(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
        this.type = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /*
    * PARCELABLE IMPLEMENTATION
    */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(type);
    }

    public static final Parcelable.Creator<Campaign> CREATOR = new Parcelable.Creator<Campaign>() {
        public Campaign createFromParcel(Parcel in) {
            return new Campaign(in);
        }

        public Campaign[] newArray(int size) {
            return new Campaign[size];
        }
    };
}
