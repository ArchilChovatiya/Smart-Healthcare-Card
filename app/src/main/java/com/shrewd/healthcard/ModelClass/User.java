package com.shrewd.healthcard.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.shrewd.healthcard.Utilities.CU;

import java.util.ArrayList;
import java.util.Date;

public class User implements Parcelable {

    private String name, address, email, proof, user_id, locality;
    long type, gender, contact_no;
    boolean verified;
    Date dob, reg_date;

    //For doctor only
    private String doc_license_no, doctor_type, hospital_id, hospital_name;

    //For patient only
    private double weight;
    private ArrayList<String> allergies;

    //For labAssistant only
    private String lab_id, lab_name;

    //For government only
    private String work_place;

    public User() {

    }

    public User(String user_id, String name, String address, String locality, String email, String proof,
                long type, long gender, long contact_no, boolean verified, Date dob, Date reg_date,
                String doc_license_no, String doctor_type, String hospital_id, String hospital_name,
                double weight, ArrayList<String> allergies,
                String lab_id, String lab_name,
                String work_place) {
        this.user_id = user_id;
        this.name = name;
        this.address = address;
        this.locality = locality;
        this.email = email;
        this.proof = proof;
        this.type = type;
        this.gender = gender;
        this.contact_no = contact_no;
        this.verified = verified;
        this.dob = dob;
        this.reg_date = reg_date;
        this.doc_license_no = doc_license_no;
        this.doctor_type = doctor_type;
        this.hospital_id = hospital_id;
        this.hospital_name = hospital_name;
        this.weight = weight;
        this.allergies = allergies;
        this.lab_id = lab_id;
        this.lab_name = lab_name;
        this.work_place = work_place;
    }

    protected User(Parcel in) {
        name = in.readString();
        address = in.readString();
        locality = in.readString();
        email = in.readString();
        proof = in.readString();
        user_id = in.readString();
        type = in.readLong();
        gender = in.readLong();
        contact_no = in.readLong();
        verified = in.readByte() != 0;
        doc_license_no = in.readString();
        doctor_type = in.readString();
        hospital_id = in.readString();
        hospital_name = in.readString();
        weight = in.readDouble();
        allergies = in.createStringArrayList();
        lab_id = in.readString();
        lab_name = in.readString();
        work_place = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLocality() {
        return !CU.isNullOrEmpty(locality) ? locality : "";
    }

    public String getName() {
        return !CU.isNullOrEmpty(name) ? name : "";
    }

    public String getAddress() {
        return !CU.isNullOrEmpty(address) ? address : "";
    }

    public String getEmail() {
        return !CU.isNullOrEmpty(email) ? email : "";
    }

    public String getProof() {
        return !CU.isNullOrEmpty(proof) ? proof : "";
    }

    public String getUser_id() {
        return !CU.isNullOrEmpty(user_id) ? user_id : "";
    }

    public long getType() {
        return type;
    }

    public long getGender() {
        return gender;
    }

    public long getContact_no() {
        return contact_no;
    }

    public boolean isVerified() {
        return verified;
    }

    public Date getDob() {
        return !CU.isNullOrEmpty(dob) ? dob : new Date(System.currentTimeMillis());
    }

    public Date getReg_date() {
        return !CU.isNullOrEmpty(reg_date) ? reg_date : new Date(System.currentTimeMillis());
    }

    public String getDoctor_type() {
        return !CU.isNullOrEmpty(doctor_type) ? doctor_type : "";
    }

    public String getHospital_id() {
        return !CU.isNullOrEmpty(hospital_id) ? hospital_id : "";
    }

    public String getHospital_name() {
        return !CU.isNullOrEmpty(hospital_name) ? hospital_name : "";
    }

    public double getWeight() {
        return weight;
    }

    public ArrayList<String> getAllergies() {
        return allergies != null ? allergies : new ArrayList<>();
    }

    public String getLab_id() {
        return !CU.isNullOrEmpty(lab_id) ? lab_id : "";
    }

    public String getLab_name() {
        return !CU.isNullOrEmpty(lab_name) ? lab_name : "";
    }

    public String getWork_place() {
        return !CU.isNullOrEmpty(work_place) ? work_place : "";
    }

    public String getDoc_license_no() {
        return !CU.isNullOrEmpty(doc_license_no) ? doc_license_no : "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(locality);
        dest.writeString(email);
        dest.writeString(proof);
        dest.writeString(user_id);
        dest.writeLong(type);
        dest.writeLong(gender);
        dest.writeLong(contact_no);
        dest.writeByte((byte) (verified ? 1 : 0));
        dest.writeString(doc_license_no);
        dest.writeString(doctor_type);
        dest.writeString(hospital_id);
        dest.writeString(hospital_name);
        dest.writeDouble(weight);
        dest.writeStringList(allergies);
        dest.writeString(lab_id);
        dest.writeString(lab_name);
        dest.writeString(work_place);
    }
}
