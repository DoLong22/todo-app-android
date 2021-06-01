package com.example.final_exercise.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.example.final_exercise.R;
import com.example.final_exercise.databinding.ActivityRegisterBinding;
import com.example.final_exercise.databinding.ActivityReportInformationBinding;
import com.example.final_exercise.model.Constant;
import com.example.final_exercise.model.User;
import com.example.final_exercise.ui.MainActivity;
import com.example.final_exercise.ui.todo.NewMissionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ReportInformationActivity extends AppCompatActivity {
    private User user;
    private FirebaseUser userLogin;
    private ActivityReportInformationBinding binding;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_information);
        binding = ActivityReportInformationBinding.inflate(getLayoutInflater());
        userLogin = FirebaseAuth.getInstance().getCurrentUser();
        user = new User();
        setContentView(binding.getRoot());
        setOnClickBtnLater();
        setOnClickBtnOk();
        user.setPhotoUri(Constant.AVATAR_URI_BOY);
        user.setGender("boy");
        user.setUid(userLogin.getUid());
        user.setDisplayName(null);
        user.setBirthDay(null);
        user.setSoDeep(null);
        user.setReport(true);
        setOnClickSelectDate();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radioBtnBoy:
                if (checked) {
                    Glide.with(this).load(getImage("boy")).into(binding.avatar);
                    user.setPhotoUri(Constant.AVATAR_URI_BOY);
                    user.setGender("boy");
                }
                break;
            case R.id.radioBtnGirl:
                if (checked) {
                    Glide.with(this).load(getImage("girl")).into(binding.avatar);
                    user.setPhotoUri(Constant.AVATAR_URI_GIRL);
                    user.setGender("girl");
                }
                break;
        }
        // boy 2131230808
        // girl 2131230869
    }

    public void setOnClickBtnLater() {
        binding.btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportInformationActivity.this,
                        MainActivity.class);
                user.setBirthDay(null);
                saveUser(user);
                startActivity(intent);
            }
        });
    }

    public void setOnClickBtnOk() {
        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setDisplayName(binding.displayName.getText().toString());
                user.setSoDeep(binding.soDeep.getText().toString());
                saveUser(user);
                Intent intent = new Intent(ReportInformationActivity.this,
                        MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public int getImage(String imageName) {
        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());
        Log.d("id image ", String.valueOf(drawableResourceId));
        return drawableResourceId;
    }

    private void setOnClickSelectDate() {
        binding.birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportInformationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                binding.birthday.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                user.setBirthDay(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    public void saveUser(User user) {
        reference = FirebaseDatabase.getInstance("https://android-excersice-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("users").child(user.getUid());
        reference.setValue(user);
    }
}
