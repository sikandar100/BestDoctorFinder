package com.example.dell.BDF;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static int PLACE_PICKER_REQUEST = 333;
    private static String TAG = "MAPS_ACTIVITY";
    private LatLng islamabad;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    //private GoogleApiClient mGoogleApiClient;
    private GeoDataClient  mGeoDataClient;
    private static final LatLngBounds lat_lng_bound = new LatLngBounds(new LatLng(33.6844, 73.0479) , new LatLng(33.6844, 73.0479));
    private static final AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
            .setCountry("PK")
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
            .build();
    AutoCompleteTextView mSearchText;
    ImageView mClearTxt;

    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_Fetch_All_Hospitals = Ip+"/BestDoctorFinder/FetchHospitals.php";
    ProgressDialog progressDialog;

    private ArrayList<String> HospitalNamesList = new ArrayList<String>();

    private GoogleMap mMap;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Google Map");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mClearTxt = (ImageView) findViewById(R.id.clear_txt);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            init();
            FetchHospitals();
        } else {
            Toast.makeText(this,"Permission canceled By User!",Toast.LENGTH_SHORT).show();
        }

        // Add a marker in Islamabad and move the camera
        islamabad = new LatLng(33.6844, 73.0479);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(islamabad, 10));
    }


    private void init(){
        mGeoDataClient = Places.getGeoDataClient(this, null);


        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, lat_lng_bound,typeFilter);

        mSearchText.setAdapter(placeAutocompleteAdapter);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == event.ACTION_DOWN
                        || event.getAction() == event.KEYCODE_ENTER)
                {
                    geolocate();

                }

                return false;
            }
        });
        mClearTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
            }
        });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String Hname = marker.getTitle();
                Intent intent = new Intent(MapsActivity.this, DepartmentViewOfUsers.class);
                intent.putExtra("HospitalName", Hname);
                startActivity(intent);
            }
        });
    }

    private void geolocate()
    {

        String searchString = mSearchText.getText().toString();
        Log.d(TAG, "geolocate check " + searchString);
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(list.size()>0)
        {
            Address address = list.get(0);
            Log.d(TAG, "geolocate location "+ address.toString());
            String[] parts = searchString.split(",");
            String HnameSearched = parts[0];
            if(HospitalNamesList.contains(HnameSearched)) {
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(HnameSearched);
                mMap.addMarker(markerOptions);
            }
            else
            {
                Toast.makeText(this,"Sorry Searched Location/Hospital in not in The DataBase!Try Search Hospitaal in Islamabad",Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void FetchHospitals() {
        // Tag used to cancel the request
        String cancel_req_tag = "Fetch";
        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage("Working! Plz Wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_Fetch_All_Hospitals, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Fetch Response: " + response.toString());
                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d(TAG, "Jason Response: " + jObj);
                    if (!error) {
                        //String user = jObj.getJSONObject("user").getString("name");
                        Toast.makeText(getApplicationContext(), "You Have successfully Fetched All Hospitals" , Toast.LENGTH_SHORT).show();
                        if (jObj != null) {
                            for (int i=0;i<(jObj.length()-1);i++){
                                HospitalNamesList.add(jObj.getJSONObject("hospital"+i).getString("name"));
                                String tempName = jObj.getJSONObject("hospital"+i).getString("name");
                                double lat = Double.parseDouble(jObj.getJSONObject("hospital"+i).getString("Latitude"));
                                double lon = Double.parseDouble(jObj.getJSONObject("hospital"+i).getString("Longitude"));
                                LatLng latLng = new LatLng(lat,lon);
                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(tempName);
                                mMap.addMarker(markerOptions);
                            }
                        }

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Fetched Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to the given url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


}
