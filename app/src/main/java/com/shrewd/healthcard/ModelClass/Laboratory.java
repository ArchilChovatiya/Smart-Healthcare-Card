package com.shrewd.healthcard.ModelClass;

import androidx.annotation.NonNull;

public class Laboratory {

    private String address, lab_id, name;
    private long contact_no;

    public Laboratory() {

    }

    public Laboratory(String address, long contact_no, String lab_id, String name) {
        this.address = address;
        this.contact_no = contact_no;
        this.lab_id = lab_id;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public long getContact_no() {
        return contact_no;
    }

    public String getLab_id() {
        return lab_id;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        if (contact_no == -1) {
            return "-- Select Laboratory --";
        } else {
            return name + ", " + address + ", " + contact_no;
        }
    }
}
