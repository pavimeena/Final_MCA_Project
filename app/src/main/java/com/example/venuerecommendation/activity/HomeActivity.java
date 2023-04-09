package com.example.venuerecommendation.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.example.venuerecommendation.R;
import com.example.venuerecommendation.activity.AddContactActivity;
import com.example.venuerecommendation.activity.MapsActivity2;
import com.example.venuerecommendation.activity.ViewGroupActivity2;
import com.example.venuerecommendation.adapter.SlidingImage_Adapter;
import com.example.venuerecommendation.helper.ImageConverter;
import com.example.venuerecommendation.model.ImageModel;
import com.example.venuerecommendation.model.User;
import com.example.venuerecommendation.sharedpref.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.venuerecommendation.activity.ViewGroupActivity;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
public ImageView single,group;
public TextView uname;
    TextView navUsername;

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;

    private int[] myImageList = new int[]{R.drawable.image_1, R.drawable.image_2,
            R.drawable.image_3,R.drawable.image_4
            ,R.drawable.image_5};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();

        init();

      //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        single = findViewById(R.id.imageView10);
        group = findViewById(R.id.imageView11);
        uname = findViewById(R.id.name);


        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SelectSingleTagActivity.class);
                startActivity(intent);
              //  Toast.makeText(getApplicationContext(),"No Action Performed",Toast.LENGTH_SHORT).show();
            }
        });

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewGroupActivity2.class);
                startActivity(intent);
                //oast.makeText(getApplicationContext(),"No Action Performed",Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.name);
        ImageView imageView = (ImageView) headerView.findViewById(R.id.circleView);

        User user = SharedPrefManager.getInstance(this).getUser();
        navUsername.setText("Hi "+user.getUsername());

        /*Bitmap bitmap = getBitmapFromURL("http://10.0.0.16/VenueRecommender/uploadedFiles/"+user.getEmail()+".jpg");
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);
        imageView.setImageBitmap(circularBitmap);*/

       /* try {
            URL url = new URL("http://10.0.0.16/VenueRecommender/uploadedFiles/"+user.getEmail()+".jpg");
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(image, 100);
            imageView.setImageBitmap(circularBitmap);
        } catch(IOException e) {
            System.out.println(e);
        }*/


       // Picasso.get().load("http://10.0.0.16/VenueRecommender/uploadedFiles/"+user.getEmail()+".jpg").into(imageView);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.image_3);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

       // Bitmap bm=getBitmapFromURL("http://"+IpAddress.Ip_Address+"/VenueRecommender/uploadedFiles/"+user.getEmail()+".jpg");
       // Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bm, 100);
       // imageView.setImageBitmap(circularBitmap);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    } // Author: silentnuke
    private ArrayList<ImageModel> populateList(){

        ArrayList<ImageModel> list = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            ImageModel imageModel = new ImageModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }

    private void init() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(HomeActivity.this,imageModelArrayList));

        /*CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);*/

        /*indicator.setViewPager(mPager);*/

        /*final float density = getResources().getDisplayMetrics().density;*/

//Set circle indicator radius
        /*indicator.setRadius(5 * density);*/

        NUM_PAGES =imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        /*indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });*/

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private Bitmap convertToBitmap(byte[] b){

        return BitmapFactory.decodeByteArray(b, 0, b.length);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        }
        else if (id == R.id.nav_gallery) {
           /* Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
            startActivity(intent);*/
            Intent intent = new Intent(getApplicationContext(), AddContactActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(getApplicationContext(), ViewGroupActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_tools) {
            Intent intent = new Intent(getApplicationContext(), MapsActivity2.class);
            startActivity(intent);

        }
        else if (id == R.id.logout) {
            finish();
            SharedPrefManager.getInstance(getApplicationContext()).logout();

        }
       else if (id == R.id.drag_marker) {
            Intent intent = new Intent(getApplicationContext(),OwnLocationTag.class);
            startActivity(intent);
        }
        else if (id == R.id.nearby_events) {
            Intent intent = new Intent(getApplicationContext(),NearbyEventsActivity.class);
            startActivity(intent);

        }

       /* else if (id == R.id.search) {
            Intent intent = new Intent(getApplicationContext(),SearchMaps.class);
            startActivity(intent);

        }*/


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
