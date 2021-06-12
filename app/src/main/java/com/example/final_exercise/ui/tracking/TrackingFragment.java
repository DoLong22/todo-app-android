package com.example.final_exercise.ui.tracking;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.final_exercise.databinding.FragmentTrackingBinding;
import com.example.final_exercise.model.Mission;
import com.example.final_exercise.service.FirebaseService;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrackingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrackingFragment extends Fragment {
    private FragmentTrackingBinding binding;
    private FirebaseService fbService;
    ArrayList<Mission> myTodos;
    private Calendar calendar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrackingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrackingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrackingFragment newInstance(String param1, String param2) {
        TrackingFragment fragment = new TrackingFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrackingBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fbService = FirebaseService.getInstance();
        calendar = Calendar.getInstance();
        getMission();
        setChart();
    }

    public void getMission() {
        Query myQuery = fbService.getMyRef().child("todos")
                .child("my-todo-" + fbService.getUidUser())
                .orderByKey();
        myQuery.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myTodos = new ArrayList<Mission>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Mission myTodo = item.getValue(Mission.class);
                    myTodos.add(myTodo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<Entry> setData(String label) {
        ArrayList<Entry> listEntry = new ArrayList<Entry>();
        for(int i = 0;i<=7;i++){
            calendar.set(calendar.get(Calendar.DAY_OF_MONTH)-i,Calendar.DAY_OF_MONTH);
            long time = calendar.getTimeInMillis();
            int totalMiss=0;
            for(Mission mission:myTodos){
                if(mission.getTimeInMills()==time && mission.getLabel().equals(label)){
                    totalMiss++;
                }
            }
            listEntry.add(new Entry(calendar.get(Calendar.DAY_OF_MONTH)-i, totalMiss));
        }
        return listEntry;
    }

    public void setChart() {
        LineDataSet lineDataSet1 = new LineDataSet(setData("Very important"), "Very important");
        LineDataSet lineDataSet2 = new LineDataSet(setData("Important"), "Important");
        LineDataSet lineDataSet3 = new LineDataSet(setData("Normal"), "Normal");
        LineDataSet lineDataSet4 = new LineDataSet(setData("Unnecessary"), "Unnecessary");

        lineDataSet1.setColor(Color.parseColor("#750099"));
        lineDataSet2.setColor(Color.parseColor("#b5126e"));
        lineDataSet3.setColor(Color.parseColor("#5762fa"));
        lineDataSet4.setColor(Color.parseColor("#57fa95"));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);
        dataSets.add(lineDataSet3);
        dataSets.add(lineDataSet4);
        LineData data = new LineData(dataSets);
        binding.lineChart.setData(data);
        binding.lineChart.invalidate();
    }
}