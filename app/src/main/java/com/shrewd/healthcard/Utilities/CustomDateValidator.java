package com.shrewd.healthcard.Utilities;

import android.os.Parcel;

import com.google.android.material.datepicker.CalendarConstraints;

public class CustomDateValidator implements CalendarConstraints.DateValidator {

    private int type;
    private long[] selection;

    public interface Type {
        int START = 0;
        int END = 1;
        int BETWEEN = 2;
    }

    /**
     * type indicates START from start_date, BETWEEN start_date and end_date & END to end_date
     * selection will be start_date for START type,
     *                  end_date for END type,
     *                  start_date & end_date for BETWEEN type
     * */
    public CustomDateValidator(int type, long... selection) {
        this.type = type;
        this.selection = selection;
        for (int i = 0; i < selection.length; i++) {
            selection[i] = CU.getMidnightTime(selection[i]);
        }
    }

    protected CustomDateValidator(Parcel in) {
        type = in.readInt();
        selection = in.createLongArray();
    }

    public static final Creator<CustomDateValidator> CREATOR = new Creator<CustomDateValidator>() {
        @Override
        public CustomDateValidator createFromParcel(Parcel in) {
            return new CustomDateValidator(in);
        }

        @Override
        public CustomDateValidator[] newArray(int size) {
            return new CustomDateValidator[size];
        }
    };

    @Override
    public boolean isValid(long date) {
        switch (type) {
            case Type.START:
                if (selection.length != 1) {
                    return false;
                }
                return date >= selection[0];
            case Type.END:
                if (selection.length != 1) {
                    return false;
                }
                return date <= selection[0];
            case Type.BETWEEN:
                if (selection.length != 2) {
                    return false;
                }
                return date >= selection[0] && date <= selection[1];
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeLongArray(selection);
    }
}
