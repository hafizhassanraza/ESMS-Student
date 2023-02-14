package com.enfotrix.cgscstudent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.enfotrix.cgscstudent.adapter.Adapter_Feedback;
import com.enfotrix.cgscstudent.adapter.NotificationsAdapter;
import com.enfotrix.cgscstudent.model.ModelNotification;
import com.enfotrix.cgscstudent.model.Model_Feedback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView recyc_notifications;
    List<ModelNotification> list_Notifications = new ArrayList<>();
    private FirebaseFirestore firestore;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        init();
        getNotifications();
    }

    private void getNotifications() {
        firestore.collection("Notifications").whereEqualTo("StudentID", sharedPrefManager.getStudent().getStudentId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        list_Notifications = task.getResult().toObjects(ModelNotification.class);

                        NotificationsAdapter adapter_notification = new NotificationsAdapter(list_Notifications);
                        recyc_notifications.setAdapter(adapter_notification);
                        adapter_notification.notifyDataSetChanged();

                    }
                });

    }

    public void init() {
        firestore = FirebaseFirestore.getInstance();
        sharedPrefManager = new SharedPrefManager(NotificationsActivity.this);


        recyc_notifications = findViewById(R.id.recyc_notifications);
        recyc_notifications.setHasFixedSize(true);
        recyc_notifications.setLayoutManager(new LinearLayoutManager(this));


    }
}