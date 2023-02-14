package com.enfotrix.cgscstudent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.utils.Utils;
import com.enfotrix.cgscstudent.adapter.Adapter_Feedback;
import com.enfotrix.cgscstudent.model.Model_Feedback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    List<Model_Feedback> list_Feedback = new ArrayList<>();
    private FirebaseFirestore firestore;
    private EditText textFeedback, headingFeedback;
    private Button submitFeedback;
    SharedPrefManager sharedPrefManager;
    int feedbacknumber = 0;

    RecyclerView recyc_Feedback;
    private String PhoneNumber, SectionName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        init();


        submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEmpty())
                    addFeedback();
            }
        });


        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //  fetchFeedback(utils.getToken());
                pullToRefresh.setRefreshing(false);
            }
        });
    }


    public void init() {

        textFeedback = findViewById(R.id.text_feedback);
        headingFeedback = findViewById(R.id.feedback_heading);
        recyc_Feedback = findViewById(R.id.list_Feedback);
        submitFeedback = findViewById(R.id.btn_submit_feedback);
        firestore = FirebaseFirestore.getInstance();
        sharedPrefManager = new SharedPrefManager(FeedbackActivity.this);
        fetchFeedback();

        recyc_Feedback = findViewById(R.id.list_Feedback);
        recyc_Feedback.setHasFixedSize(true);
        recyc_Feedback.setLayoutManager(new LinearLayoutManager(this));


        getFatherPhoneNumber();
        getSectionName();

    }

    private void getSectionName() {
        firestore.collection("Class")
                .document(sharedPrefManager.getStudent().getCurrentClass()).collection("Sections")
                .document(sharedPrefManager.getStudent().getCurrentSection()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        SectionName = documentSnapshot.getString("SectionName");


                    }
                });
    }

    private void getFatherPhoneNumber() {

        firestore.collection("Students").document(sharedPrefManager.getStudent().getStudentId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isComplete()) {
                            DocumentSnapshot document = task.getResult();
                            PhoneNumber = document.getString("FatherPhoneNumber");
                        }
                    }
                });
    }


    private void fetchFeedback() {


        firestore.collection("Feedback").whereEqualTo("studentid", sharedPrefManager.getStudent().getStudentId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Model_Feedback model_feedback = new Model_Feedback(
                                        documentSnapshot.getId(),
                                        documentSnapshot.getString("data"),
                                        documentSnapshot.getString("date"),
                                        documentSnapshot.getString("heading"),
                                        documentSnapshot.getString("studentid"));
                                list_Feedback.add(model_feedback);
                            }


                            Collections.sort(list_Feedback, new Comparator<Model_Feedback>() {
                                @Override
                                public int compare(Model_Feedback feedback, Model_Feedback t1) {
                                    return feedback.getDate().trim().compareToIgnoreCase(t1.getDate().trim());
                                }
                            });

                            Adapter_Feedback adapter_feedback = new Adapter_Feedback(list_Feedback);
                            recyc_Feedback.setAdapter(adapter_feedback);
                            adapter_feedback.notifyDataSetChanged();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FeedbackActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void addFeedback() {


        Map<String, Object> map = new HashMap<>();
        map.put("data", textFeedback.getText().toString());
        map.put("date", getDate());
        map.put("heading", headingFeedback.getText().toString());
        map.put("ClassName", sharedPrefManager.getStudent().getCurrentClass());
        map.put("SectionId", sharedPrefManager.getStudent().getCurrentSection());
        map.put("Status", "Pending");
        map.put("StudentName", sharedPrefManager.getStudent().getFirstName());
        map.put("studentid", sharedPrefManager.getStudent().getStudentId());
        map.put("FatherPhoneNumber", PhoneNumber);
        map.put("SectionName", SectionName);


        firestore.collection("Feedback")
                .add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        textFeedback.setText(null);
                        headingFeedback.setText(null);
                        Toast.makeText(FeedbackActivity.this, "Feedback Submitted Successfully", Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FeedbackActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private String getDate() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private boolean checkEmpty() {
        Boolean isEmpty = false;
        if (textFeedback.getText().toString().trim().isEmpty())
            Toast.makeText(getApplicationContext(), "Please write feedback", Toast.LENGTH_SHORT).show();
        else isEmpty = true;
        return isEmpty;
    }


}