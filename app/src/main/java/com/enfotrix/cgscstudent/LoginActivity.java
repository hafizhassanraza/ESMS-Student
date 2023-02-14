package com.enfotrix.cgscstudent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.enfotrix.cgscstudent.model.Student;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;

    private String regNo, password;
    private SharedPrefManager sharedPrefManager;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onClick(View view) {
                if (validateDetails()) {
                    loginUser();
                }
            }
        });

    }

    public void init() {

        sharedPrefManager = new SharedPrefManager(LoginActivity.this);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_log_in);
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please Wait..");
        db = FirebaseFirestore.getInstance();
    }

    private boolean validateDetails() {
        boolean isValid = true;
        regNo = etEmail.getText().toString().trim();
        password = etPassword.getText().toString();

        if (TextUtils.isEmpty(regNo)) {
            isValid = false;
            Toast.makeText(this, "Please enter your reg no!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            isValid = false;
            Toast.makeText(this, "Please enter your password!", Toast.LENGTH_SHORT).show();

        }
        return isValid;
    }


    @Override
    protected void onStart() {
        super.onStart();
        performLoginCheck();
    }

    public void performLoginCheck() {
        if (sharedPrefManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    private void loginUser() {
        // Call Retrofit
         // progressDialog.show();
        Dialog loginDialog = new Dialog(LoginActivity.this);
        loginDialog.setContentView(R.layout.dialog_login);
        loginDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loginDialog.setCancelable(false);
        loginDialog.show();
        db.collection("Students")
                .whereEqualTo("RegNumber", regNo)
                .whereEqualTo("Password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        loginDialog.dismiss();
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    sharedPrefManager.saveStudent(new Student(
                                            document.getString("RegNumber"),
                                            document.getString("FirstName"),
                                            document.getString("LastName"),
                                            document.getString("Email"),
                                            document.getString("CurrentSection"),
                                            document.getString("StudentId"),
                                            document.getString("FatherName"),
                                            document.getString("CurrentClass")

                                    ));


                                    Log.d(TAG, "onComplete: " + document.getData());
                                }

                                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));


                            } else {
                                Toast.makeText(LoginActivity.this, "fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}