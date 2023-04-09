package com.example.venuerecommendation.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.example.venuerecommendation.adapter.CardAdapter;
import com.example.venuerecommendation.listner.RecyclerTouchListener;
import com.example.venuerecommendation.model.Group;
import com.example.venuerecommendation.model.User;
import com.example.venuerecommendation.sharedpref.SharedPrefManager;
import com.example.venuerecommendation.url.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewGroupActivity2 extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private RequestQueue requestQueue;
    private int uid;
    private List<Group> groupList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        requestQueue = Volley.newRequestQueue(this);

        getData();


        adapter = new CardAdapter(groupList);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Group group = groupList.get(position);
                Intent intent = new Intent(getApplicationContext(),ViewContactActivity2.class);
                intent.putExtra("gname",group.getGname());
                startActivity(intent);
                Toast.makeText(getApplicationContext(), group.getGname() + " is selected!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }


    private JsonArrayRequest getDataFromServer(int requestCount) {

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);


        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLs.DATA_URL + String.valueOf(requestCount),
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
                        Toast.makeText(ViewGroupActivity2.this, "No Group Available", Toast.LENGTH_SHORT).show();
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }

    //This method will get data from the web api
    private void getData() {
        User user = SharedPrefManager.getInstance(this).getUser();
        uid = user.getId();

        requestQueue.add(getDataFromServer(uid));

    }

    //This method will parse json data
    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            //Creating the superhero object
            Group superHero = new Group();
            JSONObject json = null;
            try {
                //Getting json
                json = array.getJSONObject(i);

                //Adding data to the superhero object

                superHero.setGname(json.getString("gname"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Adding the superhero object to the list
            groupList.add(superHero);
        }

        //Notifying the adapter that data has been added or changed
        adapter.notifyDataSetChanged();
    }

    //This method would check that the recyclerview scroll has reached the bottom or not
}
