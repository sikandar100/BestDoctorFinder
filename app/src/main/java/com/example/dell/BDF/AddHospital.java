package com.example.dell.BDF;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddHospital extends Fragment {

    private static EditText Hname;
    private static Button BtnSubmit, Btnclear;
    private static Spinner spinnerCites;

    private static final String TAG = "AddHospital";
    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_Fetch_All_Cities = Ip + "/BestDoctorFinder/FetchCities.php";
    private static final String URL_FOR_Add_Hospital = Ip + "/BestDoctorFinder/AddHospital.php";
    ProgressDialog progressDialog;
    private static final int PERMISSIONS_REQUEST = 123;

    private ArrayList<String> CityArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;


    public AddHospital() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Add Hospital");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_hospital, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinnerCites = (Spinner) view.findViewById(R.id.spinnerCityList);
        Btnclear = (Button) view.findViewById(R.id.ClearBtn);
        BtnSubmit = (Button) view.findViewById(R.id.SubmitBtn);
        Hname = (EditText) view.findViewById(R.id.hospitalName);

        FetchCities();

        Btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hname.setText("");
            }
        });


        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

    }

    private void FetchCities() {
        // Tag used to cancel the request
        String cancel_req_tag = "Fetch";

        progressDialog.setMessage("Working! Plz Wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_Fetch_All_Cities, new Response.Listener<String>() {

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
                        Toast.makeText(getContext(), "You Have successfully Fetched All Cities", Toast.LENGTH_SHORT).show();
                        if (jObj != null) {
                            for (int i = 0; i < (jObj.length() - 1); i++) {
                                CityArrayList.add(jObj.getJSONObject("user" + i).getString("name"));
                            }
                        }
                        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, CityArrayList);
                        spinnerCites.setAdapter(arrayAdapter);

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
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
                Toast.makeText(getContext(),
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
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


    private void checkValidation() {

        // Get all edittext texts
        String getHospitalName = Hname.getText().toString();
        String getCityname = spinnerCites.getSelectedItem().toString();
        Log.d(TAG, "Spinner Response: " + getCityname);

        // Check if all strings are null or not
        if (getHospitalName.equals("") || getHospitalName.length() == 0 || getCityname.equals("") || getCityname.length() == 0)
            new CustomToast().Show_Toast(getActivity(), getView(), "All fields are required.");
            // Else do signup or do your stuff
        else {
            //Toast.makeText(getActivity(), "All Ok till Now", Toast.LENGTH_SHORT).show();
            Location location = getMyLocation();
            final double lati = location.getLatitude();
            final double longi = location.getLongitude();
            AddHospital(getHospitalName, getCityname, lati, longi);
        }

    }

    private void AddHospital(final String name, final String city, final double lati, final double longi) {
        // Tag used to cancel the request
        String cancel_req_tag = "AddingDoctor";

        progressDialog.setMessage("Working Plz Wait ...");
        showDialog();
        final String Latitude = Double.toString(lati);
        final String Longitude = Double.toString(longi);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_Add_Hospital, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Added Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String hospitalname = jObj.getJSONObject("hospitalname").getString("name");
                        Toast.makeText(getContext(), hospitalname + ", Hospital successfully Added!", Toast.LENGTH_SHORT).show();

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Addind Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("city", city);
                params.put("Latitude", Latitude);
                params.put("Longitude", Longitude);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    //-------------------------------Permissions For Locations-------------------------------

    @SuppressLint("MissingPermission")
    public Location getMyLocation() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE); // Get location from GPS if it's available

       Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            myLocation = lm.getLastKnownLocation(provider);
        }
        return myLocation;
    }


}
