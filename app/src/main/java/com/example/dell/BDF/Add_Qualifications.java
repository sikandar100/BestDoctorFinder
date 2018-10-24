package com.example.dell.BDF;


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
public class Add_Qualifications extends Fragment {
    private static final String TAG = "AddQualifications";
    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_Add_Qualifications = Ip+"/BestDoctorFinder/AddQualifications.php";
    ProgressDialog progressDialog;

    private Spinner SpinnerDegree;
    private EditText NameofInstitute;
    private String uId;
    private SharedPreferences preferences;
    private Button clrBtn,submitBtn;


    public Add_Qualifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Add Qualifications");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__qualifications, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        uId = preferences.getString("UserId","NotAny");
        Log.d("LetsMeSeeNaPlz", "onViewCreated: " + uId);
        NameofInstitute = (EditText) view.findViewById(R.id.instituteName);
        clrBtn = (Button) view.findViewById(R.id.ClearallBtn);
        submitBtn = (Button) view.findViewById(R.id.AddInfo);
        SpinnerDegree = (Spinner) view.findViewById(R.id.spinnerDegrees);
        ArrayList<String> DegreeList = new ArrayList<String>();

        DegreeList.add("MBBS");
        DegreeList.add("MCM");
        DegreeList.add("MMed");
        DegreeList.add("MSurg");
        DegreeList.add("DCM");
        DegreeList.add("DClinSurg");
        DegreeList.add("DSurg");

        ArrayAdapter<String> degreeAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, DegreeList);
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerDegree.setAdapter(degreeAdapter);

        clrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameofInstitute.setText("");
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });
    }

    private void checkValidation() {

        // Get all edittext texts
        String getInstituteName = NameofInstitute.getText().toString();
        String getDegreename = SpinnerDegree.getSelectedItem().toString();


        // Check if all strings are null or not
        if (getInstituteName.equals("") || getInstituteName.length() == 0 || getDegreename.equals("") || getDegreename.length() == 0)
            new CustomToast().Show_Toast(getActivity(), getView(),"All fields are required.");
            // Else do signup or do your stuff
        else {
            //Toast.makeText(getActivity(), "All Ok till Now", Toast.LENGTH_SHORT).show();
            AddQualifications(getDegreename,getInstituteName);
        }

    }

    private void AddQualifications(final String DegreeName,  final String InsName) {
        // Tag used to cancel the request
        String cancel_req_tag = "AddingQualifications";
        progressDialog.setMessage("Working Plz Wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_Add_Qualifications, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Added Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String degreename = jObj.getJSONObject("degree").getString("name");
                        Toast.makeText(getContext(),  degreename +", Hospital successfully Added!", Toast.LENGTH_SHORT).show();

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
                params.put("Dname", DegreeName);
                params.put("iName", InsName);
                params.put("UserId", uId);

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


}
