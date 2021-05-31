package com.example.final_exercise.ui.todo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.final_exercise.R;
import com.example.final_exercise.databinding.ActivityMainBinding;
import com.example.final_exercise.databinding.FragmentTodoBinding;
import com.example.final_exercise.model.Mission;
import com.example.final_exercise.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoFragment extends Fragment {
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FragmentTodoBinding binding;
    List<Mission> myTodos;
    private Todo_Adapter adapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TodoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodoFragment newInstance(String param1, String param2) {
        TodoFragment fragment = new TodoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                getInformation();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTodoBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnClickAdd();
        getInformation();
        getMission();
        binding.todoListView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                adapter = new Todo_Adapter(getContext(), myTodos, myRef, user);
                binding.todoListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

        @Override
    public void onResume() {
        getInformation();
        super.onResume();
        Log.d("tesst","come here");
    }
    public void setOnClickAdd() {
        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),
                        NewMissionActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getMission() {
        binding.todoListView.setLayoutManager(new LinearLayoutManager(getContext()));
        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance("https://android-excersice-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("my-todo-" + user.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myTodos = new ArrayList<Mission>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Mission myTodo = item.getValue(Mission.class);
                    myTodos.add(myTodo);
                }
                Log.d("compare ", String.valueOf(true));
                Comparator<Mission> compareById = (Mission o1, Mission o2) -> String.valueOf(o1.isDone()).compareTo(String.valueOf(o2.isDone()));
                Collections.sort(myTodos, compareById.reversed());
                adapter = new Todo_Adapter(getContext(),
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