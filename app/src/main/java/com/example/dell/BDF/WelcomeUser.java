package com.example.dell.BDF;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeUser extends Fragment {

    private static final String TAG = "RatingCheck";
    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_ratingDoctor = Ip+"/BestDoctorFinder/ratingDoctor.php";
    private static final String URL_FOR_checkRatingRequired = Ip+"/BestDoctorFinder/checkRatingRequired.php";
    ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private String DocId;
    private float rated;
    private Dialog dialog;



    public WelcomeUser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Welcome To Best Doctor Finder");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checlRatingRequired();
    }

    private void cutomratingbar (String msg)
    {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_ratingbar);

        final RatingBar ratingBar = (RatingBar)dialog.findViewById(R.id.ratingBar);
        TextView textView = (TextView) dialog.findViewById(R.id.textView11);

        ratingBar.setStepSize(1);
       // ratingBar.setRating(0);
        textView.setText(msg);

        dialog.show();

        Button SubmitRating = (Button)dialog.findViewById(R.id.Submitbtn);

        SubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rated = ratingBar.getRating();
                ratingDoctor();
                dialog.dismiss();
            }
        });

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


    private void checlRatingRequired() {
        // Tag used to cancel the request
        String cancel_req_tag = "checking";

        progressDialog.setMessage("Working! Plz Wait ...");
        showDialog();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String Uid = preferences.getString("UserId","NotAny");


        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_checkRatingRequired, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "checking Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d(TAG, "Jason Response: " + jObj);
                    if (!error) {
                        DocId = jObj.getJSONObject("user").getString("DocId");
                        cutomratingbar("Rate Your Doctor Now");

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
                Log.e(TAG, "checking Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status","3");
                params.put("Uid",Uid);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

    private void ratingDoctor() {
        // Tag used to cancel the request
        String cancel_req_tag = "rating";

        progressDialog.setMessage("Working! Plz Wait ...");
        showDialog();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String Uid = preferences.getString("UserId","NotAny");


        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_ratingDoctor, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "rating Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d(TAG, "Jason Response: " + jObj);
                    if (!error) {
                        Toast.makeText(getContext(),"Successfully rated",Toast.LENGTH_SHORT).show();

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
                Log.e(TAG, "checking Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status","0");
                params.put("Uid",Uid);
                params.put("DocId",DocId);
                params.put("rating", String.valueOf(rated).toString().trim());
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
    }
}
