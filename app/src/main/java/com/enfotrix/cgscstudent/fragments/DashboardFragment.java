package com.enfotrix.cgscstudent.fragments;

import static android.app.Activity.RESULT_OK;
import static android.provider.Telephony.TextBasedSmsColumns.BODY;
import static android.provider.Telephony.TextBasedSmsColumns.SUBJECT;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.core.app.DialogCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.enfotrix.cgscstudent.AttendanceActivity;
import com.enfotrix.cgscstudent.DatesheetActivity;
import com.enfotrix.cgscstudent.FeeDetails;
import com.enfotrix.cgscstudent.FeedbackActivity;
import com.enfotrix.cgscstudent.MarksActivity;
import com.enfotrix.cgscstudent.R;
import com.enfotrix.cgscstudent.SharedPrefManager;
import com.enfotrix.cgscstudent.adapter.Adapter_Feedback;
import com.enfotrix.cgscstudent.adapter.TimetableAdapter;
import com.enfotrix.cgscstudent.model.Fee;
import com.enfotrix.cgscstudent.model.Section;
import com.enfotrix.cgscstudent.model.Student;
import com.enfotrix.cgscstudent.model.StudentAttendance;
import com.enfotrix.cgscstudent.model.Subject;
import com.enfotrix.cgscstudent.model.Timetable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DashboardFragment extends Fragment {
    PieChart pieChart;

    private TextView studentName, fatherName, regNumber, classSection, classMedium, presents_percentage;
    private TextView feestatus, feeamount;
    private ImageView uploadImageIV, profilePic;
    private String txtsession, txtexamtype, txtType, txtExam;
    private ArrayList<String> examtype;
    private ArrayList<String> examctg;
    private ArrayList<String> sessions;
    private List<Timetable> timetables = new ArrayList<>();


    int totalpresents = 0, totalabsents = 0, totalleaves = 0;

    RelativeLayout contactUsCard, feedbackCard, datesheet;
    LinearLayout feedetails;
    private String contacttxt;
    private ArrayList<String> departmentname;
    private String contact_num, contact_email, contact_landline, contact_whatsapp;
    private String tittle, cu_email1, cu_mobileNo1, cu_whatsapp1, cu_landline1;


    RecyclerView timetablerecycler;
    TimetableAdapter timetableAdapter;


    private String StudentName, FatherName, RegNumber, ClassSection, CurrentClass, ClassMedium;
    private SharedPrefManager sharedPrefManager;
    private FirebaseFirestore db;
    private Student student;
    private Section section;
    private List<Timetable> timetable;
    private List<StudentAttendance> attendanceList1;
    private int presents = 0, absents = 0, leaves = 0;


    // Uri indicates, where the image will be picked from
    private Uri filePath;
    private Uri profileLink;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;


    private RelativeLayout RlayAttendance, RlayResult;
    private String ClassSection1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        init(view);


        getSessions();
        getAttendance();
        studentData();
        getClassDetail();
        getSubjects();
        fetchdepartment();
        getData();
        getFeeDetails();


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgProfile();
            }
        });

        feedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FeeDetails.class));
            }
        });


        RlayAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AttendanceActivity.class));
            }
        });
        RlayResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultDialog();
            }
        });

        setData();


        if (getActivity() != null) {
        }

        return view;

    }

    private void imgProfile() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


    }

    private void init(View view) {
        if (getActivity() != null) {
            sharedPrefManager = new SharedPrefManager(getContext());
        }

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        departmentname = new ArrayList<>();


        sessions = new ArrayList<>();
        examtype = new ArrayList<>();
        examctg = new ArrayList<>();
        datesheet = view.findViewById(R.id.rl_datesheet);

        presents_percentage = view.findViewById(R.id.presentsprecentage);

        studentName = view.findViewById(R.id.student_name);
        fatherName = view.findViewById(R.id.father_name);
        regNumber = view.findViewById(R.id.reg_number);
        classSection = view.findViewById(R.id.class_section);
        classMedium = view.findViewById(R.id.class_medium);
        profilePic = view.findViewById(R.id.card_profile_pic);
        pieChart = view.findViewById(R.id.piechart);
        contactUsCard = view.findViewById(R.id.contact_us);


        feestatus = view.findViewById(R.id.txt_fee_status);
        feeamount = view.findViewById(R.id.txt_fee_amount);

        RlayAttendance = view.findViewById(R.id.RlayAttendance);
        RlayResult = view.findViewById(R.id.RlayResult);
        //tvStudentName = view.findViewById(R.id.tv_student_name);
        timetablerecycler = view.findViewById(R.id.recycler_timetable);
        feedbackCard = view.findViewById(R.id.feedback_card);
        feedetails = view.findViewById(R.id.feedetails);

        timetable = new ArrayList<>();
        timetableAdapter = new TimetableAdapter(getContext(), timetable);
        timetablerecycler.setHasFixedSize(true);
        timetablerecycler.setAdapter(timetableAdapter);
        timetablerecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        feedbackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), FeedbackActivity.class));
            }
        });

        datesheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultdialog();
            }
        });

        contactUsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactUs();

            }
        });


    }


    private void ResultDialog() {
        Dialog dialog = new Dialog(getContext());
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
                if (checkedId == R.id.rd_present) {
                    exam = "General Exam";
                } else if (checkedId == R.id.rd_leave) {
                    exam = "Phase Test";
                }
                startActivity(new Intent(getContext(), MarksActivity.class)
                        .putExtra("exam", exam));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void setData() {
        pieChart.addPieSlice(
                new PieModel(
                        "Present",
                        totalpresents,
                        Color.parseColor("#76DD7B")
                ));
        pieChart.addPieSlice(
                new PieModel(
                        "Absent",
                        totalabsents,
                        Color.parseColor("#EB3F62")));
        pieChart.addPieSlice(
                new PieModel(
                        "Leave",
                        totalleaves,
                        Color.parseColor("#61B8E0")
                ));
        pieChart.startAnimation();
    }


    public void studentData() {


        student = sharedPrefManager.getStudent();

        StudentName = student.getFirstName() + " " + student.getLastName();
        FatherName = student.getFatherName();
        RegNumber = student.getRegNumber();
        ClassSection = student.getCurrentSection();
        CurrentClass = student.getCurrentClass();


        classMedium.setText(ClassMedium);
        studentName.setText(StudentName);
        fatherName.setText(FatherName);
        regNumber.setText(RegNumber);


    }

    public void getClassDetail() {


        db.collection("Class")
                .document(CurrentClass).collection("Sections").document(ClassSection).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Section section = new Section(
                                documentSnapshot.getString("DocID"),
                                documentSnapshot.getString("Medium"),
                                documentSnapshot.getString("SectionName")
                        );

                        sharedPrefManager.saveSection(section);

                        ClassSection1 = documentSnapshot.getString("SectionName");
                        ClassMedium = documentSnapshot.getString("Medium");
                        classSection.setText(CurrentClass + " " + ClassSection1);
                        classMedium.setText(ClassMedium);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void getSubjects() {
        db.collection("Subjects")
                .whereArrayContains("SectionsIDs", sharedPrefManager.getStudent().getCurrentSection())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                    //task complete
                                    String SubjectID = documentSnapshot.getString("ID");
                                    String timetableDoc = sharedPrefManager.getStudent().getCurrentSection() + SubjectID;


                                    db.collection("Timetable")
                                            .document(timetableDoc)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task1) {

                                                    if (task1.isComplete()) {

                                                        DocumentSnapshot documentSnapshot1 = task1.getResult();

                                                        Timetable timetable = new Timetable();
                                                        timetable.setSubject(documentSnapshot1.getString("Subject"));
                                                        timetable.setEndtime(documentSnapshot1.getString("Endtime"));
                                                        timetable.setStarttime(documentSnapshot1.getString("Starttime"));

                                                        timetables.add(timetable);

                                                    }

                                                    TimetableAdapter adapter_feedback = new TimetableAdapter(getContext(), timetables);
                                                    timetablerecycler.setAdapter(adapter_feedback);
                                                    adapter_feedback.notifyDataSetChanged();

                                                }
                                            });
                                }


                            }
                        }
                    }
                });
    }

    public void getAttendance() {


        db.collection("Attendance")
                .whereEqualTo("StudentID", sharedPrefManager.getStudent().getStudentId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot doc : task.getResult()) {

                            String tempDate = doc.getString("Date");
                            if (tempDate != null) {
                                String sysMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 + "";

                                String month = tempDate.substring(3, 5);
                                String year = tempDate.substring(6, 10);
                                if (year.equals("2022"))
                                    if (sysMonth.equals(month)) {

                                        if (doc.getString("Status").equals("Present")) {
                                            totalpresents++;
                                        }
                                        if (doc.getString("Status").equals("Absent")) {
                                            totalabsents++;
                                        }
                                        if (doc.getString("Status").equals("Leave")) {
                                            totalleaves++;
                                        }
                                        // Toast.makeText(getContext(), ""+doc.getString("Status"), Toast.LENGTH_SHORT).show();
                                    }
                                //Toast.makeText(getContext(), month+ " "+ year, Toast.LENGTH_SHORT).show();
                            }

                        }

                        if (task.isComplete()) {
                            setData();


                            int total = totalpresents + totalabsents + totalleaves;
                            int percentage = (int) (((double) totalpresents / (double) total) * 100);
                            presents_percentage.setText(percentage + "%");

                        }

                    }
                });
    }


    private void contactUs() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View attendancedialog = getLayoutInflater().inflate(R.layout.department_dialog, null);

        AppCompatSpinner txtContact = attendancedialog.findViewById(R.id.txtContact);
        AppCompatButton btn_Contact = attendancedialog.findViewById(R.id.btn_Contact);


        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.dropdown_department, departmentname);
