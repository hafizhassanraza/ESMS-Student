package com.enfotrix.cgscstudent.model;

import com.google.firebase.Timestamp;

public class ModelNotification {
    String Data;
    String Heading;
    String Status;
    String StudentID;

    public ModelNotification(){

    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        Timestamp = timestamp;
    }

    public ModelNotification(String data, String heading, String status, String studentID, com.google.firebase.Timestamp timestamp) {
        Data = data;
        Heading = heading;
        Status = status;
        StudentID = studentID;
        Timestamp = timestamp;
    }

    Timestamp Timestamp;
}
