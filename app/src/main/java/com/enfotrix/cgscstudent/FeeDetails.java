package com.enfotrix.cgscstudent;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.enfotrix.cgscstudent.adapter.Adapter_Feedback;
import com.enfotrix.cgscstudent.adapter.FeeAdapter;
import com.enfotrix.cgscstudent.model.Fee;
import com.enfotrix.cgscstudent.model.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FeeDetails extends AppCompatActivity {
    TextView admissionFee, discountAmount, dues, examFee, fine;
    RecyclerView recyclerView;


    private Student student;
    private SharedPrefManager sharedPrefManager;
    private FirebaseFirestore db;

    List<Fee> feeArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_details);

        init();


    }


    public void init() {

        admissionFee = findViewById(R.id.txtadmissionfee);
        discountAmount = findViewById(R.id.txtdiscountamount);
        dues = findViewById(R.id.txtdues);
        examFee = findViewById(R.id.txtexamfee);
        fine = findViewById(R.id.txtfine);
        recyclerView = findViewById(R.id.rvFeeOld);

        sharedPrefManager = new SharedPrefManager(this);
        db = FirebaseFirestore.getInstance();
        student = sharedPrefManager.getStudent();


        getFeeDetails();
        getAllMonthsFee();

    }

    public void getFeeDetails() {

        String year1 = null;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Year year = Year.now();
            year1 = String.valueOf(year);

        }
        String month_name = month_date.format(cal.getTime());

        String document = month_name + year1 + student.getStudentId();


        db.collection("Fee").document(document).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                admissionFee.setText(documentSnapshot.getString("AdmissionFee"));
                discountAmount.setText(documentSnapshot.getString("DiscountAmount"));
                dues.setText(documentSnapshot.getString("Dues"));
                examFee.setText(documentSnapshot.getString("ExamFee"));
                fine.setText(documentSnapshot.getString("Fine"));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getAllMonthsFee() {

        db.collection("Fee").whereEqualTo("StudentID", sharedPrefManager.getStudent().getStudentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                feeArrayList = task.getResult().toObjects(Fee.class);

            }
        });

        FeeAdapter feeAdapter = new FeeAdapter(getApplicationContext(), feeArrayList);
        recyclerView.setAdapter(feeAdapter);
        feeAdapter.notifyDataSetChanged();
    }
}