package com.enfotrix.cgscstudent.fragments;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enfotrix.cgscstudent.Common;
import com.enfotrix.cgscstudent.LoginActivity;
import com.enfotrix.cgscstudent.NotificationsActivity;
import com.enfotrix.cgscstudent.R;
import com.enfotrix.cgscstudent.SharedPrefManager;
import com.enfotrix.cgscstudent.adapter.AnnouncementAdapter;
import com.enfotrix.cgscstudent.adapter.FeaturesAdapter;
import com.enfotrix.cgscstudent.adapter.sliderAdapter;
import com.enfotrix.cgscstudent.model.AnnouncementModel;
import com.enfotrix.cgscstudent.model.Feature;
import com.enfotrix.cgscstudent.model.SliderItem;
import com.enfotrix.cgscstudent.model.Subject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private ImageView ivLogout;
    private TextView tvAttendance, tvRank, tvFee, tvStudentName, todayDate, countnotifications;
    private RecyclerView rvFeatures;
    private RelativeLayout RlayGallery;
    private List<Feature> featuresList;
    private CardView cardView;

    private SharedPrefManager sharedPrefManager;
    private FirebaseFirestore db;

    private int count = 0;

    private List<AnnouncementModel> announcementModelList;
    private RecyclerView rvAnnouncement;
    AnnouncementAdapter adapter;
    final static String status = "Approved";


    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;


    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);

        getNotifications();
        sliderSet();
        getAnnouncements();


        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date = dateFormat.format(calendar.getTime());
        todayDate.setText(date);


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NotificationsActivity.class));
            }
        });
        RlayGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Available Soon..", Toast.LENGTH_SHORT).show();
            }
        });
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog logoutDialog = new Dialog(getActivity());
                logoutDialog.setContentView(R.layout.dialog_logout);
                logoutDialog.setCancelable(true);
                logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                logoutDialog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (logoutDialog.isShowing())
                            logoutDialog.dismiss();
                        sharedPrefManager.logout();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finishAffinity();

                    }
                });

                logoutDialog.findViewById(R.id.btncancle).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (logoutDialog.isShowing())
                            logoutDialog.dismiss();

                    }
                });

                logoutDialog.show();
            }
        });


        if (getActivity() != null) {
            tvStudentName.setText(sharedPrefManager.getStudent().getFirstName() + " " + sharedPrefManager.getStudent().getLastName());
        }
        sendToken();
        getDashboardData();
        getCurrentDayAttendance();
        return view;
    }

    /*@RequiresApi(api = Build.VERSION_CODES.N)
    private void getCurrentDayAttendance() {
        if (getActivity() != null) {
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String currentMonth = String.format("%02d", ((Calendar.getInstance().get(Calendar.MONTH)) + 1));
            Log.d(TAG, "getCurrentDayAttendance: " + date);
            FirebaseFirestore.getInstance().collection("Attendance")
                    .document("2022-23")
                    .collection("AttendanceMonth")
                    .document(currentMonth)
                    .collection("AttendanceDate")
                    .document(date)
                    .collection("AttendanceSections")
                    .document(sharedPrefManager.getStudent().getCurrentSection())
                    .collection("AttendanceStudents")
                    .whereEqualTo("StudentID", sharedPrefManager.getStudent().getStudentId()) // omitting this line would get all students in attendance
                    .get(Source.SERVER)
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String status = document.getString("Status");
                                        tvAttendance.setText(status.substring(0, 1).toUpperCase());
                                        break;
                                    }
                                }
                            }
                        }
                    });
        }

    }
*/
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getCurrentDayAttendance() {
        if (getActivity() != null) {


            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String DocID = date + sharedPrefManager.getStudent().getStudentId();
            FirebaseFirestore.getInstance().collection("Attendance").document(DocID).get(Source.SERVER)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                            if (task.isSuccessful()) {
                                if (task.getResult().exists()) {
                                    //task.getResult().toObject()
                                    tvAttendance.setText(task.getResult().getString("Status"));

                                }
                            }

                        }
                    });


        }

    }

    private void init(View view) {
        if (getActivity() != null) {
            sharedPrefManager = new SharedPrefManager(getActivity());
        }

        calendar = Calendar.getInstance();
        countnotifications = view.findViewById(R.id.txtcountnotifications);

        cardView = view.findViewById(R.id.card_sales_manager);

        RlayGallery = view.findViewById(R.id.RlayGallery);
        viewPager2 = view.findViewById(R.id.viewpager_imageslider);
        tvStudentName = view.findViewById(R.id.tv_student_name);
        ivLogout = view.findViewById(R.id.iv_logout);
        tvAttendance = view.findViewById(R.id.tv_attendance);
        todayDate = view.findViewById(R.id.txt_today_date);
        //  tvRank = view.findViewById(R.id.tv_ranking);
        //   tvFee = view.findViewById(R.id.tv_fees);
        db = FirebaseFirestore.getInstance();

        rvFeatures = view.findViewById(R.id.rv_features);
        featuresList = new ArrayList<>();
        featuresList.add(new Feature("Attendance", R.drawable.ic_attendance2));
        featuresList.add(new Feature("Marks", R.drawable.ic_marks));
        rvFeatures.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvFeatures.setAdapter(new FeaturesAdapter(getActivity(), featuresList));


        rvAnnouncement = view.findViewById(R.id.rvAnnouncements);
        announcementModelList = new ArrayList<>();
        rvAnnouncement.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));

    }

    private void getDashboardData() {

    }

    private void sendToken() {
        if (getActivity() != null) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("YOMO", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();
                            Common.updateToken(getActivity(), token);
                        }
                    });
        }

    }


    private Runnable sliderRunnabler = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);


        }
    };


    public void sliderSet() {
        List<SliderItem> sliderItemList = new ArrayList<>();
        sliderItemList.add(new SliderItem(R.drawable.ad_one));
        sliderItemList.add(new SliderItem(R.drawable.ad_two));

        viewPager2.setAdapter(new sliderAdapter(sliderItemList, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnabler);
                sliderHandler.postDelayed(sliderRunnabler, 3000);
            }
        });

    }


    private void getNotifications() {

        db.collection("Notifications").whereEqualTo("StudentID", sharedPrefManager.getStudent().getStudentId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                count++;
                            }

                            countnotifications.setText(String.valueOf(count));

                        }
                    }
                });
    }


    private void getAnnouncements() {

        db.collection("Announcement").whereEqualTo("Status", status).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isComplete()) {
                    announcementModelList = task.getResult().toObjects(AnnouncementModel.class);
                    adapter = new AnnouncementAdapter(announcementModelList, getContext());
                    rvAnnouncement.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } else
                    Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
