package com.example.dell.BDF;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Add_Department extends Fragment {

    private static EditText Dname;
    private static Button BtnSubmit, Btnclear;
    private static Spinner spinnerHospitals;

    private static final String TAG = "AddDepartment";
    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_Fetch_All_Hospitals = Ip+"/BestDoctorFinder/FetchHospitals.php";
    private static final String URL_FOR_Add_Hospital = Ip+"/BestDoctorFinder/AddDepartment.php";
    ProgressDialog progressDialog;

    private ArrayList<String> HospitalArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;


    public Add_Department() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Add Department");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__department, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerHospitals = (Spinner) view.findViewById(R.id.spinnerHospitalList);
        Btnclear = (Button) view.findViewById(R.id.ClearBtn);
        BtnSubmit = (Button) view.findViewById(R.id.SubmitBtn);
        Dname = (EditText) view.findViewById(R.id.DepartmentName);

        FetchHospitals();

        Btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dname.setText("");
            }
        });

        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkValidation();

            }
        });


    }

    private void FetchHospitals() {
        // Tag used to cancel the request
        String cancel_req_tag = "Fetch";

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
                        Toast.makeText(getContext(), "You Have successfully Fetched All Hospitals" , Toast.LENGTH_SHORT).show();
                        if (jObj != null) {
                            for (int i=0;i<(jObj.length()-1);i++){
                                HospitalArrayList.add(jObj.getJSONObject("hospital"+i).getString("name"));
                            }
                        }
                        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,HospitalArrayList);
                        spinnerHospitals.setAdapter(arrayAdapter);

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
        String getdepartmentName = Dname.getText().toString();
        String getHospitalname = spinnerHospitals.getSelectedItem().toString();
        Log.d(TAG, "Spinner Response: " + getHospitalname);

        // Check if all strings are null or not
        if (getdepartmentName.equals("") || getdepartmentName.length() == 0 || getHospitalname.equals("") || getHospitalname.length() == 0)
            new CustomToast().Show_Toast(getActivity(), getView(),"All fields are required.");
            // Else do signup or do your stuff
        else {
            //Toast.makeText(getActivity(), "All Ok till Now", Toast.LENGTH_SHORT).show();
            AddDepartment(getdepartmentName,getHospitalname);
        }

    }

    private void AddDepartment(final String name,  final String hospital) {
        // Tag used to cancel the request
        String cancel_req_tag = "AddingDepartment";

        progressDialog.setMessage("Working Plz Wait ...");
        showDialog();

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
                        String Department = jObj.getJSONObject("DepartmentName").getString("name");
                        Toast.makeText(getContext(),  Department +", Hospital successfully Added!", Toast.LENGTH_SHORT).show();

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
                Log.e(TAG, "Adding Error: " + error.getMessage());
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
                params.put("hospital", hospital);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
    }

}
