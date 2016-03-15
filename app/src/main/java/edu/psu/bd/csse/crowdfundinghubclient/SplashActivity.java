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


import java.util.ArrayList;

import edu.psu.bd.csse.crowdfundinghubclient.model.Campaign;
import edu.psu.bd.csse.crowdfundinghubclient.net.HttpController;

public class SplashActivity extends AppCompatActivity {

    private final String HOST = "162.243.227.230";
    private final String PORT = ":3000";

    private TextView progressMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressMessage = (TextView) findViewById(R.id.progressMessageText);

        RequestCampaignTask task = new RequestCampaignTask(this);
        task.execute("http://" + HOST + PORT + "/campaigns");
    }

   private class RequestCampaignTask extends AsyncTask<String, String, ArrayList<Campaign>> {

       private Activity activity;

       public RequestCampaignTask(Activity activity) {
           this.activity = activity;
       }

       @Override
       protected ArrayList<Campaign> doInBackground(String... urls) {
           JSONArray jsonResponse;

           publishProgress("Getting crowdfunding campaigns...");
           jsonResponse = HttpController.makeGetRequest(urls[0]);

           // parse Json into Campaign models
           Campaign c;
           JSONObject json;
           ArrayList<Campaign> campaigns = new ArrayList<>();
           for (int n = 0; n < jsonResponse.length(); n++) {
               try {
                   // get the JSON object from the response (A big JSON array of campaigns)
                   json = jsonResponse.getJSONObject(n);

                   // create our Java model of a campaign
                   c = new Campaign();
                   c.setUrl(json.getString("url"));
                   c.setTitle(json.getString("title"));
                   c.setType(json.getString("type"));
                   c.setAmountRaised(json.getDouble("amt_raised"));
                   c.setAmountRaised(json.getDouble("percent_complete"));

                   // print out progress on splash in case it takes a while
                   //publishProgress(c.getTitle());
                   publishProgress("Processing campaign " + n + " of " + jsonResponse.length());

                   // add to campaign results list
                   campaigns.add(c);
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }

           publishProgress("All Done!");

           return campaigns;
       }

       protected void onProgressUpdate(String... progress) {
           progressMessage.setText(progress[0]);
       }

       protected void onPostExecute(ArrayList<Campaign> campaigns) {
           // bundle our campaign data list for the Intent
           Bundle extras = new Bundle();
           extras.putParcelableArrayList("campaignsList", campaigns);

           // switch to MainActivity
           Intent intent = new Intent(activity, MainActivity.class);
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           intent.putExtras(extras);
           activity.startActivity(intent);
       }
   }
}
