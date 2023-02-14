 package com.enfotrix.cgscstudent.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.enfotrix.cgscstudent.R;
import com.enfotrix.cgscstudent.model.ModelNotification;
import com.enfotrix.cgscstudent.model.Model_Feedback;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List<ModelNotification> model_Nofitications;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    // RecyclerView recyclerView;
    public NotificationsAdapter(List<ModelNotification> model_Nofitications) {
        this.model_Nofitications = model_Nofitications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_notifications, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ModelNotification current_Notifications = model_Nofitications.get(position);
        holder.txt_heading.setText(current_Notifications.getHeading());
        holder.txt_notification.setText(current_Notifications.getData());
        String status = current_Notifications.getStatus();


        //holder.layout_Feedback.setBackgroundColor(Color.TRANSPARENT);// setBackgroundColor(Color.parseColor("#9F000000"));

//        holder.layout_Withdraw_req.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               /* Intent intent = new Intent(view.getContext(), ActivityNotificationDetails.class);
//                intent.putExtra("id", current_Feedback.getNotificatioID().toString().trim());
//                view.getContext().startActivity(intent);*/
//                //Toast.makeText(view.getContext(),"click on item: "+current_Feedback.getAmount().toString().trim(), Toast.LENGTH_LONG).show();
//                //Toast.makeText(view.getContext(),"click on item: "+current_investor.get_name_Investor(), Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return model_Nofitications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public TextView txt_heading, txt_notification;
        public LinearLayout layout_Feedback;

        public ViewHolder(View itemView) {
            super(itemView);
            this.txt_heading = (TextView) itemView.findViewById(R.id.txt_header_notify);
            this.txt_notification = (TextView) itemView.findViewById(R.id.txt_notification);
            //layout_Feedback = (LinearLayout)itemView.findViewById(R.id.layout_Notify);
        }

    }
}


