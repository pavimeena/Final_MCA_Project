package com.example.venuerecommendation.activity;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.venuerecommendation.R;
import com.example.venuerecommendation.adapter.CustomAdapter;
import com.example.venuerecommendation.model.ContactModel;
import com.example.venuerecommendation.model.User;
import com.example.venuerecommendation.sharedpref.SharedPrefManager;
import com.example.venuerecommendation.volley.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddFamilyContactActivity extends AppCompatActivity {

    private ListView listView;
    private CustomAdapter customAdapter;
    private ArrayList<ContactModel> contactModelArrayList;
    public Button button;
    HashMap<String,String> HashMap;
    int uid;
    EditText group;
    public String gname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_contact);

        button = findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.listView);

        contactModelArrayList = new ArrayList<>();
        HashMap=new HashMap<String, String>();

        User user = SharedPrefManager.getInstance(this).getUser();
        uid = user.getId();

        Bundle extras = getIntent().getExtras();
        gname = extras.getString("gname");

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            ContactModel contactModel = new ContactModel();
            contactModel.setName(name);
            contactModel.setNumber(phoneNumber);
            contactModel.setCheck(false);
            contactModelArrayList.add(contactModel);
            //listViewLog.d("name>>",name+"  "+phoneNumber);
        }
        phones.close();

        customAdapter = new CustomAdapter(this,contactModelArrayList);
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String name = contactModelArrayList.get(position).getName();
                String number = contactModelArrayList.get(position).getNumber();
                boolean check = contactModelArrayList.get(position).isCheck();

                Log.e("t", String.valueOf(check));

                if(!check){
                    view.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                    contactModelArrayList.get(position).setCheck(true);
                    HashMap.put(name,number);
                    for(Map.Entry map  :  HashMap.entrySet() ) {
                        Log.e("te",map.getKey()+""+map.getValue());
                    }
                }
                else {
                    view.setBackgroundColor(getColor(R.color.colorWhite));
                    contactModelArrayList.get(position).setCheck(false);
                    HashMap.remove(name);
                    for(Map.Entry map  :  HashMap.entrySet() ) {
                        Log.e("te",map.getKey()+""+map.getValue());
                    }

                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(HashMap.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Plese Select Contact",Toast.LENGTH_SHORT).show();
                }
                else if(group.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Plese Enter Group Name",Toast.LENGTH_SHORT).show();
                }
                else {
                    for(Map.Entry map  :  HashMap.entrySet() ) {
                        addContact(map.getKey().toString(),map.getValue().toString());

                    }
                }
            }
        });
    }
    public void addContact(final String nam, final String num){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+IpAddress.Ip_Address+"/VenueRecommender/Api.php?apicall=group",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                finish();


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
                params.put("gname", gname);
                params.put("cname", nam);
                params.put("cnumber", num);

                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }
}
