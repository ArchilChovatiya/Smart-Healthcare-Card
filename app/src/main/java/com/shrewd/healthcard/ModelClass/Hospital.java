package com.shrewd.healthcard.ModelClass;

public class Hospital {

    private String address, hospital_id, name;
    private long contact_no;

    public Hospital(){

    }

    public Hospital(String address, long contact_no, String hospital_id, String name) {
        this.address = address;
        this.contact_no = contact_no;
        this.hospital_id = hospital_id;
        this.name = name;
    }

    @Override
    public String toString() {
        if (contact_no == -1) {
            return "-- Select Hospital --";
        } else {
            return name + ", " + address + ", " + contact_no;
        }
    }

    public String getAddress() {
        return address;
    }

    public long getContact_no() {
        return contact_no;
    }

    public String getHospital_id() {
        return hospital_id;
    }

    public String getName() {
        return name;
    }
}
