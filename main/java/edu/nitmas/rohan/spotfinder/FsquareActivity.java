package edu.nitmas.rohan.spotfinder;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FsquareActivity extends AppCompatActivity {


    GoogleMap mGoogleMap;
    LatLng search, mark, mark1;
    Spinner mSprPlaceType;
    String[] mPlaceType = null;
    String[] mPlaceTypeName = null;
    private int radius;
    Dialog dialog;
    String location, placeid;
    Context mContext;
    EditText atvPlaces;
    ListView l;
    SimpleAdapter idAdapter, adapter1;
    PlacesTask1 placesTask1;
    ParserTask1 parserTask1;
    String sear;
    EditText location_tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fsquare);
        mContext = this;
        checkFirstRun();
        mPlaceType = getResources().getStringArray(R.array.place_type1);
        mPlaceTypeName = getResources().getStringArray(R.array.place_type_name1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mPlaceTypeName);
        mSprPlaceType = (Spinner) findViewById(R.id.spr_place_type);
        mSprPlaceType.setAdapter(adapter);

        Button btnFind, btnFindMe;
        btnFind = (Button) findViewById(R.id.btn_find);
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {
            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mGoogleMap = fragment.getMap();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }

            final Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            } else {
                Toast.makeText(FsquareActivity.this, "Internet Connection Too Slow or Poor GPS Coverage", Toast.LENGTH_SHORT).show();
            }

            btnFind.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int selectedPosition = mSprPlaceType.getSelectedItemPosition();
                    String type = mPlaceType[selectedPosition];
                    loadpreferences();
                    StringBuilder sb = new StringBuilder("https://api.foursquare.com/v2/venues/search?");
                    sb.append("client_id=0JXRU5DH34H0ZOOHQ1GX531GJSB2LMARUJBS442P4LOUFB13&client_secret=1XWM1S0BB5RK3JQPCAYRU4I0R03E24UTEOEM4KON4IYNCN2T&v=20130815");
                    sb.append("&ll=" + search.latitude + "," + search.longitude + "&categoryId=" + type);
                    sb.append("&radius=" + radius);
                    System.out.println(sb.toString());
                    Log.d("Fsqaure", sb.toString());
                    new downloadResult(FsquareActivity.this).execute(sb.toString());
                }
            });

            btnFindMe = (Button) findViewById(R.id.btn_findme);
            btnFindMe.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onLocationChanged(location);
                }
            });
        }


        final EditText location_tf = (EditText) findViewById(R.id.TFaddress);
        location_tf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setContentView(R.layout.dialog_location);
                dialog.getWindow().getAttributes();
                dialog.show();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                atvPlaces = (EditText) dialog.findViewById(R.id.atv_places);
                l = (ListView) dialog.findViewById(R.id.list_cities);
                l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        HashMap<String, Object> obj = (HashMap<String, Object>) adapter1.getItem(position);
                        location = (String) obj.get("description");


                        HashMap<String, Object> idObj = (HashMap<String, Object>) idAdapter.getItem(position);
                        placeid = (String) idObj.get("_id");
                        System.out.println("place id is" + placeid);


                        dialog.cancel();
                        location_tf.setText(location);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    }
                });
                atvPlaces.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (atvPlaces.getText().length() > 0) {
                            atvPlaces.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, R.drawable.ic_navigation_cancel, 0);
                            placesTask1 = new PlacesTask1();
                            placesTask1.execute(s.toString());
                            atvPlaces.setOnTouchListener(new View.OnTouchListener() {

                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    atvPlaces.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, R.drawable.ic_navigation_cancel, 0);
                                    final int DRAWABLE_RIGHT = 2;
                                    if (event.getAction() == MotionEvent.ACTION_UP) {
                                        if (event.getRawX() >= (atvPlaces.getRight() - atvPlaces.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                            atvPlaces.setText("");
                                            return true;
                                        }
                                    }
                                    return false;
                                }
                            });

                        } else {
                            l.setAdapter(null);
                            atvPlaces.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, 0, 0);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


            }
        });

    }

    public class downloadResult extends AsyncTask<String, Void, List<Venue>> {

        Context context;

        public downloadResult(Context context) {
            this.context = context;

        }

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected List<Venue> doInBackground(String... params) {

            List<Venue> search;
            search = new ArrayList<Venue>();
            String data;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    data = convertInputStreamtoString(connection
                            .getInputStream());
                    search = parseJSON(data);

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return search;
        }

        protected void onPostExecute(List<Venue> result) {
            super.onPostExecute(result);
            mGoogleMap.clear();
            mGoogleMap.addMarker(new MarkerOptions().position(mark).title("I'm Here"));
            if (sear != null) {
                mGoogleMap.addMarker(new MarkerOptions().position(mark1).title("" + location_tf.getText()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }

            if (result.size() == 0) {
                Log.d("Fsqaure", "Result List: Null");
                AlertDialog.Builder builder = new AlertDialog.Builder(FsquareActivity.this);
                builder.setTitle("ALERT!");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("No nearby places available, please increase your search radius in settings");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert1 = builder.create();
                alert1.show();
            } else {
                Log.d("Fsquare", "Result List: Fetched Size: " + result.size());
                for (int i = 0; i < result.size(); i++) {
                    Log.d("Fsquare", "ADDING MARKER: " + i);
                    MarkerOptions markerOptions = new MarkerOptions();
                    Venue tmp = result.get(i);
                    double lat = tmp.getLat();
                    double lng = tmp.getLng();
                    String name = tmp.getname();
                    String address = tmp.getAddress();
                    LatLng latLng = new LatLng(lat, lng);

                    markerOptions.position(latLng);
                    markerOptions.title(name + ", " + address);
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    ;
                    mGoogleMap.addMarker(markerOptions);

                }
                Toast.makeText(FsquareActivity.this, "Spots Fetched!", Toast.LENGTH_SHORT).show();

            }

        }

        private List<Venue> parseJSON(String response) throws JSONException {
            List<Venue> tmp = new ArrayList<Venue>();
            String id, name, address;
            double lat, lng;

            JSONObject rootObject = new JSONObject(response);
            JSONObject responseObject = rootObject.getJSONObject("response");
            JSONArray itemsArray = responseObject.getJSONArray("venues");
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject i_itemObject = itemsArray.getJSONObject(i);
                JSONObject locationObject = i_itemObject
                        .getJSONObject("location");
                id = i_itemObject.getString("id");
                name = i_itemObject.getString("name");
                if (locationObject.has("address")) {
                    address = locationObject.getString("address");
                } else {
                    address = "N.A.";
                }
                lat = locationObject.getDouble("lat");
                lng = locationObject.getDouble("lng");
                tmp.add(new Venue(id, name, address, new LatLng(lat, lng)));
            }

            return tmp;
        }

        private String convertInputStreamtoString(InputStream input)
                throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    input));
            StringBuilder builder = new StringBuilder();
            String line = new String();
            while ((line = reader.readLine()) != null) {
                builder = builder.append(line);
            }
            return builder.toString();
        }

    }


    public void onSearch(View view) {
        mGoogleMap.clear();
        location_tf = (EditText) findViewById(R.id.TFaddress);

        String location = location_tf.getText().toString();
        sear = location;
        if (location.length() == 0){

            AlertDialog.Builder builder = new AlertDialog.Builder(FsquareActivity.this);
            builder.setTitle("ALERT!");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage("Enter a location");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alert1 = builder.create();
            alert1.show();
        }

        else {
            List<Address> addressList = null;

            if (location != null || location.equals("")) {
                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addressList.size() <= 0) {
                    Toast.makeText(FsquareActivity.this, "Enter a valid location", Toast.LENGTH_LONG).show();
                    location_tf.setText("");
                } else {
                    Address address = addressList.get(0);
                    search = new LatLng(address.getLatitude(), address.getLongitude());
                    mark1 = search;
                    mGoogleMap.addMarker(new MarkerOptions().position(search).title("" + location_tf.getText()).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(search));
                }
            }
        }
    }

    public void onLocationChanged(Location location) {
        search = new LatLng(location.getLatitude(), location.getLongitude());
        mark = search;
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(search));
        mGoogleMap.addMarker(new MarkerOptions().position(search).title("I'm Here"));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    void loadpreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        radius = Integer.parseInt(prefs.getString("radius", "5000"));
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();


            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception in  url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    public class PlacesTask1 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {

            String data = "";
            String key = "key=AIzaSyC2QOXdAX3q4Y0pJ6wBESxmdiobjVDZIkU";
            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            String parameters = input + "&" + key;

            String output = "json";

            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

            try {

                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parserTask1 = new ParserTask1();
            parserTask1.execute(result);


        }
    }


    public class ParserTask1 extends AsyncTask<String, Integer, List<HashMap<String, String>>> {


        JSONObject jObject;

        @Override
        public List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser1 placeJsonParser1 = new PlaceJSONParser1();

            try {
                jObject = new JSONObject(jsonData[0]);


                places = placeJsonParser1.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }


        public void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};
            adapter1 = new SimpleAdapter(getBaseContext(), result, R.layout.list_item, from, to);


            String[] ids = new String[]{"_id"};
            int[] idTo = new int[]{android.R.id.text2};
            idAdapter = new SimpleAdapter(getBaseContext(), result, R.layout.list_item, ids, idTo);
            idAdapter.notifyDataSetChanged();
            l.setAdapter(adapter1);
            adapter1.notifyDataSetChanged();


        }
    }

    public void checkFirstRun() {
        boolean isFirstRun2 = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun2", true);
        if (isFirstRun2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FsquareActivity.this);
            builder.setTitle("INSTRUCTIONS");
            builder.setMessage("After searching for any spot then zoom in and tap on the marker and you will get options for getting directions with the help of Google map. Simply click it and use your Gps to reach the place with information of means of transport, shortest route and exact directions.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert2 = builder.create();
            alert2.show();
            builder.setTitle("INSTRUCTIONS");
            builder.setMessage("This screen shows the current location by default and you can search list of spots by using Find option. You can also do these searching from any other location apart from current location and also change the radius limit for search optimization. The red colored marker shows the current location, blue the searched location and orange the result of spots searched. The Me tab reverts back you to current location.");
            builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert1 = builder.create();
            alert1.show();


            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun2", false).apply();
        }
    }

}
