package com.example.final_exercise.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.final_exercise.R;
import com.example.final_exercise.databinding.ActivityMainBinding;
import com.example.final_exercise.model.Mission;
import com.example.final_exercise.ui.todo.NewMissionActivity;
import com.example.final_exercise.ui.todo.Todo_Adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ActivityMainBinding binding;
    List<Mission> myTodos;
    private Todo_Adapter adapter;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setOnClickAdd();
        getInformation();
        binding.todoListView.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance("https://android-excersice-default-rtdb.asia-southeast1.firebasedatabase.app/");
        myRef = database.getReference().child("my-todo-" + user.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myTodos = new ArrayList<Mission>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Mission myTodo = item.getValue(Mission.class);
                    Log.d("title ", myTodo.getTitle());
                    myTodos.add(myTodo);
                }
                adapter = new Todo_Adapter(MainActivity.this, myTodos, myRef, user);
                binding.todoListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setOnClickAdd() {
        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        NewMissionActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getMission() {
        binding.todoListView.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance("https://android-excersice-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("my-todo-" + user.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myTodos = new ArrayList<Mission>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Mission myTodo = item.getValue(Mission.class);
                    Log.d("des ", myTodo.getDescription());
                    myTodos.add(myTodo);
                }
                adapter = new Todo_Adapter(MainActivity.this,
                        myTodos, myRef, user);
                binding.todoListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getInformation() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() != null) {
            Log.d("image ", user.getPhotoUrl().toString());
            Glide.with(this).load(user.getPhotoUrl()).into(binding.avatar);
        }
        if (user.getDisplayName() != null) {
            Log.d("name ", user.getDisplayName());
            binding.welcomeTv.setText("Hey " + user.getDisplayName());
        }
    }
}