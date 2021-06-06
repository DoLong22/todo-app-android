package com.example.final_exercise.ui.todo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.final_exercise.R;

import com.example.final_exercise.databinding.ActivityNewMissionBinding;
import com.example.final_exercise.databinding.ActivityUpdateMissionBinding;
import com.example.final_exercise.model.Mission;
import com.example.final_exercise.notification.MyNotification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Random;

public class UpdateMissionActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private FirebaseUser user;
    private int mYear, mMonth, mDay;
    private final Integer TodoNum = new Random().nextInt();
    private final String keytodo = Integer.toString(TodoNum);
    private ActivityUpdateMissionBinding binding;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_mission);
        binding = ActivityUpdateMissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = FirebaseAuth.getInstance().getCurrentUser();
        final Intent intent = getIntent();
        if (intent.getSerializableExtra("todo") != null) {
            final Mission myTodo = (Mission) intent.getSerializableExtra("todo");
            binding.title.setText(myTodo.getTitle());
            binding.des.setText(myTodo.getDescription());
            binding.date.setText(myTodo.getDate());
            String compareValue = myTodo.getLabel();
            binding.labelSpinner.setSelection(getIndex(binding.labelSpinner, compareValue));
            reference = FirebaseDatabase.getInstance("https://android-excersice-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference().child("todos").child("my-todo-" + user.getUid())
                    .child("mission" + myTodo.getKey());
            setOnclickSaveBtn(myTodo);
            setOnClickSelectDate();
            setOnClickCancelBtn();
        }
    }

    private void setOnclickSaveBtn(Mission mission) {
        binding.btnSaveMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Mission editTodo = new Mission();
                editTodo.setTitle(binding.title.getText().toString());
                editTodo.setDescription(binding.des.getText().toString());
                editTodo.setDate(binding.date.getText().toString());
                editTodo.setKey(mission.getKey());
                String label = binding.labelSpinner.getSelectedItem().toString();
                editTodo.setLabel(label);
                editTodo.setLevel(getLevel(label));
                reference.setValue(editTodo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),
                                "Update success.", Toast.LENGTH_SHORT).show();
                        if (calendar != null) {
                            startAlarm(calendar, editTodo);
                        }
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Failed to update value.", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void setOnClickSelectDate() {
        binding.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateMissionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                binding.date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                calendar.set(mYear, mMonth, mDay);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c, Mission mission) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyNotification.class);
        intent.putExtra("myAction", "mDoNotify");
        intent.putExtra("Title", mission.getTitle());
        intent.putExtra("Description", mission.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
//        if (c.before(Calendar.getInstance())) {
//            c.add(Calendar.DATE, 1);
//        }
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