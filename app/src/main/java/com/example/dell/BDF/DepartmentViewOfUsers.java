package com.example.dell.BDF;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class DepartmentViewOfUsers extends AppCompatActivity {

    private static final String TAG = "DepartmentDisplay";
    private static final String Ip = Constants.FixIp;
    private static final String URL_FOR_All_Departments_fetch = Ip+"/BestDoctorFinder/AllDepartmentFetch.php";
    ProgressDialog progressDialog;
    private EditText etSearch;
    private ListView lvDepartmets;
    private ArrayList<Department> mProductArrayList = new ArrayList<Department>();
    private MyAdapter adapter1;

    private String Hname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_view_of_users);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Departments of Selected Hospitals");
        progressDialog = new ProgressDialog(DepartmentViewOfUsers.this);
        progressDialog.setCancelable(false);

        etSearch = (EditText)findViewById(R.id.searchBar);
        lvDepartmets = (ListView)findViewById(R.id.ListofDepartments);

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                adapter1.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        Bundle extras = getIntent().getExtras();
        Hname= extras.getString("HospitalName");
        FetchDepartments();

    }

    private void FetchDepartments() {
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
                        Toast.makeText(getApplicationContext(), "You Have successfully Fetched All Departments" , Toast.LENGTH_SHORT).show();
                        mProductArrayList.clear();
                        if (jObj != null) {
                            for (int i=0;i<(jObj.length()-1);i++){
                                mProductArrayList.add( new Department(jObj.getJSONObject("department"+i).getString("id"),jObj.getJSONObject("department"+i).getString("name")));
                            }
                            adapter1 = new MyAdapter(getApplicationContext(), mProductArrayList);
                            lvDepartmets.setAdapter(adapter1);
                        }


                    } else {
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
                Log.e(TAG, "Fetched Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
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

    public class Department {

        public String name;
        public String id;
        public Department(String id, String name) {
            super();
            this.id = id;
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }

    public class MyAdapter extends BaseAdapter implements Filterable {

        private ArrayList<Department> mOriginalValues; // Original Values
        private ArrayList<Department> mDisplayedValues;    // Values to be displayed
        LayoutInflater inflater;

        public MyAdapter(Context context, ArrayList<Department> mProductArrayList) {
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
            LinearLayout DepartmentContainer;
            TextView tvName;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.row_single, null);
                holder.DepartmentContainer = (LinearLayout)convertView.findViewById(R.id.DepartmentContainer);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                //holder.tvId = (TextView) convertView.findViewById(R.id.tvEmail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
           // holder.tvName.setText(mDisplayedValues.get(position).id);
            holder.tvName.setText(mDisplayedValues.get(position).name+"");

            holder.DepartmentContainer.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), mDisplayedValues.get(position).id, Toast.LENGTH_SHORT).show();

                    String DeptId = mDisplayedValues.get(position).id;
                    Intent intent = new Intent(DepartmentViewOfUsers.this, ListofDoctorsUsersView.class);
                    intent.putExtra("HospitalName", Hname);
                    intent.putExtra("DeptId", DeptId);
                    startActivity(intent);

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

                    mDisplayedValues = (ArrayList<Department>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<Department> FilteredArrList = new ArrayList<Department>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<Department>(mDisplayedValues); // saves the original data in mOriginalValues
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
                                FilteredArrList.add(new Department(mOriginalValues.get(i).id,mOriginalValues.get(i).name));
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
}
