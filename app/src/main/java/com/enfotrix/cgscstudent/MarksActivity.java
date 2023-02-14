package com.enfotrix.cgscstudent;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.enfotrix.cgscstudent.adapter.MarksAdapter;
import com.enfotrix.cgscstudent.model.Exam;
import com.enfotrix.cgscstudent.model.Marks;
import com.enfotrix.cgscstudent.model.Subject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MarksActivity extends AppCompatActivity {

    private static final String TAG = "MarksActivity";

    private SharedPrefManager sharedPrefManager;
    private FirebaseFirestore db;


    private List<Exam> exams;
    private List<Subject> subjects; // Subjects of current student
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Spinner spinExams, spinCats;
    private CollectionReference resultsCollection;
    private List<Marks> marks;
    private MarksAdapter adapter;

    int totalSubjectMarks = 0, totalObtainedMarks = 0;
    private TextView tvTotalMarks, tvObtainedMarks;
    private ImageView ivBack;

    private String examCtg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);


        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            examCtg = bundle.getString("exam");
        }
//        spinCats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                getExams(spinCats.getSelectedItem().toString());
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


        // Get Subjects List of current Student
        getExams(examCtg);
        getSubjects();

    }

    private void getSubjects() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Subjects")
                .whereArrayContains("SectionsIDs", sharedPrefManager.getStudent().getCurrentSection())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                subjects = task.getResult().toObjects(Subject.class);
                            }
                        }
                    }
                });
    }

    private void getExams(String category) {

        exams.clear();
        db.collection("Exams")
                .whereEqualTo("ExamCtg", examCtg)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            exams = task.getResult().toObjects(Exam.class);
                            initExamsSpinner();
                        }
                    }
                });

    }

    public void init() {

        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvTotalMarks = findViewById(R.id.tv_total);
        tvObtainedMarks = findViewById(R.id.tv_obtained);
        spinCats = findViewById(R.id.spin_cat);
        sharedPrefManager = new SharedPrefManager(this);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.rv_students);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        progressBar = findViewById(R.id.progress_bar);
        spinExams = findViewById(R.id.spin_exams);
        marks = new ArrayList<>();
        exams = new ArrayList<>();
        subjects = new ArrayList<>();

    }

    public void initExamsSpinner() {

        ArrayAdapter sectionsAdapter
                = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, exams);


        exams.add(0, new Exam("Select Exam"));
        spinExams.setAdapter(sectionsAdapter);
        spinExams.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.hint));
                ((TextView) view).setTypeface(ResourcesCompat.getFont(MarksActivity.this, R.font.poppinsregular));

                if (spinExams.getSelectedItemPosition() != 0) {
                    Exam exam = ((Exam) spinExams.getSelectedItem());
                    if (subjects.size() > 0) {
                        progressBar.setVisibility(View.VISIBLE);
                        fetchMarks(exam);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void fetchMarks(Exam exam) {
        totalObtainedMarks = 0;
        totalSubjectMarks = 0;

        /*resultsCollection = db.collection("Result")
                .document("2022-23")
                .collection("ResultExam")
                .document(exam.getID()) // exam id
                .collection("ResultSections")
                .document(sharedPrefManager.getStudent().getCurrentSection()) // Section Id of current Student
                .collection("ResultSubjects");*/

        String session="2022-23";

        marks.clear();

        progressBar.setVisibility(View.VISIBLE);

        totalSubjectMarks=0;
        totalObtainedMarks=0;
        for (int i = 0; i < subjects.size(); i++) {
            int finalI = i;
            String docID=session+exam.getID()+subjects.get(i).getID()+sharedPrefManager.getStudent().getStudentId();

            db.collection("Result").document(docID).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            //task.getResult().toObject()

                                            Marks marks1 = task.getResult().toObject(Marks.class);
                                            marks.add(marks1);
                                            totalSubjectMarks += Integer.parseInt(marks1.getTotalMarks().trim());
                                            totalObtainedMarks += Integer.parseInt(marks1.getObtainMarks().trim());


                                            //tvAttendance.setText(task.getResult().getString("Status"));

                                            tvTotalMarks.setText(totalSubjectMarks + "");
                                            tvObtainedMarks.setText(totalObtainedMarks + "");
                                        }
                                        if (finalI == subjects.size() - 1) {
                                            progressBar.setVisibility(View.GONE);
                                            setAdapter();
                                        }
                                    }


                                }
                            });


            /*resultsCollection.document(subjects.get(i).getID()) // Subject Id
                    .collection("ResultStudents")
                    .whereEqualTo("StudentId", sharedPrefManager.getStudent().getStudentId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        if (doc != null) {
                                            Marks marks1 = doc.toObject(Marks.class);
                                            marks.add(marks1);
                                            totalSubjectMarks += Integer.parseInt(marks1.getTotalMarks().trim());
                                            totalObtainedMarks += Integer.parseInt(marks1.getObtainMarks().trim());
                                        }
                                    }
                                }
                                if (finalI == subjects.size() - 1) {
                                    progressBar.setVisibility(View.GONE);
                                    setAdapter();
                                }

                            }
                        }
                    });*/
        }


    }

    public void setAdapter() {
        if (marks.size() > 0) {
            adapter = new MarksAdapter(this, marks);
            recyclerView.setAdapter(adapter);

        } else {
            Toast.makeText(this, "No Record found", Toast.LENGTH_SHORT).show();
        }
    }
}