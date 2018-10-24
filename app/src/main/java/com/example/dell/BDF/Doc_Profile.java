package com.example.dell.BDF;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Doc_Profile extends Fragment {
    private Button selectImg,SubmitBtn,clrBtn,BtnFetch;
    private EditText chkUpFee,desc,HrsFrom,MinFrom,HrsTo,MinTo;
    private Spinner HospitalSelected,DepartmentSelected;
    private Bitmap imgUser;
    String EncodedImage = "";

    private SharedPreferences preferences;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "AddDocProfile";
    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_Fetch_All_Hospitals = Ip+"/BestDoctorFinder/FetchHospitals.php";
    private static final String URL_FOR_All_Departments_fetch = Ip+"/BestDoctorFinder/AllDepartmentFetch.php";
    private static final String URL_FOR_AddDoctorProfile = Ip+"/BestDoctorFinder/AddDoctorProfile.php";
    ProgressDialog progressDialog;

    private ArrayList<String> HospitalArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    private ArrayList<String> DepartmentArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter1;

    public Doc_Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Add Profile");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doc__profile, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data !=null)
        {
            Uri SelectedImg = data.getData();

                try {
                    imgUser = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), SelectedImg);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imgUser.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageInByte = stream.toByteArray();
                    long lengthbmp = imageInByte.length;
                    Log.d(TAG, "Result ok: "+lengthbmp);
                    if(lengthbmp>90000)
                    {
                        new CustomToast().Show_Toast(getActivity(), getView(),"Image Size is Greater Than 64Kb");
                    }
                    else
                    {
                        EncodedImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
                        Toast.makeText(getContext(),"Image has been selected!",Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectImg = (Button)view.findViewById(R.id.ImgSelection);
        SubmitBtn = (Button)view.findViewById(R.id.SubmitForm);
        clrBtn = (Button)view.findViewById(R.id.ClearForm);
        chkUpFee = (EditText)view.findViewById(R.id.chkFee);
        desc = (EditText)view.findViewById(R.id.desc);
        HrsFrom = (EditText)view.findViewById(R.id.HrsFrom);
        MinFrom = (EditText)view.findViewById(R.id.MinFrom);
        HrsTo = (EditText)view.findViewById(R.id.HrsTo);
        MinTo = (EditText)view.findViewById(R.id.MinTo);
        HospitalSelected = (Spinner)view.findViewById(R.id.spinnerHospitalSelect);
        DepartmentSelected = (Spinner)view.findViewById(R.id.spinnerDepartmentSelect);

        FetchHospitals();

        clrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkUpFee.setText("");
                desc.setText("");
                HrsFrom.setText("");
                MinFrom.setText("");
                HrsTo.setText("");
                MinTo.setText("");
            }
        });

        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }

        });

        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallryIntent,RESULT_LOAD_IMAGE);

            }
        });


        HospitalSelected.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String getHospitalname = HospitalSelected.getSelectedItem().toString();
                if(DepartmentArrayList.size()>0) {
                    DepartmentArrayList.clear();
                    arrayAdapter1 = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,DepartmentArrayList);
                    DepartmentSelected.setAdapter(arrayAdapter1);
                }
                FetchDepartments(getHospitalname.trim());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void checkValidation() {

        String getchkUpFee = chkUpFee.getText().toString();
        String getdesc =desc.getText().toString();
        String getHrsFrom = HrsFrom.getText().toString();
        String getMinFrom = MinFrom.getText().toString();
        String getHrsTo =HrsTo.getText().toString();
        String getMinTo = MinTo.getText().toString();

        if (getchkUpFee.equals("") || getchkUpFee.length() == 0
                || getdesc.equals("") || getdesc.length() == 0
                || getHrsFrom.equals("") || getHrsFrom.length() == 0
                || getMinFrom.equals("") || getMinFrom.length() == 0
                || getHrsTo.equals("") || getHrsTo.length() == 0
                || getMinTo.equals("") || getMinTo.length() == 0)
        {
            new CustomToast().Show_Toast(getActivity(), getView(),
                    "All fields are required.");
        }
        else {

            if(EncodedImage.equals(""))
            {
                new CustomToast().Show_Toast(getActivity(), getView(), "Profile Picture is not Selected");
            }
            else
            {
                Random rand = new Random();
                int  Rnum = rand.nextInt(10000) + 1;
                preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String s = preferences.getString("EmailId","NotAny");
                String UID = preferences.getString("UserId","NotAny");
                String upToNCharacters = s.substring(0, Math.min(s.length(), 4));
                String ImgName = upToNCharacters + Rnum;
                Log.d(TAG, "NameCheck: "+ ImgName);
                String timeFrom = getHrsFrom+":"+getMinFrom;
                String timeTo=getHrsTo+":"+getMinTo;
                String Hname = HospitalSelected.getSelectedItem().toString();
                String Dname = DepartmentSelected.getSelectedItem().toString();
                //------------------------------Server call-------------------------------------
                AddProfile(EncodedImage,ImgName,getchkUpFee,getdesc,timeFrom,timeTo,UID,Hname,Dname);
            }

        }

    }

    private void AddProfile(final String Image,  final String ImgName, final String chkUpFee,
                                final String desc, final String TimeFrom, final String TimeTo,
                                final String uId, final String Hname, final String Dname) {
        // Tag used to cancel the request
        String cancel_req_tag = "AddProfileDoctor";

        progressDialog.setMessage("Adding Profile Plz Wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_AddDoctorProfile, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Adding Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String time = jObj.getString("testout");
                    Log.d(TAG, "timeonResponse: "+ time);
                    if (!error) {
                        Toast.makeText(getContext(), " You Have successfully Add/Updated Your Profile!", Toast.LENGTH_SHORT).show();

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
                params.put("Image", Image);
                params.put("ImgName", ImgName);
                params.put("chkUpFee", chkUpFee);
                params.put("desc", desc);
                params.put("TimeFrom", TimeFrom);
                params.put("TimeTo", TimeTo);
                params.put("uId", uId);
                params.put("Hname", Hname);
                params.put("Dname", Dname);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
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
                        HospitalSelected.setAdapter(arrayAdapter);

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

    private void FetchDepartments(final String Hname) {
        // Tag used to cancel the request
        String cancel_req_tag = "Fetch";
        progressDialog.setMessage("Working! Plz Wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_All_Departments_fetch, new Response.Listener<String>() {

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
                        Toast.makeText(getContext(), "You Have successfully Fetched All Departments" , Toast.LENGTH_SHORT).show();
                        if (jObj != null) {
                            for (int i=0;i<(jObj.length()-1);i++){
                                DepartmentArrayList.add(jObj.getJSONObject("department"+i).getString("name"));
                            }
                        }
                        arrayAdapter1 = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,DepartmentArrayList);
                        DepartmentSelected.setAdapter(arrayAdapter1);

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
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("Hname", Hname);
                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
    }
}
