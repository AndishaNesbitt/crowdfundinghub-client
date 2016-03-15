package edu.psu.bd.csse.crowdfundinghubclient.net;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Zach on 1/31/2016.
 */
public class HttpController {

    public static JSONArray makeGetRequest(String loc) {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder responseString = null;
        JSONArray json = null;

        try {
            url = new URL(loc);

            // setup the http url connection and connect
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(false);
            urlConnection.setRequestMethod("GET");
            //urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            // Receive response from the server through an input stream
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(in);

            // Read the input via the BufferedReader and construct the response string
            String line = "";
            responseString = new StringBuilder();
            while((line = reader.readLine()) != null)
                responseString.append(line);

            // parse responseString into a JSON Object
            json = new JSONArray(responseString.toString());

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            // close the connection between the app and server
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return json;
    }
}
