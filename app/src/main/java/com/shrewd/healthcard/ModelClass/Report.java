package com.shrewd.healthcard.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class Report implements Parcelable {

    private String report_id, lab_id, lab_name, type, patient_id, patient_name;
    private ArrayList<String> image;
    private Date date;

    public Report() {

    }

    public Report(String report_id, String lab_id, String lab_name, String type, ArrayList<String> image,
                  Date date, String patient_id, String patient_name) {
        this.report_id = report_id;
        this.lab_id = lab_id;
        this.lab_name = lab_name;
        this.type = type;
        this.image = image;
        this.date = date;
        this.patient_id = patient_id;
        this.patient_name = patient_name;
    }

    protected Report(Parcel in) {
        report_id = in.readString();
        lab_id = in.readString();
        lab_name = in.readString();
        type = in.readString();
        patient_id = in.readString();
        patient_name = in.readString();
        image = in.createStringArrayList();
        date = (Date) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(report_id);
        dest.writeString(lab_id);
        dest.writeString(lab_name);
        dest.writeString(type);
        dest.writeString(patient_id);
        dest.writeString(patient_name);
        dest.writeStringList(image);
        dest.writeSerializable(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    public String getPatient_id() {
        return patient_id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public String getLab_name() {
        return lab_name;
    }

    public String getReport_id() {
        return report_id;
    }

    public String getLab_id() {
        return lab_id;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public Date getDate() {
        return date;
    }
}
