package com.enfotrix.cgscstudent.model;

public class Section {
    private String DocID;
    private String Medium;
    private String SectionName;
    public Section(){

    }

    public Section(String sectionName) {
        this.SectionName = sectionName;
    }

    public Section(String docId, String medium, String sectionName) {
        this.DocID = docId;
        this.Medium = medium;
        this.SectionName = sectionName;
    }

    @Override
    public String toString() {
        return SectionName;
    }

    public String getDocID() {
        return DocID;
    }

    public void setDocID(String docID) {
        DocID = docID;
    }

    public String getMedium() {
        return Medium;
    }

    public void setMedium(String medium) {
        Medium = medium;
    }

    public String getSectionName() {
        return SectionName;
    }

    public void setSectionName(String sectionName) {
        SectionName = sectionName;
    }
}
