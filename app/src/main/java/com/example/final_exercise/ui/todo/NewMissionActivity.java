package com.example.final_exercise.ui.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.example.final_exercise.R;
import com.example.final_exercise.databinding.ActivityNewMissionBinding;
import com.example.final_exercise.notification.MyNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NewMissionActivity extends AppCompatActivity {
    private ActivityNewMissionBinding binding;
    private DatabaseReference reference;
    private FirebaseUser user;
    private int mYear, mMonth, mDay;
    private Integer TodoNum = new Random().nextInt();
    private String keytodo = Integer.toString(TodoNum);
private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mission);
        binding = ActivityNewMissionBinding.inflate(getLayoutInflater());
        user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(binding.getRoot());
        calendar = Calendar.getInstance();
        setOnClickCancelBtn();
        setOnClickCreateBtn();
        setOnClickSelectDate();
    }

    private void setOnClickCreateBtn(){
        binding.btnSaveMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                reference = FirebaseDatabase.getInstance("https://android-excersice-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                .child("my-todo-"+ user.getUid()).child("mission" + keytodo);
                Map<String, Object> data= new HashMap<>();
                data.put("title", binding.title.getText().toString());
                data.put("description", binding.des.getText().toString());
                data.put("date", binding.date.getText().toString());
                data.put("key", keytodo);
                data.put("isDone",false);
                data.put("label", binding.labelSpinner.getSelectedItem().toString());
                reference.setValue(data);

                AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(NewMissionActivity.this,
                        MyNotification.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(NewMissionActivity.this,
                        0, intent, 0);
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                finish();
            }
        });
    }
    private void setOnClickCancelBtn(){
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setOnClickSelectDate(){
        binding.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c=Calendar.getInstance();
                mYear=c.get(Calendar.YEAR);
                mMonth=c.get(Calendar.MONTH);
                mDay=c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(NewMissionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                binding.date.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.set(mYear,mMonth,mDay);
                            }
                        },mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }
}