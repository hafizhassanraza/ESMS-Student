package com.enfotrix.cgscstudent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.enfotrix.cgscstudent.model.Student;
import com.enfotrix.cgscstudent.model.StudentAttendance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AttendanceActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private static final String TAG = "AttendanceActivity";
    private FirebaseFirestore db;
    private SharedPrefManager sharedPrefManager;
    private ProgressBar progressBar;
    private List<StudentAttendance> attendanceList;
    private Student student;

    private TextView tvTotalPresents, tvTotalAbsents, tvTotalLeaves;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        init();
        student = sharedPrefManager.getStudent();
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                try {
                    Field currentMonthField = MaterialCalendarView.class.getDeclaredField("currentMonth");
                    currentMonthField.setAccessible(true);
                    int currentMonth = ((CalendarDay) currentMonthField.get(widget)).getMonth();
                    fetchAttendance(currentMonth);

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // Failed to get field value, maybe library was changed.
                }
            }
        });


        fetchAttendance((Calendar.getInstance().get(Calendar.MONTH)) + 1);


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchAttendance(int currentMonth) {

        progressBar.setVisibility(View.VISIBLE);

        String currentMonthString = String.format("%02d", currentMonth);
        Log.d(TAG, "fetchAttendance: " + currentMonthString);

        CollectionReference monthRef = db.collection("Attendance");
//                .document("2022-23")
//                .collection("AttendanceMonth")
//                .document(currentMonthString)
//                .collection("AttendanceDate");

        attendanceList.clear();

        tvTotalAbsents.setText("");
        tvTotalLeaves.setText("");
        tvTotalPresents.setText("");


        List<Task<Stream<StudentAttendance>>> fetchStudentsTasks = new ArrayList<Task<Stream<StudentAttendance>>>(31);

        for (int i = 0; i <= 31; i++) {
            // for each computed date, fetch the students who attended on those dates
            String date = String.format("%02d", i) + "-" + String.format("%02d", currentMonth) + "-" + 2022;
            fetchStudentsTasks.add(
                    monthRef
                            .whereEqualTo("StudentID", student.getStudentId())
                            .whereEqualTo("Date", date)// omitting this line would get all students in attendance
                            .get(Source.SERVER)
                            .continueWith(t -> { // continueWith manipulates completed tasks, returning a new task with the given result
                                // The t.getResult() below will throw an error if the get
                                // documents task failed, effectively rethrowing it, which
                                // is OK here. If you want to ignore any failed queries,
                                // return Stream.empty() when t.isSuccessful() is false.
                                return StreamSupport
                                        .stream(t.getResult().spliterator(), false) // streams the documents in the snapshot so we can iterate them
                                        .map(document -> { // take each document and convert it to a StudentAttendance instance, with the right date
                                            StudentAttendance studentAttendance = document.toObject(StudentAttendance.class);
                                            studentAttendance.setDate(date);
                                            return studentAttendance;
                                        }); // this returned Stream is 'open', waiting for further processing
                            })
            );
        }

        Tasks.whenAll(fetchStudentsTasks)
                .addOnSuccessListener(unused -> {
                    // if here, all queries returned successfully, update the list
                    attendanceList = fetchStudentsTasks
                            .stream()
                            .flatMap(t -> t.getResult()) // pull out the streams of StudentAttendance instances and merge them into one stream (closing each child stream when finished)
                            .collect(Collectors.toList()); // join all the StudentAttendance instances into one large ordered list

                    Log.d(TAG, "onComplete: " + attendanceList.size());
                    progressBar.setVisibility(View.INVISIBLE);
                    if (attendanceList.size() > 0) {
                        drawDecorators();
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    private void drawDecorators() {
        int presents = 0, leaves = 0, absents = 0;
        for (StudentAttendance attendance : attendanceList) {
            CalendarDay calendarDay = CalendarDay.from(Integer.parseInt(attendance.getDate().substring(6, 10)),
                    Integer.parseInt(attendance.getDate().substring(3, 5)),
                    Integer.parseInt(attendance.getDate().substring(0, 2)));
            calendarView.addDecorator(new SaleDateDecorator(AttendanceActivity.this, calendarDay, attendance.getStatus()));

            if (attendance.getStatus().equalsIgnoreCase("Present")) {
                presents++;
            } else if (attendance.getStatus().equalsIgnoreCase("Absent")) {
                absents++;
            } else if (attendance.getStatus().equalsIgnoreCase("Leave")) {
                leaves++;
            }

        }
        calendarView.invalidateDecorators();
        tvTotalAbsents.setText(absents + "");
        tvTotalPresents.setText(presents + "");
        tvTotalLeaves.setText(leaves + "");
    }

    public void init() {
        tvTotalPresents = findViewById(R.id.tv_total_presents);
        tvTotalAbsents = findViewById(R.id.tv_total_absents);
        tvTotalLeaves = findViewById(R.id.tv_total_leaves);
        attendanceList = new ArrayList<>();
        progressBar = findViewById(R.id.progress_bar);
        sharedPrefManager = new SharedPrefManager(this);
        calendarView = findViewById(R.id.calendarView);
        db = FirebaseFirestore.getInstance();

    }
}