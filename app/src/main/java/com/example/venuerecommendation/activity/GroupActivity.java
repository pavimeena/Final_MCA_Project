package com.example.venuerecommendation.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.venuerecommendation.R;
import com.example.venuerecommendation.model.User;
import com.example.venuerecommendation.sharedpref.SharedPrefManager;
import com.example.venuerecommendation.url.URLs;
import com.example.venuerecommendation.volley.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class GroupActivity extends AppCompatActivity {

    EditText group,cname,cnumber;
    Button add,viewGroup;
    ProgressBar progressBar;
    int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        group =(EditText) findViewById(R.id.group);
        cname = (EditText)findViewById(R.id.cname);
        cnumber =(EditText) findViewById(R.id.cnumber);
        add =(Button) findViewById(R.id.add);
      //  viewGroup =(Button)findViewById(R.id.viewgroup);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        User user = SharedPrefManager.getInstance(this).getUser();
        uid = user.getId();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup();
            }
        });


    }
    private void addGroup(){
        final String sgroup = group.getText().toString().trim();
        final String scname = cname.getText().toString().trim();
        final String scnumber = cnumber.getText().toString().trim();

        if (TextUtils.isEmpty(sgroup)) {
            group.setError("Please enter Group name");
            group.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(scname)) {
            cname.setError("Please enter name");
            cname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(scnumber)) {
            cnumber.setError("Enter a number");
            cnumber.requestFocus();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_GROUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                group.setText("");
                                cname.setText("");
                                cnumber.setText("");


                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", String.valueOf(uid));
                params.put("gname", sgroup);
                params.put("cname", scname);
                params.put("cnumber", scnumber);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }
}
