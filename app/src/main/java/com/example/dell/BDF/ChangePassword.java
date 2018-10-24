package com.example.dell.BDF;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ChangePassword extends Fragment {
    private EditText curpass;
    private EditText newpass;
    private EditText cnfpass;
    private static final String TAG = "ChangePassword";
    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_Changing_Password = Ip+"/BestDoctorFinder/ChangePassword.php";
    ProgressDialog progressDialog;
    private static FragmentManager fragmentManager;
    String cpass;
    String npass;
    String cnfPass;



    public ChangePassword() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Change Password");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        curpass = (EditText)view.findViewById(R.id.curPass);
        newpass = (EditText)view.findViewById(R.id.nPass);
        cnfpass = (EditText)view.findViewById(R.id.cnfPass);

        Button btn = (Button) view.findViewById(R.id.DonePass);

        Button btnclear = (Button) view.findViewById(R.id.clearPass);

        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curpass.setText("");
                newpass.setText("");
                cnfpass.setText("");
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 cpass = curpass.getText().toString();
                 npass = newpass.getText().toString();
                 cnfPass = cnfpass.getText().toString();

                if (!cpass.isEmpty() && !npass.isEmpty() && !cnfPass.isEmpty()) {

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String eId = sharedPreferences.getString(getString(R.string.emailId), "");

                    if(!eId.isEmpty())
                        changePass(eId);
                    else
                        new CustomToast().Show_Toast(getActivity(), view,"Email is Not stored:");

                } else {

                    new CustomToast().Show_Toast(getActivity(), view,
                            "All Fields Must be Filled.");

                }


            }
        });
    }

    private void changePass( final String email) {
        // Tag used to cancel the request
        String cancel_req_tag = "login";
        progressDialog.setMessage("Processing Plz Wait...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_Changing_Password, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "GetPassword Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getContext(),"Your Password is changed successfully.Plz Login Agin With New Password", Toast.LENGTH_LONG).show();

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();

                        Intent i = new Intent(getContext(), MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        getActivity().finish();
                    }
                    else {

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
                Log.e(TAG, "Processing Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("cpass", cpass);
                params.put("npass", npass);
                params.put("cnfPass", cnfPass);
                return params;
            }

        };
        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq,cancel_req_tag);
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