//        txtContact.setText(arrayAdapter.getItem(0).toString(), false);
        txtContact.setAdapter(arrayAdapter);

        txtContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                contacttxt = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomsheetcontact(contacttxt);

            }
        });

        builder.setView(attendancedialog);
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void bottomsheetcontact(String dptName) {

        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        View vie = getLayoutInflater().inflate(R.layout.bottom_sheet_contact, null);


        TextView txt_cu_departmentName1 = vie.findViewById(R.id.txt_pri);
        ImageView img_cu_call1 = vie.findViewById(R.id.img_cu_call1);
        ImageView img_cu_landline1 = vie.findViewById(R.id.img_cu_landline1);
        ImageView img_cu_mail1 = vie.findViewById(R.id.img_cu_mail1);
        ImageView img_cu_whatsapp1 = vie.findViewById(R.id.img_cu_whatsapp1);
        CardView cv_mobile = vie.findViewById(R.id.cv_mobile);
        CardView cv_landline = vie.findViewById(R.id.cv_landline);
        CardView cv_whatsapp = vie.findViewById(R.id.cv_whatsapp);
        CardView cv_email = vie.findViewById(R.id.cv_email);

        db.collection("Contacts").document(String.valueOf(dptName))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            DocumentSnapshot documentSnapshot = task.getResult();

                            cu_mobileNo1 = documentSnapshot.getString("cell");
                            cu_email1 = documentSnapshot.getString("email");
                            cu_whatsapp1 = documentSnapshot.getString("whatsapp");
                            cu_landline1 = documentSnapshot.getString("landline");
                            //  tittle = documentSnapshot.getString("tittle");
                            // txt_cu_departmentName1.setText(tittle);

                            /////////////////////////////////////////
                            if (cu_mobileNo1 != null) {
                                contact_num = cu_mobileNo1;
                            } else {
                                cv_mobile.setVisibility(View.GONE);
                            }
                            ////////////////////////////////////////////
                            if (cu_email1 != null) {
                                contact_email = cu_email1;
                            } else {
                                cv_email.setVisibility(View.GONE);
                            }
                            /////////////////////////////////////////////
                            if (cu_whatsapp1 != null) {
                                contact_whatsapp = cu_whatsapp1;
                            } else {
                                cv_whatsapp.setVisibility(View.GONE);
                            }
                            /////////////////////////////////////////
                            if (cu_landline1 != null) {
                                contact_landline = cu_landline1;
                            } else {
                                cv_landline.setVisibility(View.GONE);
                            }


//                            Toast.makeText(getContext(), "" + cellnumber + email + landline + tittle + whatsapp, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        dialog.setContentView(vie);
        dialog.setCancelable(true);
        dialog.show();


        img_cu_call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // use country code with your phone number
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + contact_num));
                startActivity(i);

            }
        });

        img_cu_landline1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + contact_landline));
                startActivity(i);
            }
        });

        img_cu_mail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", contact_email, null));
                i.putExtra(Intent.EXTRA_SUBJECT, SUBJECT);
                i.putExtra(Intent.EXTRA_TEXT, BODY);
                startActivity(i);
            }
        });

        img_cu_whatsapp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "https://api.whatsapp.com/send?phone=" + contact_whatsapp;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

    }

    private void fetchdepartment() {

        db.collection("Contacts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                departmentname.add(document.getId());


                                //Toast.makeText(getContext(), "Debug", Toast.LENGTH_SHORT).show();
//                                Toast.makeText(getContext(), "" + document.getId(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                profilePic.setImageBitmap(bitmap);

                updateUser(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    ///////////////////////////////////////////////////////////////


    private void updateUser(Uri filePath) {


        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();


        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Map<String, Object> m = new HashMap<>();
                                m.put("StudentPicture", uri.toString());


                                db.collection("Students").document(sharedPrefManager.getStudent().getStudentId()).update(m)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                getData();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        //Toast.makeText(ActivityNominee.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploading Data " + (int) progress + "%");
                    }
                });


    }


    public void getData() {

        db.collection("Students").document(sharedPrefManager.getStudent().getStudentId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        String student_profilePicFromDb = document.getString("StudentPicture");

                        if (student_profilePicFromDb == null) {

                        } else
                            Glide.with(getContext()).load(student_profilePicFromDb).into(profilePic);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void resultdialog() {

        txtType = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = getLayoutInflater().inflate(R.layout.result_dialog, null);

        AppCompatSpinner spinner = view.findViewById(R.id.autoCompletetxt);
        AppCompatSpinner spnType = view.findViewById(R.id.text_type);
        AppCompatSpinner spnExam = view.findViewById(R.id.text_exam);
        AppCompatButton btn_getResult = view.findViewById(R.id.btn_getResult);

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.dropdown_session, sessions);
//        autoCompletetxt.setText(arrayAdapter.getItem(0).toString(), false);
        spinner.setAdapter(arrayAdapter);


       /* ArrayAdapter Adapter = new ArrayAdapter(getContext(), R.layout.dropdown_examtype, examtype);
        spinner1.setAdapter(Adapter);*/
//        text_examtype.setText(Adapter.getItem(0).toString(), false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                txtsession = adapterView.getItemAtPosition(i).toString();

                if (txtsession != null && txtType != null)
                    fetchresult(spnExam, txtsession, txtType);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                txtType = adapterView.getItemAtPosition(i).toString();

                if (txtsession != null && txtType != null) {

                    fetchresult(spnExam, txtsession, txtType);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });

        spnExam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                txtExam = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btn_getResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    if (!txtExam.isEmpty() || txtExam != null) {
                        Intent resultIntent = new Intent(getActivity().getApplicationContext(), DatesheetActivity.class);


                        resultIntent.putExtra("session", txtsession);
                        resultIntent.putExtra("type", txtType);
                        resultIntent.putExtra("exam", txtExam);
                        startActivity(resultIntent);
                    } else
                        Toast.makeText(getContext(), "Please Select Exam!", Toast.LENGTH_SHORT).show();
                }


