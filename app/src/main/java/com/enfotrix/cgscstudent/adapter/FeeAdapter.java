package com.enfotrix.cgscstudent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enfotrix.cgscstudent.R;
import com.enfotrix.cgscstudent.model.Fee;

import java.util.List;

public class FeeAdapter extends RecyclerView.Adapter<FeeAdapter.ViewHolder> {
    private Context context;
    private List<Fee> feeList;

    public FeeAdapter(Context context, List<Fee> feeList) {
        this.context = context;
        this.feeList = feeList;
    }

    @NonNull
    @Override
    public FeeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.student_fee_rv, null);
        FeeAdapter.ViewHolder viewHolder = new FeeAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeeAdapter.ViewHolder holder, int position) {

        Fee fee = feeList.get(position);

        holder.studentName.setText(fee.getStudentName());
        holder.feeAmount.setText(fee.getPayableAmount());
        holder.feeMonth.setText(fee.getMonth());

    }

    @Override
    public int getItemCount() {
        return feeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, feeMonth;
        EditText feeAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            studentName = itemView.findViewById(R.id.tv_student_name);
            feeMonth = itemView.findViewById(R.id.tv_month);
            feeAmount = itemView.findViewById(R.id.et_fee);

        }
    }
}
