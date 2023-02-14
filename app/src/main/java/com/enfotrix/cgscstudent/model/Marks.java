package com.enfotrix.cgscstudent.model;

public class Marks {

    private String FatherName;
    private String ObtainMarks;
    private String RegNumber;
    private String StudentId;
    private String StudentName;
    private String SubjectName;
    private String TotalMarks;

    public Marks(){

    }

    public String getFatherName() {
        return FatherName;
    }

    public void setFatherName(String fatherName) {
        FatherName = fatherName;
    }

    public String getObtainMarks() {
        return ObtainMarks;
    }

    public void setObtainMarks(String obtainMarks) {
        ObtainMarks = obtainMarks;
    }

    public String getRegNumber() {
        return RegNumber;
    }

    public void setRegNumber(String regNumber) {
        RegNumber = regNumber;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
    }

    public String getTotalMarks() {
        return TotalMarks;
    }

    public void setTotalMarks(String totalMarks) {
        TotalMarks = totalMarks;
    }

    @Override
    public String toString() {
        return "Marks{" +
                "FatherName='" + FatherName + '\'' +
                ", ObtainMarks='" + ObtainMarks + '\'' +
                ", RegNumber='" + RegNumber + '\'' +
                ", StudentId='" + StudentId + '\'' +
                ", StudentName='" + StudentName + '\'' +
                ", SubjectName='" + SubjectName + '\'' +
                ", TotalMarks='" + TotalMarks + '\'' +
                '}';
    }
}
