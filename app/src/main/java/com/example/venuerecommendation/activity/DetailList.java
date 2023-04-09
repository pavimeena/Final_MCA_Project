package com.example.venuerecommendation.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.venuerecommendation.R;
import com.example.venuerecommendation.url.URLs;
import com.example.venuerecommendation.volley.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailList extends AppCompatActivity {
    String name;
    String lat,lng;
    public TextView placeName,date,stime,etime;
    public EditText eventname,hostname,offers,entryfee,eventsize,description;
    public ImageView idate,istime,ietime,eventimage;
    public Button add;
    public Spinner spinner,envispinner,suitablespinner,session;
    private int year, month, day;
    private String mmonth;
    private Calendar calendar;
    private ProgressBar progressBar;
    private final int GALLERY = 1;
    private  String encodedImage,placetype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_list);

      //  final Calendar myCalendar = Calendar.getInstance();
//-------------------TextView-------------------------------------------
        placeName = findViewById(R.id.textView12);
        date = findViewById(R.id.textView9);
        stime = findViewById(R.id.textView10);
        etime = findViewById(R.id.textView11);
//-------------------EditText-----------------------------------------------
        eventname = findViewById(R.id.editText4);
        hostname = findViewById(R.id.editText5);
        offers = findViewById(R.id.editText7);
        entryfee = findViewById(R.id.editText8);
        eventsize = findViewById(R.id.editText10);
        description = findViewById(R.id.editText11);
//-------------------ImageView--------------------------------------------------
        idate = findViewById(R.id.imageView2);
        istime = findViewById(R.id.imageView3);
        ietime = findViewById(R.id.imageView4);
        eventimage = findViewById(R.id.imageView8);
//-------------------Button-------------------------------------------------------
        add = findViewById(R.id.button6);
//-------------------Spinner-----------------------------------------------
        spinner = findViewById(R.id.spinner3);
        envispinner = findViewById(R.id.spinner5);
        suitablespinner =findViewById(R.id.spinner4);
        session = findViewById(R.id.spinner6);
//-----------------------------ProgessBar----------------------------------
        progressBar = findViewById(R.id.progressBar2);

        Bundle extras = getIntent().getExtras();
        name = extras.getString("placename");
        lat = String.valueOf(extras.getDouble("lat"));
        lng = String.valueOf(extras.getDouble("lng"));
        Log.e("test",name+""+lat+""+""+lng);

        SharedPreferences sp = getSharedPreferences("pre",MODE_PRIVATE);
        placetype = sp.getString("placetype",null);


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        mmonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        showDate(year, mmonth, day);

        placeName.setText(name);

        istime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DetailList.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        stime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Start Time");
                mTimePicker.show();
            }
        });

        ietime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DetailList.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select End Time");
                mTimePicker.show();
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTags();
                Log.e("test","button");
            }
        });

        eventimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY);
            }
        });



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    eventimage.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                   // uploadImage(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DetailList.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, "", arg3);
                }
            };

    private void showDate(int year, String month, int day) {
        date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
    private void addTags() {
        final String stringevent = eventname.getText().toString().trim();
        final String stringhost = hostname.getText().toString().trim();
        final String stringoffer = offers.getText().toString().trim();
        final String stringCat = spinner.getSelectedItem().toString();
        final String stringdate = date.getText().toString();
        final String stringstime = stime.getText().toString();
        final String stringetime = etime.getText().toString();
        final String stringentryfee = entryfee.getText().toString();
        final String stringsize = eventsize.getText().toString();
        final String stringdesc = description.getText().toString();
        final String stringenvi = envispinner.getSelectedItem().toString();
        final String stringsui = suitablespinner.getSelectedItem().toString();
        final String stringsess = session.getSelectedItem().toString();

        //final String gender = ((RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();
        //first we will do the validations
        if (TextUtils.isEmpty(stringevent)) {
            eventname.setError("Please enter event Name");
            eventname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(stringhost)) {
            hostname.setError("Please enter Host Name");
            hostname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(stringoffer)) {
            offers.setError("Enter a password");
            offers.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(stringentryfee)) {
            entryfee.setError("Please enter Fees");
            entryfee.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(stringsize)) {
            eventsize.setError("Please enter event Size");
            eventsize.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(stringdesc)) {
            description.setError("Please enter Description");
            description.requestFocus();
            return;
        }
        if (stringCat.equalsIgnoreCase("Select Category")) {
           Toast.makeText(getApplicationContext(),"Select Category",Toast.LENGTH_SHORT).show();
            return;
        }
        if (stringenvi.equalsIgnoreCase("Select Environment")) {
            Toast.makeText(getApplicationContext(),"Select Environment",Toast.LENGTH_SHORT).show();
            return;
        }
        if (stringsui.equalsIgnoreCase("Suitable For")) {
            Toast.makeText(getApplicationContext(),"Select Suitable User",Toast.LENGTH_SHORT).show();
            return;
        }
        if (stringsess.equalsIgnoreCase("Select Session")) {
            Toast.makeText(getApplicationContext(),"Select Session",Toast.LENGTH_SHORT).show();
            return;
        }
        if (stringdate.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(),"Select Date",Toast.LENGTH_SHORT).show();
            return;
        }
        if (stringstime.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(),"Select Start Time",Toast.LENGTH_SHORT).show();
            return;
        }
        if (stringetime.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(),"Select End Time",Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_TAGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("test","tets1");

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            Log.e("test",response);

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
                params.put("name", name);
                params.put("lat", lat);
                params.put("lng", lng);
                params.put("eventname", stringevent);
                params.put("hostname", stringhost);
                params.put("sdate", stringdate);
                params.put("stime", stringstime);
                params.put("etime", stringetime);
                params.put("offer", stringoffer);
                params.put("category", stringCat);
                params.put("placetype", placetype);
                params.put("fees", stringentryfee);
                params.put("eventimage", encodedImage);
                params.put("description", stringdesc);
                params.put("environment", stringenvi);
                params.put("suitablefor", stringsui);
                params.put("session", stringsess);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }


}
