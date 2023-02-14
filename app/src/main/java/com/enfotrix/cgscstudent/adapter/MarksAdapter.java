package com.enfotrix.cgscstudent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enfotrix.cgscstudent.model.Marks;
import com.enfotrix.cgscstudent.R;

import java.util.List;

public class MarksAdapter extends RecyclerView.Adapter<MarksAdapter.ViewHolder> {
    private Context context;
    private List<Marks> marks;

    public MarksAdapter( Context context, List<Marks> marks){
        this.context = context;
        this.marks = marks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_student_marks, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Marks mark = marks.get(position);
        holder.tvSubjectName.setText(mark.getSubjectName());
        holder.tvTotalMarks.setText(mark.getTotalMarks());
        holder.tvObtainedMarks.setText(mark.getObtainMarks());

    }

    @Override
    public int getItemCount() {
        return marks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSubjectName, tvTotalMarks, tvObtainedMarks;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvObtainedMarks = itemView.findViewById(R.id.tv_obtained);
            tvTotalMarks = itemView.findViewById(R.id.tv_total);
        }
    }
}
