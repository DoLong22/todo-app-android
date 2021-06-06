package com.example.final_exercise.ui.todo;

import android.content.Intent;
import android.net.Uri;
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
import com.example.final_exercise.databinding.FragmentTodoBinding;
import com.example.final_exercise.model.Mission;
import com.example.final_exercise.model.User;
import com.example.final_exercise.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoFragment extends Fragment {
    private FirebaseService fbService;
    private FragmentTodoBinding binding;
    ArrayList<Mission> myTodos;
    private Todo_Adapter adapter;
    // TODO: Rename parameter arguments, choose names that match
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
        binding.todoListView.setLayoutManager(new LinearLayoutManager(getContext()));
        fbService = FirebaseService.getInstance();
        getMission();
        setOnClickAdd();
        getInformation();
    }

    @Override
    public void onResume() {
        getInformation();
        super.onResume();
        Log.d("tesst", "come here");
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
        Query myQuery = fbService.getMyRef().child("todos")
                .child("my-todo-" + fbService.getUidUser())
                .orderByKey();
        myQuery.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myTodos = new ArrayList<Mission>();
                List<Mission> missionsDone = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Mission myTodo = item.getValue(Mission.class);
                    if (myTodo.isDone()) {
                        missionsDone.add(myTodo);
                    } else {
                        myTodos.add(myTodo);
                    }
                }
                Collections.sort(myTodos);
                myTodos.addAll(missionsDone);
                adapter = new Todo_Adapter(getContext(),
                        myTodos, fbService.getMyRef(), fbService.getUidUser());
                binding.todoListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getInformation() {
        fbService.getMyRef()
                .child("users").child(fbService.getUidUser())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (getActivity() == null) {
                            return;
                        }else{
                            Glide.with(getContext()).load(Uri.parse(user.getPhotoUri())).into(binding.avatar);
                            binding.welcomeTv.setText("Hey " + user.getDisplayName());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}