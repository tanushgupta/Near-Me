package com.maps.tanush.howcloseru;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,GoogleMap.InfoWindowAdapter {

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private boolean isSearched = false;
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;
    private Boolean isGPSEnabled;
    private Boolean isNetworkEnabled;
    private Boolean canGetLocation;
    private double longitude = -34;
    private double latitude = 151;
    private static final float radius = 10;
    private LocationManager locationmanager;
    private Location location;
    private static final long time = 6000;
    private boolean cancheck = true;
    private static final String Name = "name";
    //private GoogleMap myMap;
    private GoogleMap mMap;
    ArrayList<HashMap<String, String>> lstPlaces;
    JSONParser jParser;
    Button btnSearch;
    EditText txtsearch;
    EditText txtradius;
    private List<String> lstjson;
    private List<Place> myplaces;
    myLocInfo myLocation;
    private String searchval = "";
    private int rad = 500;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);//activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        lstjson = new ArrayList<String>();
        myLocation = new myLocInfo();
        myplaces = new ArrayList<Place>();
        myLocation.setMyLocation(getLocation());
        txtsearch = (EditText) findViewById(R.id.txtSearch);
        txtradius = (EditText) findViewById(R.id.txtradius);
        btnSearch = (Button) findViewById(R.id.btnsearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!txtradius.getText().toString().equals(""))
                        rad = Integer.parseInt(txtradius.getText().toString());
                    if(!txtsearch.getText().toString().equals(""))
                    {
                        searchval = txtsearch.getText().toString();
                        ArrayList<Place> arr = new PlacesService().search(searchval, latitude, longitude, rad);
                        isSearched = true;
                        myplaces.clear();
                        mMap.clear();
                        lstjson.clear();
                        onLocationChanged(myLocation.getMyLocation());
                        for (int i = 0; i < arr.size(); i++) {
                            Place p = arr.get(i);
                            createCustomMarker(p);
                            myplaces.add(arr.get(i));
                            try{
                                String jval = createpath(makeURL(p.getlat(),p.getlng()));
                                lstjson.add(jval);
                            }
                            catch(IOException ex){}
                        }
                    }
                } catch (Exception ex) {
                }
                //String url = "https://maps.googleapis.com/maps/api/directions/json?origin=Chicago,IL&destination=Los+Angeles,CA&waypoints=Joplin,MO|Oklahoma+City,OK&key=AIzaSyA520LTRt9rqbz02oMcJqHtbjfsG8ZAltw";
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void createCustomMarker(Place place) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getlat(), place.getlng()))
                        .title(place.getplace() + "\n")
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromURL(place.geticon())))
                        .snippet("Address: " + place.getvicinity())
        );
    }

    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.maps.tanush.howcloseru/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

    public Location getLocation() {
        try {
            requestPermissions(INITIAL_PERMS, LOCATION_REQUEST);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            } else {
                locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
                isGPSEnabled = locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!isGPSEnabled && !isNetworkEnabled) {
                } else {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
                        locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, time, radius, this);
                        if (locationmanager != null) {
                            location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, radius, this);
                            if (locationmanager != null) {
                                location = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            }

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        MarkerOptions myLoc = new MarkerOptions();
        myLoc.position(new LatLng(location.getLatitude(), location.getLongitude()));
        myLoc.title("You are here.");
        myLoc.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mMap.addMarker(myLoc);
        if(!isSearched) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        try {
            for (int i = 0; i < myplaces.size(); i++) {
                Place place = myplaces.get(i);
                MarkerOptions Loc = new MarkerOptions();
                Loc.position(new LatLng(place.getlat(), place.getlng()));
                Loc.title(place.getplace());
                Loc.snippet(place.getvicinity());
                try {
                    Loc.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromURL(place.geticon())));
                } catch (Exception ex) {
                }
                mMap.addMarker(Loc);
            }
        } catch (Exception ex) {
        }
        try{
            for(int i = 0;i<lstjson.size();i++)
            drawPath(lstjson.get(i));
        }
        catch(Exception iex){
        }
        try{
            createCircle(rad);
        }
        catch (Exception ex){}
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Map_Normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.Map_Hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.Map_satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.Map_terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.maps.tanush.howcloseru/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Toast.makeText(this,"Working",Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public String createpath(String url) throws IOException
    {
        JSONParser jParser = new JSONParser();
        String json = jParser.getJSONFromUrl(url);
        //Log.d("Value", json);
        drawPath(json);
        return json;
    }

    public void drawPath(String  result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(20)
                            .color(Color.parseColor("#05b1fb"))//Google maps blue color
                            .geodesic(true)
            );
        }
        catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }
    public String makeURL (double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");
        urlString.append(Double.toString(latitude));
        urlString.append(",");
        urlString.append(Double.toString(longitude));
        urlString.append("&destination=");
        urlString.append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&AIzaSyA520LTRt9rqbz02oMcJqHtbjfsG8ZAltw");
        return urlString.toString();
    }

    public void createCircle(int rad)
    {
        Circle cir = mMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(rad)
                .strokeColor(Color.parseColor("#fb1405"))
        );
    }
}

class myLocInfo
{
    private Location myLocation;

    public void setMyLocation(Location newlocation)
    {
        this.myLocation = newlocation;
    }

    public  Location getMyLocation()
    {
        return myLocation;
    }
}