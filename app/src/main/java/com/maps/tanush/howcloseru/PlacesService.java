package com.maps.tanush.howcloseru;

/**
 * Created by Tanush on 12/5/2015.
 */

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

//import com.google.android.gms.location.places.Place;

/**
 * @author saxman
 */
public class PlacesService {
    private static StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private static final String LOG_TAG = "ExampleApp";

    //private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String PLACES_API_BASE ="https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "";//"/search";

    private static final String OUT_JSON = "";//"/json/";

    // KEY!
    private static final String API_KEY = "AIzaSyA520LTRt9rqbz02oMcJqHtbjfsG8ZAltw";

    public static ArrayList<Place> autocomplete(String input) {
        ArrayList<Place> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_AUTOCOMPLETE);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
            JSONObject jObj = predsJsonArray.getJSONObject(i);
            Place place = new Place();
            place.setlng(Double.parseDouble(jObj.getString("lng")));
            place.setlat(Double.parseDouble(jObj.getString("lat")));
            place.setplace(jObj.getString("places"));
            resultList.add(place);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

        return resultList;
    }

    public static ArrayList<Place> search(String keyword, double lat, double lng, int radius) {

        ArrayList<Place> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StrictMode.setThreadPolicy(policy);
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&types=" + URLEncoder.encode(keyword, "utf8"));
            sb.append("&location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=" + String.valueOf(radius));
            Log.d("URL ", sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
                place.setsearch(keyword);
                JSONObject jObj = predsJsonArray.getJSONObject(i);
                JSONObject jgeo = (JSONObject) jObj.get("geometry");
                JSONObject jloc = (JSONObject)jgeo.get("location");
                place.setlng(Double.parseDouble(jloc.getString("lng")));
                place.setlat(Double.parseDouble(jloc.getString("lat")));
                try {
                    place.setplace(jObj.getString("name"));
                    place.setvicinity(jObj.getString("vicinity"));
                    place.seticon(jObj.getString("icon"));
                }
                catch(JSONException e) {
                    place.seticon("");
                    place.setvicinity("");
                    place.setplace("");
                }
                try{
                    JSONObject johrs = (JSONObject)jObj.get("opening_hours");
                    place.setOpennow(johrs.getString("open_now"));
                }
                catch(JSONException e) {
                    place.setOpennow("");
                }
                resultList.add(place);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }
        return resultList;
    }

    public static Place details(String reference) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_DETAILS);
            sb.append(OUT_JSON);
            sb.append("?sensor=false");
            sb.append("&key=" + API_KEY);
            sb.append("&reference=" + URLEncoder.encode(reference, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        Place place = null;
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");

            place = new Place();
            place.setlat(Double.parseDouble(jsonObj.getString("lat")));
            place.setlng(Double.parseDouble(jsonObj.getString("lng")));
            place.setplace(jsonObj.getString("places"));
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

        return place;
    }
}

class Place {

    private String search;
    private String icon;
    private String opennow;
    private String vicinity;
    private double lat;
    private double lng;
    private String place;

    public void setsearch(String search)
    {
        this.search = search;
    }

    public String getsearch() {
        return search;
    }

    public void setvicinity(String vicinity)
    {
        this.vicinity = vicinity;
    }

    public String getvicinity() {
        return vicinity;
    }


    public void setOpennow(String opnnow)
    {
        this.opennow = opnnow;
    }

    public String getopennow() {
        return opennow;
    }

    public void seticon(String icon)
    {
        this.icon = icon;
    }

    public String geticon() {
        return icon;
    }

    public void setlat(double lat)
    {
        this.lat = lat;
    }

    public double getlat() {
        return lat;
    }

    public void setlng(double lng) {
        this.lng = lng;
    }

    public double getlng() {
        return lng;
    }

    public void setplace(String place) {
        this.place = place;
    }

    public String getplace()
    {
        return place;
    }
}