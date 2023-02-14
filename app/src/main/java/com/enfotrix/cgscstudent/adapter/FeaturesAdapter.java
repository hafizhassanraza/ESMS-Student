package com.enfotrix.cgscstudent.adapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.enfotrix.cgscstudent.AttendanceActivity;
import com.enfotrix.cgscstudent.MarksActivity;
import com.enfotrix.cgscstudent.model.Exam;
import com.enfotrix.cgscstudent.model.Feature;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.enfotrix.cgscstudent.R;

import java.util.Calendar;
import java.util.List;

public class FeaturesAdapter extends RecyclerView.Adapter<FeaturesAdapter.DashboardViewHolder> {
    private Context mContext;
    private List<Feature> categories;


    public FeaturesAdapter(Context mContext, List<Feature> categories) {
        this.mContext = mContext;
        this.categories = categories;


    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DashboardViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_feature, null));
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {
        Feature feature = categories.get(position);
        holder.ivCategoryIcon.setImageResource(feature.getResourceId());
        holder.tvCategoryName.setText(categories.get(position).getFeatureName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (feature.getResourceId()) {
                    case R.drawable.ic_attendance2:
                        mContext.startActivity(new Intent(mContext, AttendanceActivity.class));
                        break;


                    case R.drawable.ic_marks:
                        Dialog dialog = new Dialog(mContext);
                        dialog.setContentView(R.layout.dialog_select_exam);
                        dialog.setCancelable(true);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));


                        Spinner spinSession = dialog.findViewById(R.id.spin_session);
                        RadioGroup rdExam = dialog.findViewById(R.id.rg_attendance);
                        Button btnContinue = dialog.findViewById(R.id.btn_yes);


                        btnContinue.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int checkedId = rdExam.getCheckedRadioButtonId();
                                String exam = null;
                                if (checkedId == R.id.rd_present){
                                    exam = "General Exam";
                                } else if (checkedId == R.id.rd_leave){
                                    exam = "Phase Test";
                                }
                                mContext.startActivity(new Intent(mContext, MarksActivity.class)
                                .putExtra("exam",exam));
                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public class DashboardViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName;
        private ImageView ivCategoryIcon;
        private CardView cardView;

        public DashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_sales_manager);
            tvCategoryName = itemView.findViewById(R.id.tv_category_text);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
        }
    }
}