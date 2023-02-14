package com.enfotrix.cgscstudent;


import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.enfotrix.cgscstudent.fragments.DashboardFragment;
import com.enfotrix.cgscstudent.fragments.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {


    private SharedPrefManager sharedPrefManager;
    BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        loadFragment(new HomeFragment());



    }

    public void init() {
        sharedPrefManager = new SharedPrefManager(this);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.nav_home:
                    loadFragment(new HomeFragment());
                    return true;

                case R.id.nav_dashboard:
                    loadFragment(new DashboardFragment());
                    return true;

//                case R.id.nav_profile:
//
//                    loadFragment(new ProfileFragment());
//                    return true;
//
//
//                case R.id.nav_noti:
//
//                    loadFragment(new NotificationsFragment());
//                    return true;

            }
            return false;
        }
    };

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "");
        fragmentTransaction.commit();
    }







}