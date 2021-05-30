package com.example.final_exercise.ui.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
            reference = FirebaseDatabase.getInstance("https://android-excersice-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                    .child("my-todo-" + user.getUid()).child("mission" + myTodo.getKey());
            setOnclickSaveBtn(myTodo);
            setOnClickSelectDate();
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
                editTodo.setLabel(binding.labelSpinner.getSelectedItem().toString());

                reference.setValue(editTodo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),
                                "Update success.", Toast.LENGTH_SHORT).show();
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
    private void setOnClickSelectDate(){
        binding.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c=Calendar.getInstance();
                mYear=c.get(Calendar.YEAR);
                mMonth=c.get(Calendar.MONTH);
                mDay=c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(UpdateMissionActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                binding.date.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                            }
                        },mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }
}