package com.enfotrix.cgscstudent;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.enfotrix.cgscstudent.model.Section;
import com.enfotrix.cgscstudent.model.Student;


public class SharedPrefManager {
    public static String SHARED_PREF_NAME = "enfotrix";
    private SharedPreferences sharedPreferences;
    private Context context;
    private SharedPreferences.Editor editor;
    final String PREF_PROFILE_LINK = "profilelink";

    public SharedPrefManager(Context context) {
        this.context = context;
    }

    public SharedPrefManager() {

    }




    public void saveSection(Section section) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("docId", section.getDocID());
        editor.putString("medium", section.getMedium());
        editor.putString("sectionName", section.getSectionName());
        editor.apply();
    }

    public void saveStudent(Student student) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("id", student.getRegNumber());
        editor.putString("fname", student.getFirstName());
        editor.putString("lname", student.getLastName());
        editor.putString("email", student.getEmail());
        editor.putString("CurrentSection", student.getCurrentSection());
        editor.putString("StudentId", student.getStudentId());
        editor.putString("fatherName", student.getFatherName());
        editor.putString("currentClass", student.getCurrentClass());
        editor.putBoolean("logged", true);

        editor.apply();
    }

    public void saveUri(String profileLink) {

        sharedPreferences = context.getSharedPreferences(PREF_PROFILE_LINK, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(PREF_PROFILE_LINK, profileLink);
        editor.commit();
    }

    public String getUri() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_PROFILE_LINK, "");
    }


    public boolean isLoggedIn() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("logged", false);
    }

    public Section getSection() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Section(
                sharedPreferences.getString("docId", null),
                sharedPreferences.getString("medium", null),
                sharedPreferences.getString("sectionName", null)
        );

    }

    public Student getStudent() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Student(
                sharedPreferences.getString("id", null),
                sharedPreferences.getString("fname", null),
                sharedPreferences.getString("lname", null),
                sharedPreferences.getString("email", null),
                sharedPreferences.getString("CurrentSection", null),
                sharedPreferences.getString("StudentId", null),
                sharedPreferences.getString("fatherName", null),
                sharedPreferences.getString("currentClass", null)
        );

    }

    public void logout() {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