//                Toast.makeText(getContext(), classid + "\n" + classgrade, Toast.LENGTH_SHORT).show();

            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();


    }


    private void fetchresult(AppCompatSpinner spinner, String sessionstxt, String type) {

        examtype.clear();
        spinner.setAdapter(null);
        //Toast.makeText(getContext(), sessionstxt, Toast.LENGTH_SHORT).show();
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        db.collection("Exams").whereEqualTo("ExamCtg", type).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            examtype.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (document.getString("ExamCtg").equals(type)) {
                                    examtype.add(document.getString("ExamName"));
                                    ArrayAdapter Adapter = new ArrayAdapter(getContext(), R.layout.dropdown_examtype, examtype);
                                    Adapter.notifyDataSetChanged();
                                    spinner.setAdapter(Adapter);
                                }
                            }
                            progressDialog.dismiss();

                        }
                    }
                });

    }


    public ArrayList<String> getSessions() {

        db.collection("Session").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                    String sessionName = documentSnapshot.getString("SessionName");
                    sessions.add(sessionName);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });


        return sessions;
    }
/*
    public ArrayList<String> getExamctg(AppCompatSpinner spinner) {
        db.collection("Exams").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String examCgt = document.getString("ExamCgt");
                        examctg.add(examCgt);

                        ArrayAdapter arrayAdapter1 = new ArrayAdapter(getContext(), R.layout.dropdown_examctg, examctg);
                        arrayAdapter1.notifyDataSetChanged();
                        spinner.setAdapter(arrayAdapter1);

                    }

                }
            }
        });


        return examctg;
    }*/


    public void getFeeDetails() {

        String year1 = null;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Year year = Year.now();
            year1 = String.valueOf(year);
            // Toast.makeText(getActivity(), year1, Toast.LENGTH_SHORT).show();

        }
        String month_name = month_date.format(cal.getTime());

        String document = month_name + year1 + student.getStudentId();

        //Toast.makeText(getActivity(), document, Toast.LENGTH_SHORT).show();


        db.collection("Fee").document(document).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String feeStatus = documentSnapshot.getString("Status");
                String amountDue = documentSnapshot.getString("PayableAmount");

                //  fee = documentSnapshot.toObject(Fee.class);

                feestatus.setText(feeStatus);
                feeamount.setText(amountDue);

                //      Toast.makeText(getActivity(), documentSnapshot.getData().toString(), Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}


