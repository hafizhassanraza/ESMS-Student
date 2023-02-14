package com.enfotrix.cgscstudent.model;

public class Model_Feedback {

    private String id;
    private String data;
    private String date;
    private String heading;

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    private String studentid;

    public Model_Feedback(String id, String data, String date, String heading, String studentid) {
        this.id = id;
        this.data = data;
        this.date = date;
        this.heading = heading;
        this.studentid = studentid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }
}
