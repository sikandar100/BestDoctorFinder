package com.example.dell.BDF;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
public class Patients_List extends Fragment {
    private static final String TAG = "PatientsList";
    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_Send_TreatmentResponce = Ip + "/BestDoctorFinder/TreatmentResponce.php";
    private static final String URL_FOR_All_Patients_in_treatment_list = Ip + "/BestDoctorFinder/FetchPatientsList.php";
    ProgressDialog progressDialog;
    private SharedPreferences preferences;

    private EditText etSearch;
    private ListView lvPersons;
    private ArrayList<Person> PatientsArrayList = new ArrayList<Person>();
    private MyAdapter adapter1;


    public Patients_List() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Patients List");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patients__list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = (EditText) view.findViewById(R.id.searchBar);
        lvPersons = (ListView) view.findViewById(R.id.Listofpatients);

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
        FetchPatientsList();
    }

    private void FetchPatientsList() {
        // Tag used to cancel the request
        String cancel_req_tag = "Fetch";

        progressDialog.setMessage("Working! Plz Wait ...");
        showDialog();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String Did = preferences.getString("UserId","NotAny");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_All_Patients_in_treatment_list, new Response.Listener<String>() {

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
                        Toast.makeText(getContext(), "You Have successfully Fetched All Patients List", Toast.LENGTH_SHORT).show();
                        if (jObj != null) {
                            for (int i = 0; i < (jObj.length() - 1); i++) {
                                PatientsArrayList.add( new Person(jObj.getJSONObject("user"+i).getString("email")));
                            }
                        }
                        adapter1 = new MyAdapter(getContext(), PatientsArrayList);
                        lvPersons.setAdapter(adapter1);

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
                params.put("status", "2");
                params.put("Did",Did);
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
    public class Person {

        public String email;
        public Person(String email) {
            super();
            this.email = email;

        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

    }

    public class MyAdapter extends BaseAdapter implements Filterable {

        private ArrayList<Person> mOriginalValues; // Original Values
        private ArrayList<Person> mDisplayedValues;    // Values to be displayed
        LayoutInflater inflater;

        public MyAdapter(Context context, ArrayList<Person> mProductArrayList) {
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
            LinearLayout PLContainer;
            TextView tvEmail;
            Button completebtn;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            MyAdapter.ViewHolder holder = null;

            if (convertView == null) {

                holder = new MyAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.patients_list, null);
                holder.PLContainer = (LinearLayout)convertView.findViewById(R.id.PLContainer);
                holder.tvEmail = (TextView) convertView.findViewById(R.id.Emailview);
                holder.completebtn = (Button) convertView.findViewById(R.id.treatmentComplete);
                convertView.setTag(holder);
            } else {
                holder = (MyAdapter.ViewHolder) convertView.getTag();
            }

            holder.tvEmail.setText(mDisplayedValues.get(position).email);

            holder.completebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Toast.makeText(getContext(),mDisplayedValues.get(position).email,Toast.LENGTH_SHORT).show();
                    TreatmentResponce(mDisplayedValues.get(position).email,"3");
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

                    mDisplayedValues = (ArrayList<Person>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<Person> FilteredArrList = new ArrayList<Person>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<Person>(mDisplayedValues); // saves the original data in mOriginalValues
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
                            String data = mOriginalValues.get(i).email;
                            if (data.toLowerCase().startsWith(constraint.toString())) {
                                FilteredArrList.add(new Person(mOriginalValues.get(i).email));
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

    private void TreatmentResponce(final String email,final String status) {
        // Tag used to cancel the request
        String cancel_req_tag = "TreatmentResponce";

        progressDialog.setMessage("Working Plz Wait ...");
        showDialog();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String Did = preferences.getString("UserId","NotAny");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_Send_TreatmentResponce, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getContext()," Success!", Toast.LENGTH_SHORT).show();
                        showDialog();
                        adapter1.EmptyTheArray();
                        FetchPatientsList();
                        hideDialog();

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
                params.put("email", email);
                params.put("status", status);
                params.put("Did",Did);

                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
    }
}
