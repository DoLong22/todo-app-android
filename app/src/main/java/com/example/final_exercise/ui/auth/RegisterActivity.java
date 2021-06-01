package com.example.final_exercise.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_exercise.R;
import com.example.final_exercise.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    protected FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mFirebaseAuth = FirebaseAuth.getInstance();
        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();
                String confirmPassword = binding.confirmPassword.getText().toString();
                if (password.isEmpty()) {
                    binding.password.setError("Password is not blank");
                } else if (password.length() < 6) {
                    binding.password.setError("Password must be >=6 characters");
                } else if (!confirmPassword.equals(password)) {
                    binding.confirmPassword.setError("Confirm password not equal password");
                } else {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Succsessfull!!", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Log.d("uuid_user ", user.getUid());
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Fail!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}