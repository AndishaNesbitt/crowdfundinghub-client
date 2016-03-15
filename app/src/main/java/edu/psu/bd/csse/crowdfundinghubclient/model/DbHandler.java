package edu.psu.bd.csse.crowdfundinghubclient.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zach on 3/14/2016.
 */
public class DbHandler extends SQLiteOpenHelper implements ICampaignCrud {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "campaignstore.db";

    /* Table definition */
    private static final String TABLE_NAME = "CAMPAIGNS";
    private static final String TABLE_COL_TITLE = "TITLE";
    private static final String TABLE_COL_URL = "URL";
    private static final String TABLE_COL_TYPE = "TYPE";
    private static final String TABLE_COL_AMT_RAISED = "AMT_RAISED";
    private static final String TABLE_COL_PERCENT_COMPLETE = "PERCENT_COMPLETE";

    private SQLiteDatabase db;

    public DbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, DB_VERSION); // may have to change VERSION to argument
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + TABLE_COL_TITLE + " TEXT, "
                + TABLE_COL_URL + " TEXT, "
                + TABLE_COL_TYPE + " TEXT, "
                + TABLE_COL_AMT_RAISED + " REAL, "
                + TABLE_COL_PERCENT_COMPLETE + " REAL)";

        // execute SQL to create the table for campaigns
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    private List<Campaign> queryCampaignList(String query) {
        List<Campaign> campaigns = new ArrayList<>();
        Campaign c;

        db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()) {
            c = new Campaign();
            c.setTitle(cursor.getString(0));
            c.setUrl(cursor.getString(1));
            c.setType(cursor.getString(2));
            c.setAmountRaised(cursor.getDouble(3));
            c.setPercentComplete(cursor.getDouble(3));

            campaigns.add(c);
        }
        cursor.close();
        db.close();

        return campaigns;
    }

    // temporary, might reuse elsewhere?
    private String getTypeName(int section) {
        switch (section) {
            case Campaign.SECTION_REWARDS:
                return Campaign.TYPE_REWARDS;
            case Campaign.SECTION_DONATION:
                return Campaign.TYPE_DONATION;
            case Campaign.SECTION_EQUITY:
                return Campaign.TYPE_EQUITY;
            default:
                return "";
        }
    }

    /* CRUD Operations */

    @Override
    public void addCampaign(Campaign campaign) {
        ContentValues values = new ContentValues();
        values.put(TABLE_COL_TITLE, campaign.getTitle());
        values.put(TABLE_COL_URL, campaign.getUrl());
        values.put(TABLE_COL_TYPE, campaign.getType());
        values.put(TABLE_COL_AMT_RAISED, campaign.getAmountRaised());
        values.put(TABLE_COL_PERCENT_COMPLETE, campaign.getPercentComplete());

        db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public List<Campaign> getCampaigns(int section) {

        String query = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + TABLE_COL_TYPE + " = \"" + getTypeName(section) + "\"";

        return queryCampaignList(query);
    }
}
