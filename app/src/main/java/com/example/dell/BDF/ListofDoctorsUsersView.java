package com.example.dell.BDF;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListofDoctorsUsersView extends AppCompatActivity {

    private SharedPreferences preferences;
    private static final String TAG = "UserViewDoctors";
    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_Fetch_ALL_Doc_Profile_Data = Ip+"/BestDoctorFinder/DoctorsListViewofUser.php";
    private static final String URL_FOR_Send_Request_To_Doctor = Ip+"/BestDoctorFinder/SendRequestToDoctor.php";
    ProgressDialog progressDialog;
    private EditText etSearch;
    private ListView lvDoctors;
    private String Hname,DeptId;
    private ArrayList<Doctors> DoctorsArrayList = new ArrayList<Doctors>();
    private MyAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listof_doctors_users_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("List of All Doctors");
        progressDialog = new ProgressDialog(ListofDoctorsUsersView.this);
        progressDialog.setCancelable(false);

        etSearch = (EditText)findViewById(R.id.searchBar);
        lvDoctors = (ListView)findViewById(R.id.ListofDocs);

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                adapter1.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Bundle extras = getIntent().getExtras();
        Hname= extras.getString("HospitalName");
        DeptId = extras.getString("DeptId");

        FetchDocsData();
    }

    private void FetchDocsData() {
        // Tag used to cancel the request
        String cancel_req_tag = "Fetch";

        progressDialog.setMessage("Working! Plz Wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_Fetch_ALL_Doc_Profile_Data, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Fetch Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray AllDocsArray = new JSONArray(response);
                    /*JSONObject jObj = AllDocsArray.getJSONObject(0);
                    boolean error = jObj.getBoolean("error");*/
                    if (AllDocsArray.length()>0) {
                        for (int i = 0; i < AllDocsArray.length(); i++) {
                            JSONObject innerJObject = AllDocsArray.getJSONObject(i);
                            String id = innerJObject.getString("id");
                            Log.d(TAG, "innerObject: " + id);
                            String name = innerJObject.getString("Name");
                            Log.d(TAG, "innerObject: " + name);
                            String Email = innerJObject.getString("Email");
                            Log.d(TAG, "innerObject: " + Email);
                            String Rating = innerJObject.getString("Rating");
                            Log.d(TAG, "innerObject: " + Rating);
                            String noOfUserRated = innerJObject.getString("noOfUserRated");
                            Log.d(TAG, "innerObject: " + noOfUserRated);
                            String checkUpFee = innerJObject.getString("checkUpfee");
                            Log.d(TAG, "innerObject: " + checkUpFee);
                            String WorkingHrs = innerJObject.getString("WorkingHours");
                            Log.d(TAG, "innerObject: " + WorkingHrs);

                            DoctorsArrayList.add(new Doctors(id,name,Email,Rating,noOfUserRated,checkUpFee,WorkingHrs));

                        }
                        adapter1 = new MyAdapter(getApplicationContext(), DoctorsArrayList);
                        lvDoctors.setAdapter(adapter1);


                    } else {

                        Toast.makeText(getApplicationContext(),
                                "There Exists No 'Doctor' in this Department", Toast.LENGTH_LONG).show();
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
                params.put("Hname", Hname);
                params.put("Did", DeptId);
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

    public class Doctors {
        public String DocId;
        public String DocName;
        public String DocEmail;
        public String DocRating;
        public String noOfUsers;
        public String DocFee;
        public String workingHrs;

        public Doctors(String docId, String docName, String docEmail, String docRating, String noOfUsers, String docFee, String workingHrs) {
            DocId = docId;
            DocName = docName;
            DocEmail = docEmail;
            DocRating = docRating;
            this.noOfUsers = noOfUsers;
            DocFee = docFee;
            this.workingHrs = workingHrs;
        }

        public String getDocId() {
            return DocId;
        }

        public String getDocName() {
            return DocName;
        }

        public String getDocEmail() {
            return DocEmail;
        }

        public String getDocRating() {
            return DocRating;
        }

        public String getNoOfUsers() {
            return noOfUsers;
        }

        public String getDocFee() {
            return DocFee;
        }

        public String getWorkingHrs() {
            return workingHrs;
        }

        public void setDocId(String docId) {
            DocId = docId;
        }

        public void setDocName(String docName) {
            DocName = docName;
        }

        public void setDocEmail(String docEmail) {
            DocEmail = docEmail;
        }

        public void setDocRating(String docRating) {
            DocRating = docRating;
        }

        public void setNoOfUsers(String noOfUsers) {
            this.noOfUsers = noOfUsers;
        }

        public void setDocFee(String docFee) {
            DocFee = docFee;
        }

        public void setWorkingHrs(String workingHrs) {
            this.workingHrs = workingHrs;
        }
    }

    public class MyAdapter extends BaseAdapter implements Filterable {

        private ArrayList<Doctors> mOriginalValues; // Original Values
        private ArrayList<Doctors> mDisplayedValues;    // Values to be displayed
        LayoutInflater inflater;

        public MyAdapter(Context context, ArrayList<Doctors> mProductArrayList) {
            this.mOriginalValues = mProductArrayList;
            this.mDisplayedValues = mProductArrayList;
            inflater = LayoutInflater.from(context);
        }

        public void EmptyTheArray()
        {

            for(int i = mDisplayedValues.size()-1 ; i >= 0; i--){
                mDisplayedValues.remove(mDisplayedValues.get(i));
            }
        }

        @Override
        public int getCount() {
            return mDisplayedValues.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            RelativeLayout drListContainer;
            TextView nameTag,emailTag,ratingTag,ratedByTag,checkupfeeTag,wrkinghoursTag,
                    nameTxt,emailTxt,ratingTxt,ratedByTxt,checkupfeeTxt,workinghrsTxt;
            Button requestBtn;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.user_view_of_doc_profile, null);
                holder.drListContainer = (RelativeLayout)convertView.findViewById(R.id.drListContainer);
                holder.nameTag = (TextView) convertView.findViewById(R.id.nameTag);
                holder.emailTag = (TextView) convertView.findViewById(R.id.emailTag);
                holder.ratingTag = (TextView) convertView.findViewById(R.id.ratingTag);
                holder.ratedByTag = (TextView) convertView.findViewById(R.id.ratedByTag);
                holder.checkupfeeTag = (TextView) convertView.findViewById(R.id.checkupfeeTag);
                holder.wrkinghoursTag = (TextView) convertView.findViewById(R.id.wrkinghoursTag);
                holder.nameTxt = (TextView) convertView.findViewById(R.id.nameTxt);
                holder.emailTxt = (TextView) convertView.findViewById(R.id.emailTxt);
                holder.ratingTxt = (TextView) convertView.findViewById(R.id.ratingTxt);
                holder.ratedByTxt = (TextView) convertView.findViewById(R.id.ratedByTxt);
                holder.checkupfeeTxt = (TextView) convertView.findViewById(R.id.checkupfeeTxt);
                holder.workinghrsTxt = (TextView) convertView.findViewById(R.id.workinghrsTxt);
                holder.requestBtn = (Button) convertView.findViewById(R.id.requestBtn);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.nameTxt.setText(mDisplayedValues.get(position).DocName);
            holder.emailTxt.setText(mDisplayedValues.get(position).DocEmail);
            holder.ratingTxt.setText(mDisplayedValues.get(position).DocRating);
            holder.ratedByTxt.setText(mDisplayedValues.get(position).noOfUsers);
            holder.checkupfeeTxt.setText(mDisplayedValues.get(position).DocFee);
            holder.workinghrsTxt.setText(mDisplayedValues.get(position).workingHrs);


            holder.requestBtn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    //-----------------------------------------------------------------------------------
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListofDoctorsUsersView.this);

                    builder.setCancelable(true);
                    builder.setTitle("Confirmation!");
                    builder.setMessage("Do You Really Want to Send Request to This Doctor?");

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //Toast.makeText(getApplicationContext(), mDisplayedValues.get(position).DocId, Toast.LENGTH_SHORT).show();
                            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            String UserId = preferences.getString("UserId","NotAny");
                            String DocId = mDisplayedValues.get(position).DocId;
                            DoctorsArrayList.remove(mDisplayedValues.get(position));
                            SendRequestToDoctor(UserId,DocId);
                        }
                    });
                    builder.show();

                    //------------------------------------------------------------------
                }
            });

            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,FilterResults results) {

                    mDisplayedValues = (ArrayList<Doctors>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<Doctors> FilteredArrList = new ArrayList<Doctors>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<Doctors>(mDisplayedValues); // saves the original data in mOriginalValues
                    }

                    /********
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            String data = mOriginalValues.get(i).DocEmail;
                            if (data.toLowerCase().startsWith(constraint.toString())) {
                                FilteredArrList.add(new Doctors(mOriginalValues.get(i).DocId,mOriginalValues.get(i).DocName,mOriginalValues.get(i).DocEmail,mOriginalValues.get(i).DocRating,mOriginalValues.get(i).noOfUsers,mOriginalValues.get(i).DocFee,mOriginalValues.get(i).workingHrs));
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }

    }

    private void SendRequestToDoctor(final String UserId,final String DocId) {
        // Tag used to cancel the request
        String cancel_req_tag = "SendRequestToDoctor";

        progressDialog.setMessage("Working Plz Wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_Send_Request_To_Doctor, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getApplicationContext()," Your Request Has been Successfully Sent!", Toast.LENGTH_SHORT).show();
                        showDialog();
                        adapter1 = new MyAdapter(getApplicationContext(), DoctorsArrayList);
                        lvDoctors.setAdapter(adapter1);
                        hideDialog();

                    } else {
                        showDialog();
                        adapter1 = new MyAdapter(getApplicationContext(), DoctorsArrayList);
                        lvDoctors.setAdapter(adapter1);
                        hideDialog();

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
                Log.e(TAG, "Adding Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserId", UserId);
                params.put("DocId", DocId);
                params.put("status", "1");

                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
    }
}
