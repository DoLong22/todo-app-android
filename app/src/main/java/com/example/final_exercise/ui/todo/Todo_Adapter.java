package com.example.final_exercise.ui.todo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_exercise.R;
import com.example.final_exercise.model.Mission;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Todo_Adapter extends
        RecyclerView.Adapter<Todo_Adapter.TodoViewHolder> {
    Context mContext;
    List<Mission> myTodoList;
    private FirebaseUser user;
    private final DatabaseReference myRef;

    public Todo_Adapter(Context mContext, List<Mission> myTodoList, DatabaseReference myRef, FirebaseUser user) {
        this.mContext = mContext;
        this.myTodoList = myTodoList;
        this.user = user;
        this.myRef = myRef;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_todo, parent, false);
        TodoViewHolder todoViewHolder = new TodoViewHolder(view);
        return todoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Todo_Adapter.TodoViewHolder holder, int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (myTodoList.get(position).isDone()) {
            holder.missionTitle.setPaintFlags(holder.missionTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.checkBox.setChecked(true);
        }
        holder.missionTitle.setText(myTodoList.get(position).getTitle());
        holder.missionDes.setText(myTodoList.get(position).getLabel());
        switch (myTodoList.get(position).getLabel()){
            case "Very Important":
                holder.missionDes.setTextColor(Color.parseColor("#750099"));
                break;
            case "Important":
                holder.missionDes.setTextColor(Color.parseColor("#b5126e"));
                break;
            case "Normal":
                holder.missionDes.setTextColor(Color.parseColor("#5762fa"));
                break;
            case "Unnecessary":
                holder.missionDes.setTextColor(Color.parseColor("#57fa95"));
                break;
        }
        holder.missionDate.setText(myTodoList.get(position).getDate());
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdateMissionActivity.class);
                intent.putExtra("todo", myTodoList.get(position));
                mContext.startActivity(intent);
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                myTodoList.get(position).setDone(!myTodoList.get(position).isDone());

                if (b) {
                    holder.missionTitle.setPaintFlags(holder.missionTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    holder.missionTitle.setPaintFlags(0);
                }
                myRef.child("mission" + myTodoList.get(position).getKey()).setValue(myTodoList.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(mContext, EditTodoActivity.class);
//                intent.putExtra("todo", myTodoList.get(position));
//                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myTodoList.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        TextView missionTitle, missionDes, missionDate;
        ImageButton editBtn;
        CheckBox checkBox;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            missionTitle = itemView.findViewById(R.id.missionTitle);
            missionDes = itemView.findViewById(R.id.missionDes);
            missionDate = itemView.findViewById(R.id.missionDate);
            editBtn = itemView.findViewById(R.id.editBtn);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
