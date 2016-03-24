package edu.psu.bd.csse.crowdfundinghubclient.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Zach on 1/31/2016.
 */
public class Campaign implements Parcelable {

    public static final String TYPE_REWARDS  = "Rewards";
    public static final String TYPE_DONATION = "Donations";
    public static final String TYPE_EQUITY   = "Equity";

    public static final int SECTION_REWARDS  = 1;
    public static final int SECTION_DONATION = 2;
    public static final int SECTION_EQUITY   = 3;

    private String title;
    private String url;
    private String type;
    private double amountRaised;
    private double percentComplete;

    public Campaign() {
        this.setTitle("");
        this.setUrl("");
        this.setType(TYPE_REWARDS);
        this.setAmountRaised(0.0);
        this.setPercentComplete(0.0);
    }

    public Campaign(String title, String url, String type, double amountRaised, double percentComplete) {
        this.setTitle(title);
        this.setUrl(url);
        this.setType(type);
        this.setAmountRaised(amountRaised);
        this.setPercentComplete(percentComplete);
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
        this.amountRaised = in.readDouble();
        this.percentComplete = in.readDouble();
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

    public double getAmountRaised() {
        return amountRaised;
    }

    public void setAmountRaised(double amountRaised) {
        this.amountRaised = amountRaised;
    }


    public double getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(double percentComplete) {
        this.percentComplete = percentComplete;
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
        dest.writeDouble(amountRaised);
        dest.writeDouble(percentComplete);
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
