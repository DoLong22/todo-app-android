package com.example.final_exercise.ui.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.final_exercise.R;
import com.example.final_exercise.databinding.ActivityReportInformationBinding;
import com.example.final_exercise.model.Constant;
import com.example.final_exercise.model.User;
import com.example.final_exercise.service.FirebaseService;
import com.example.final_exercise.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class ReportInformationActivity extends AppCompatActivity {
    private User user;
    private FirebaseService fbService;
    private ActivityReportInformationBinding binding;
    private static final int IMAGE_REQUEST = 2;
    private Uri imageUri;
    private boolean isUpdate;
    private boolean isUpdatePhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_information);
        binding = ActivityReportInformationBinding.inflate(getLayoutInflater());
        fbService = FirebaseService.getInstance();
        isUpdate = getIntent().getBooleanExtra("REQUEST_UPDATE", false);
        if (isUpdate) {
            user = (User) getIntent().getSerializableExtra("INFORMATION_USER");
            setInformation();
            binding.btnLater.setVisibility(View.GONE);
        } else {
            user = new User();
            user.setPhotoUri(Constant.AVATAR_URI_BOY);
            user.setGender("boy");
            user.setUid(fbService.getUidUser());
            user.setBirthDay(null);
            user.setSoDeep(null);
            user.setReport(true);
        }
        setContentView(binding.getRoot());
        setOnClickBtnLater();
        setOnClickBtnOk();
        setOnClickImageBtn();
        setOnClickSelectDate();
    }

    public void setInformation() {
        binding.displayName.setText(user.getDisplayName());
        binding.birthday.setText(user.getBirthDay());
        binding.soDeep.setText(user.getSoDeep());
        Glide.with(this).load(Uri.parse(user.getPhotoUri())).into(binding.avatar);
        switch (user.getGender()) {
            case "boy":
                binding.radioBtnBoy.setChecked(true);
                break;
            case "girl":
                binding.radioBtnGirl.setChecked(true);
                break;
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radioBtnBoy:
                if (checked) {
                    if(!isUpdate){
                        Glide.with(this).load(getImage("boy")).into(binding.avatar);
                        user.setPhotoUri(Constant.AVATAR_URI_BOY);
                    }
                    user.setGender("boy");
                    isUpdatePhoto = false;
                }
                break;
            case R.id.radioBtnGirl:
                if (checked) {
                    if(!isUpdate){
                        Glide.with(this).load(getImage("girl")).into(binding.avatar);
                        user.setPhotoUri(Constant.AVATAR_URI_GIRL);
                    }
                    user.setGender("girl");
                    isUpdatePhoto = false;
                }
                break;
        }
    }

    public void setOnClickBtnLater() {
        binding.btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.displayName.getText().toString().isEmpty()) {
                    binding.displayName.setError("You should have a nick name");
                } else {
                    user.setDisplayName(binding.displayName.getText().toString());
                    user.setBirthDay(null);
                    Intent intent = new Intent(ReportInformationActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    saveUser(user);
                }

            }
        });
    }

    public void setOnClickBtnOk() {
        binding.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.displayName.getText().toString().isEmpty()) {
                    binding.displayName.setError("You should have a nick name");
                }else{
                    if (isUpdatePhoto) {
                        uploadImage();
                    } else {
                        renderNextPage();
                    }
                }
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
        fbService.getMyRef().child("users").child(user.getUid()).setValue(user);
    }

    private void setOnClickImageBtn() {
        binding.btnAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdatePhoto = true;
                openImage();
            }
        });
    }

    public void openImage() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            Glide.with(ReportInformationActivity.this)
                    .load(imageUri).into(binding.avatar);
        }
    }

    public void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if (imageUri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis() + "." + getFileSuffix(imageUri));
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String photoUrlUpdated = uri.toString();
                            pd.dismiss();
                            user.setPhotoUri(photoUrlUpdated);
                            Toast.makeText(ReportInformationActivity.this,
                                    "Update successful", Toast.LENGTH_SHORT).show();
                            renderNextPage();
                        }
                    });
                }
            });
        }
    }

    private String getFileSuffix(Uri imageUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void renderNextPage() {
        user.setDisplayName(binding.displayName.getText().toString());
        user.setSoDeep(binding.soDeep.getText().toString());
        saveUser(user);
        if (isUpdate) {
            finish();
        } else {
            Intent intent = new Intent(ReportInformationActivity.this,
                    MainActivity.class);
            startActivity(intent);
        }
    }
}
