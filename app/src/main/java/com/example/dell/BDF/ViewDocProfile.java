package com.example.dell.BDF;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewDocProfile extends Fragment {

    private SharedPreferences preferences;
    private static final String TAG = "ViewDocProfile";
    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_Fetch_Doc_Profile_Data= Ip+"/BestDoctorFinder/FetchDoctorProfileData.php";
    ProgressDialog progressDialog;

    private TextView DocName,cityLocation,DocEmail,ContactNo,degreesName,InsNames,HospitalNamesList,TimingsList,FeeList,DepartList,Rating,RatedBy;
    private ImageView DocPic;
    private String TotalTimings ="",AllDepartsName="", AllHospitalsName="",AllFeeList="",AllDegrees="",AllIns="";


    public ViewDocProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("View Profile");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_doc_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DocName = (TextView)view.findViewById(R.id.DocName);
        cityLocation = (TextView)view.findViewById(R.id.cityLocation);
        DocEmail = (TextView)view.findViewById(R.id.DocEmail);
        ContactNo = (TextView)view.findViewById(R.id.ContactNo);
        degreesName = (TextView)view.findViewById(R.id.degreesName);
        InsNames = (TextView)view.findViewById(R.id.InsNames);
        HospitalNamesList = (TextView)view.findViewById(R.id.HospitalNamesList);
        TimingsList = (TextView)view.findViewById(R.id.TimingsList);
        FeeList = (TextView)view.findViewById(R.id.FeeList);
        DepartList = (TextView)view.findViewById(R.id.DepartmentsList);
        Rating = (TextView)view.findViewById(R.id.Rating);
        RatedBy = (TextView)view.findViewById(R.id.RatedBy);
        DocPic = (ImageView)view.findViewById(R.id.DocPic);


        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String UID = preferences.getString("UserId","NotAny");
        FetchProfileData(UID);


    }

    private void FetchProfileData(final String DId) {
        // Tag used to cancel the request
        String cancel_req_tag = "Fetch";

        progressDialog.setMessage("Working! Plz Wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_Fetch_Doc_Profile_Data, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Fetch Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String Dname = jObj.getString("name");
                        DocName.setText(Dname);
                        String Demail = jObj.getString("email");
                        DocEmail.setText(Demail);
                        String Contact = jObj.getString("mobile");
                        ContactNo.setText(Contact);
                        String City = jObj.getString("location");
                        cityLocation.setText(City);
                        String rating = jObj.getString("Rating");
                        Rating.setText(rating);
                        String ratedBy = jObj.getString("noOfUserRated");
                        RatedBy.setText(ratedBy);
                        String Picture = jObj.getString("pic");
                        //-----------------------Image Displaying ---------------------
                        byte[] decodedByte = Base64.decode(Picture, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
                        DocPic.setImageBitmap(bitmap);
                        //-------------------------------------------------------------
                        JSONArray AllHospitals = jObj.getJSONArray("hospitals");
                        for (int i = 0; i < AllHospitals.length(); i++) {
                            JSONObject innerJObject = AllHospitals.getJSONObject(i);
                            String Hname = innerJObject.getString("name");
                            AllHospitalsName += Hname +"  |  ";
                            String fee = innerJObject.getString("fee");
                            AllFeeList +=  fee + "  |  ";
                            String HoursFrom = innerJObject.getString("HoursFrom");
                            HoursFrom = HoursFrom.substring(0, 5);
                            String HoursTo = innerJObject.getString("HoursTo");
                            HoursTo = HoursTo.substring(0, 5);
                            String Timingeach = HoursFrom + " To "+ HoursTo;
                            TotalTimings += Timingeach +"  |  ";
                            String department = innerJObject.getString("department");
                            AllDepartsName += department + "  |  ";
                        }
                        JSONArray qualifications = jObj.getJSONArray("degrees");
                        for (int i = 0; i < qualifications.length(); i++) {
                            JSONObject innerJObject = qualifications.getJSONObject(i);
                            String Dedreename = innerJObject.getString("name");
                            AllDegrees += Dedreename + "  ,  ";
                            String inst = innerJObject.getString("institute");
                            AllIns += inst + "  |  ";

                        }
                        degreesName.setText(AllDegrees);
                        InsNames.setText(AllIns);

                        HospitalNamesList.setText(AllHospitalsName);
                        FeeList.setText(AllFeeList);
                        TimingsList.setText(TotalTimings);
                        DepartList.setText(AllDepartsName);

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
                params.put("DId", DId);
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
