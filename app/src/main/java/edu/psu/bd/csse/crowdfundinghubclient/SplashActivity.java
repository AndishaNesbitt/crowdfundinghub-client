package edu.psu.bd.csse.crowdfundinghubclient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import edu.psu.bd.csse.crowdfundinghubclient.model.Campaign;
import edu.psu.bd.csse.crowdfundinghubclient.model.DbHandler;
import edu.psu.bd.csse.crowdfundinghubclient.controller.HttpController;

public class SplashActivity extends AppCompatActivity {

    private final String HOST = "162.243.227.230"; // Public IP of our DigitalOcean server
    private final String PORT = ":3000";           // service listening on port 3000

    private TextView progressMessage;              // TextView that displays progress messages

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressMessage = (TextView) findViewById(R.id.progressMessageText);

        RequestCampaignTask task = new RequestCampaignTask(this);
        task.execute("http://" + HOST + PORT + "/campaigns");
    }

   private class RequestCampaignTask extends AsyncTask<String, String, Boolean> {

       private Activity activity;
       private DbHandler db;

       public RequestCampaignTask(Activity activity) {
           this.activity = activity;
           // need to Upgrade database to drop old table***
           db = new DbHandler(activity, null, null, 1);
       }

       @Override
       protected Boolean doInBackground(String... urls) {
           JSONArray jsonResponse;

           publishProgress("Getting crowdfunding campaigns...");

           // fetch campaigns from server. returns a JSON Array of campaigns
           jsonResponse = HttpController.makeGetRequest(urls[0]);

           // make sure the request was successful
           if (jsonResponse == null) {
               publishProgress("Could not fetch campaigns from server\nCheck your internet " +
                       "connection and try again!");

               return false; // return with failure
           }

           db.upgrade();

           // parse Json into Campaign models
           Campaign c;
           JSONObject json;
           //ArrayList<Campaign> campaigns = new ArrayList<>();
           for (int n = 0; n < jsonResponse.length(); n++) {
               try {
                   // get the JSON object from the response (A big JSON array of campaigns)
                   json = jsonResponse.getJSONObject(n);

                   // create our Java model of a campaign from JSON object entry
                   c = new Campaign();
                   c.setUrl(json.getString("url"));
                   c.setTitle(json.getString("title"));
                   c.setType(json.getString("type"));
                   c.setAmountRaised(json.getDouble("amt_raised"));
                   c.setPercentComplete(json.getDouble("percent_complete"));

                   // print out progress on splash in case it takes a while (might not be seen otherwise)
                   publishProgress("Processing campaign " + n + " of " + jsonResponse.length());

                   // add campaign model to local database (acts as a cache)
                   db.addCampaign(c);
               } catch (JSONException e) {
                   e.printStackTrace();
                   publishProgress("Sorry something went wrong. It is on our end :(");
                   return false; // something went wrong parsing JSON
               }
           }

           publishProgress("All Done!");

           return true; // campaigns received and cached to sqlite database
       }

       protected void onProgressUpdate(String... progress) {
           progressMessage.setText(progress[0]);
       }

       protected void onPostExecute(Boolean success) {
           Intent intent;
           // if the connection to the server and getting campaigns was successful..
           if (success) {
               // create an intent to switch to the main activity for browsing
               intent = new Intent(activity, MainActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

               activity.startActivity(intent);
           } else {
               // show error message / screen
               // OR JUST load old campaigns from local database from last successful launch
           }
       }
   }
}
