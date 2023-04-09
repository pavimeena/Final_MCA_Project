package com.example.venuerecommendation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.venuerecommendation.R;
import com.example.venuerecommendation.adapter.ContactAdapter;
import com.example.venuerecommendation.adapter.ContactAdapter2;
import com.example.venuerecommendation.listner.RecyclerTouchListener;
import com.example.venuerecommendation.model.Contact;
import com.example.venuerecommendation.model.User;
import com.example.venuerecommendation.sharedpref.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewContactActivity2 extends AppCompatActivity {

    private String gname;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private RequestQueue requestQueue;
    private int uid;
    private List<Contact> contactList = new ArrayList<>();
    public ArrayList<String> numbers = new ArrayList<String>();
    public Button sndsms;
    public String pnumber;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact2);

        Bundle extras = getIntent().getExtras();
        gname = extras.getString("gname");
        User user = SharedPrefManager.getInstance(this).getUser();
        pnumber = user.getPhone();
        count = numbers.size();
            //The key argument here must match that used in the other activity

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        sndsms =(Button)findViewById(R.id.button);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        requestQueue = Volley.newRequestQueue(this);

        getData();


        adapter = new ContactAdapter2(contactList);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Contact contact = contactList.get(position);
                String check = String.valueOf(contact.isCheck());
                Toast.makeText(getApplicationContext(), contact.isCheck() + " is selected!", Toast.LENGTH_SHORT).show();
                if(check.equalsIgnoreCase("true")){
                    contact.setCheck(false);
                    numbers.remove(contact.getNumber());

                }
                else {
                    contact.setCheck(true);
                    numbers.add(contact.getNumber());
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onLongClick(View view, int position) {
                Contact contact = contactList.get(position);
                String num = contact.getNumber();

                numbers.remove(num);


                for (String str_Agil : numbers)   // using foreach
                {
                    Log.e("phone" , str_Agil);
                }

            }
        }));

        sndsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SendSmsActivity.class);
                intent.putStringArrayListExtra("list",numbers);
                startActivity(intent);
                finish();
            }
        });
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_contact) {
            Intent intent = new Intent(getApplicationContext(),AddFamilyContactActivity.class);
            intent.putExtra("gname",gname);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }*/
    private JsonArrayRequest getDataFromServer(int requestCount,String gname) {

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        Log.e("s",""+gname);


        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("http://"+IpAddress.Ip_Address+"/VenueRecommender/ViewContact.php?uid="+requestCount+"&name="+gname,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        parseData(response);

                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        //If an error occurs that means end of the list has reached
                        Toast.makeText(ViewContactActivity2.this, "No Contact Available", Toast.LENGTH_SHORT).show();
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    //This method will get data from the web api
    private void getData() {
        User user = SharedPrefManager.getInstance(this).getUser();
        uid = user.getId();
        Bundle extras = getIntent().getExtras();
        gname = extras.getString("gname");

        requestQueue.add(getDataFromServer(uid,gname));

    }

    //This method will parse json data
    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            //Creating the superhero object
            Contact contact = new Contact();
            JSONObject json = null;
            try {
                //Getting json
                json = array.getJSONObject(i);

                //Adding data to the superhero object

                contact.setName(json.getString("cname"));
                contact.setNumber(json.getString("cnumber"));
                contact.setCheck(true);
                numbers.add(json.getString("cnumber"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Adding the superhero object to the list
            contactList.add(contact);

        }


        //Notifying the adapter that data has been added or changed
        adapter.notifyDataSetChanged();
    }
}
