package com.example.venuerecommendation.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.venuerecommendation.R;

import java.util.ArrayList;
import java.util.Calendar;

public class FinalPlaceActivity extends AppCompatActivity {
    String name;
    String lat;
    String lng;
    ArrayList<String> numbers;
    EditText message;
    ImageView imageView;
    TextView time;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_place);

        message = findViewById(R.id.editText6);
        imageView = findViewById(R.id.imageView5);
        time = findViewById(R.id.textView15);
        send = findViewById(R.id.button9);

        Bundle extras = getIntent().getExtras();

        name = extras.getString("name");
        lat = extras.getString("lat");
        lng = extras.getString("lng");

        numbers = getIntent().getExtras().getStringArrayList("list");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;

                mTimePicker = new TimePickerDialog(FinalPlaceActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Start Time");
                mTimePicker.show();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter message", Toast.LENGTH_SHORT).show();
                } else if (time.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Enter Time", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        SmsManager smgr = SmsManager.getDefault();
                        for (String number : numbers)   // using foreach
                        {
                            smgr.sendTextMessage(number, null, "meetup@" + lat + "@" + lng + "@" + time.getText().toString() + "@" + message.getText().toString(), null, null);
                        }

                        Toast.makeText(FinalPlaceActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(FinalPlaceActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
}
