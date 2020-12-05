package com.shrewd.healthcard.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;

public class History implements Parcelable {

    private Double lat, lng;
    private String report_suggestion;
    private String doctor_id, doctor_name, patient_id, patient_name, report_id, area, disease;
    private ArrayList<String> symptoms, vigilance, medicine;
    private Date date;

    public History() {
    }

    public History(String doctor_id, String doctor_name, String patient_id, String patient_name, String report_id, String area, String disease,
                   ArrayList<String> medicine, ArrayList<String> symptoms, ArrayList<String> vigilance,
                   Date date, String report_suggestion, double lat, double lng) {
        this.doctor_id = doctor_id;
        this.doctor_name = doctor_name;
        this.patient_id = patient_id;
        this.patient_name = patient_name;
        this.report_id = report_id;
        this.area = area;
        this.disease = disease;
        this.medicine = medicine;
        this.symptoms = symptoms;
        this.vigilance = vigilance;
        this.date = date;
        this.report_suggestion = report_suggestion;
        this.lat = lat;
        this.lng = lng;
    }

    protected History(Parcel in) {
        report_suggestion = in.readString();
        doctor_id = in.readString();
        doctor_name = in.readString();
        patient_id = in.readString();
        patient_name = in.readString();
        report_id = in.readString();
        area = in.readString();
        disease = in.readString();
        symptoms = in.createStringArrayList();
        vigilance = in.createStringArrayList();
        medicine = in.createStringArrayList();
        date = (Date) in.readSerializable();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(report_suggestion);
        dest.writeString(doctor_id);
        dest.writeString(doctor_name);
        dest.writeString(patient_id);
        dest.writeString(patient_name);
        dest.writeString(report_id);
        dest.writeString(area);
        dest.writeString(disease);
        dest.writeStringList(symptoms);
        dest.writeStringList(vigilance);
        dest.writeStringList(medicine);
        dest.writeSerializable(date);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    public String getPatient_id() {
        return patient_id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public String getReport_id() {
        return report_id;
    }

    public String getArea() {
        return area;
    }

    public String getDisease() {
        return disease;
    }

    public ArrayList<String> getMedicine() {
        return medicine;
    }

    public ArrayList<String> getSymptoms() {
        return symptoms;
    }

    public ArrayList<String> getVigilance() {
        return vigilance;
    }

    public Date getDate() {
        return date;
    }

    public String getReport_suggestion() {
        return report_suggestion;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    @Exclude
    public LatLng getLocation() {
        if (lat != null && lng != null) {
            return new LatLng(lat, lng);
        } else {
            return null;
        }
    }
}
