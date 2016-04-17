package com.maps.tanush.howcloseru;

import android.os.StrictMode;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Tanush on 12/4/2015.
 */
public class JSONParser {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    // constructor
    public JSONParser() throws IOException {
    }
    public String getJSONFromUrl(String urli) {

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        // Making HTTP request
        try {
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(urli);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            //Log.e(LOG_TAG, "Error processing Places API URL", e);
            //return resultList;
        } catch (IOException e) {
            //Log.e(LOG_TAG, "Error connecting to Places API", e);
            //return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return jsonResults.toString();

    }
}

