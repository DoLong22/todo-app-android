package com.example.final_exercise.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseService {
    private final FirebaseUser user;
    private final DatabaseReference myRef;
    public final static FirebaseService INSTANCE = new FirebaseService();

    private FirebaseService() {
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.myRef = FirebaseDatabase.
                getInstance("https://android-excersice-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference();
    }

    public static FirebaseService getInstance(){
        return INSTANCE;
    }

    public String getUidUser(){
        return this.user.getUid();
    }
    public DatabaseReference getMyRef(){
        return this.myRef;
    }
}
