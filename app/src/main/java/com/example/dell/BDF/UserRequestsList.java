package com.example.dell.BDF;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
public class UserRequestsList extends Fragment {

    private static final String TAG = "PatientRequests";
    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_RequestCancelationByUser = Ip + "/BestDoctorFinder/RequestCancelationByUser.php";
    private static final String URL_FOR_All_RequestsofUser = Ip + "/BestDoctorFinder/FetchAllRequestsofUser.php";
    ProgressDialog progressDialog;
    private SharedPreferences preferences;

    private EditText etSearch;
    private ListView lvPersons;
    private ArrayList<Person> RequestsList = new ArrayList<Person>();
    private MyAdapter adapter1;


    public UserRequestsList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Requests List");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_requests_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etSearch = (EditText) view.findViewById(R.id.searchBar);
        lvPersons = (ListView) view.findViewById(R.id.ListofRequests);

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

        FetchPendingRequests();
    }

    private void FetchPendingRequests() {
        // Tag used to cancel the request
        String cancel_req_tag = "Fetch";

        progressDialog.setMessage("Working! Plz Wait ...");
        showDialog();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String UserId = preferences.getString("UserId","NotAny");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_All_RequestsofUser, new Response.Listener<String>() {

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
                        Toast.makeText(getContext(), "You Have successfully Fetched All Your Requests", Toast.LENGTH_SHORT).show();
                        if (jObj != null) {
                            for (int i = 0; i < (jObj.length() - 1); i++) {
                                RequestsList.add( new Person(jObj.getJSONObject("user"+i).getString("name"),jObj.getJSONObject("user"+i).getString("DocId")));
                            }
                        }
                        adapter1 = new MyAdapter(getContext(), RequestsList);
                        lvPersons.setAdapter(adapter1);

                    } else {
                        adapter1 = new MyAdapter(getContext(), RequestsList);
                        lvPersons.setAdapter(adapter1);
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
                params.put("status", "1");
                params.put("UserId",UserId);
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

        public String name;
        public String Id;


        public Person(String name, String id) {
            super();
            this.name = name;
            this.Id = id;
        }
        public String getEmail() {
            return name;
        }
        public String getId() {return Id;}
        public void setEmail(String name) {
            this.name = name;
        }
        public void setId(String id) {Id = id;}
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
            LinearLayout RLContainer;
            TextView nameView;
            Button cancelbtn;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            MyAdapter.ViewHolder holder = null;

            if (convertView == null) {

                holder = new MyAdapter.ViewHolder();
                convertView = inflater.inflate(R.layout.request_list, null);
                holder.RLContainer = (LinearLayout)convertView.findViewById(R.id.RLContainer);
                holder.nameView = (TextView) convertView.findViewById(R.id.nameView);
                holder.cancelbtn = (Button) convertView.findViewById(R.id.CancelBtn);
                convertView.setTag(holder);
            } else {
                holder = (MyAdapter.ViewHolder) convertView.getTag();
            }

            holder.nameView.setText(mDisplayedValues.get(position).name);


            holder.cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setCancelable(true);
                    builder.setTitle("Confirmation!");
                    builder.setMessage("Do You Really Want to Cancel This Request?");

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           // Toast.makeText(getContext(),mDisplayedValues.get(position).Id,Toast.LENGTH_SHORT).show();
                            preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            final String UserId = preferences.getString("UserId","NotAny");
                            RequestCancelationByUser(UserId,mDisplayedValues.get(position).Id);

                        }
                    });
                    builder.show();
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
                            String data = mOriginalValues.get(i).name;
                            if (data.toLowerCase().startsWith(constraint.toString())) {
                                FilteredArrList.add(new Person(mOriginalValues.get(i).name,mOriginalValues.get(i).Id));
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

    private void RequestCancelationByUser(final String UserId,final String DocId) {
        // Tag used to cancel the request
        String cancel_req_tag = "RequestCancelationByUser";

        progressDialog.setMessage("Working Plz Wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_FOR_RequestCancelationByUser, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Request Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getContext()," Successfully Canceled Your Request!", Toast.LENGTH_SHORT).show();
                        showDialog();
                        adapter1.EmptyTheArray();
                        FetchPendingRequests();
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
                params.put("UserId", UserId);
                params.put("DocId", DocId);
                params.put("status", "1");

                return params;
            }
        };
        // Adding request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(strReq, cancel_req_tag);
    }


}
