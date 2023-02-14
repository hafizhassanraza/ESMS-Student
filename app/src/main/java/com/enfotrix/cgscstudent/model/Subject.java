package com.enfotrix.cgscstudent.model;

import java.util.List;

public class Subject {
    private String ClassID;
    private String ID; // Subject Id
    private List<String> SectionsIDs;
    private String SubjectName;

    public Subject(String subjectName){
        this.SubjectName = subjectName;
    }

    public Subject(){

    }

    public String getClassID() {
        return ClassID;
    }

    public void setClassID(String classID) {
        ClassID = classID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    public List<String> getSectionsIDs() {
        return SectionsIDs;
    }

    public void setSectionsIDs(List<String> sectionsIDs) {
        SectionsIDs = sectionsIDs;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
    }

    @Override
    public String toString() {
        return SubjectName;
    }
}
