package com.example.final_exercise.ui.todo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.final_exercise.R;
import com.example.final_exercise.databinding.ActivityNewMissionBinding;
import com.example.final_exercise.model.Mission;
import com.example.final_exercise.notification.MyNotification;
import com.example.final_exercise.service.FirebaseService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Random;

public class NewMissionActivity extends AppCompatActivity {
    private ActivityNewMissionBinding binding;
    private DatabaseReference reference;
    private DatabaseReference myRef;
    private FirebaseService fbService;
    private int mYear, mMonth, mDay,mHourOfDay, mMinute;
    private final Integer TodoNum = new Random().nextInt();
    private final String keytodo = Integer.toString(TodoNum);
    private Calendar calendar;
    private boolean isSetAlarm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mission);
        binding = ActivityNewMissionBinding.inflate(getLayoutInflater());
        fbService = FirebaseService.getInstance();
        myRef = fbService.getMyRef();
        calendar = Calendar.getInstance();
        setContentView(binding.getRoot());
        setOnClickCancelBtn();
        setOnClickCreateBtn();
        setOnClickSelectDate();
        setOnClickSelectTime();
    }

    private void setOnClickCreateBtn() {
        binding.btnSaveMission.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance("https://android-excersice-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference().child("todos").child("my-todo-" + fbService.getUidUser())
                        .child("mission" + keytodo);
                Mission mission = new Mission();
                mission.setTitle(binding.title.getText().toString());
                mission.setDescription(binding.des.getText().toString());
                mission.setDate(binding.time.getText().toString()+" "+binding.date.getText().toString());
                mission.setKey(keytodo);
                mission.setDone(false);
                String label = binding.labelSpinner.getSelectedItem().toString();
                mission.setLabel(label);
                mission.setLevel(getLevel(label));
                if (isSetAlarm) {
                    startAlarm(calendar, mission);
                    Log.d("alarm_hour:",String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
                    Log.d("alarm_minute:",String.valueOf(calendar.get(Calendar.MINUTE)));
                }
                reference.setValue(mission);
                finish();
            }
        });
    }

    private void setOnClickCancelBtn() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setOnClickSelectTime() {
        binding.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                mMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewMissionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        binding.time.setText(hourOfDay + ":" + minute);
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        isSetAlarm = true;
                    }
                }, mHourOfDay, mMinute, true);
                timePickerDialog.show();
            }
        });
    }

    private void setOnClickSelectDate() {
        binding.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewMissionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                binding.date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                isSetAlarm = true;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c, Mission mission) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyNotification.class);
        intent.putExtra("myAction", "mDoNotify");
        intent.putExtra("Title", mission.getTitle());
        intent.putExtra("Description", mission.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(mission.getKey()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    public int getLevel(String label) {
        int level = 0;
        switch (label) {
            case "Very Important":
                level = 3;
                break;
            case "Important":
                level = 2;
                break;
            case "Normal":
                level = 1;
                break;
            case "Unnecessary":
                level = 0;
                break;
        }
        return level;
    }
}