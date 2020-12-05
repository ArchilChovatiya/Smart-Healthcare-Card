package com.shrewd.healthcard.Utilities;

public class CS {
    public static final int DOCTOR = 0;
    public static final int PATIENT = 1;
    public static final int LAB = 2;
    public static final int GOVERNMENT = 3;
    public static final int ADMIN = 4;

    public static final int HISTORY = 0;
    public static final int REPORT = 1;

    public static final int NA = 0;
    public static final int Male = 1;
    public static final int Female = 2;
    public static final String NOTIFICATION = "Notification";

    public static final String pushNotification = "pushNotification";
    public static final String messageNotification = "messageNotification";

    public static String User = "User";
    public static String History = "History";
    public static String Hospital = "Hospital";
    public static String Government = "Government";
    public static String LabAssistant = "LabAssistant";
    public static String Laboratory = "Laboratory";
    public static String Report = "Report";
    public static String Dr = "Dr. ";

    public static String user_id = "user_id";
    public static String area = "area";
    public static String date = "date";
    public static String reg_date = "reg_date";
    public static String disease = "disease";
    public static String doctor_id = "doctor_id";
    public static String doctor_name = "docto_rname";
    public static String medicine = "medicine";
    public static String patient_id = "patient_id";
    public static String patient_name = "patient_name";
    public static String report_id = "report_id";
    public static String lab_id = "lab_id";
    public static String lab_name = "lab_name";
    public static String labassistant_id = "labassistant_id";
    public static String symptoms = "symptoms";
    public static String vigilance = "vigilance";
    public static String allergy = "allergy";

    public static String government_id = "government_id";

    public static String Address = "address";
    public static String contact_no = "contact_no";
    public static String dob = "dob";
    public static String email = "email";
    public static String name = "name";
    public static String type = "type";
    public static String gender = "gender";
    public static String proof = "proof";
    public static String verified = "verified";
    public static String verificationGIF = "https://firebasestorage.googleapis.com/v0/b/healthcard-463ca.appspot.com/o/verification.gif?alt=media&token=d88c2f26-8db7-4629-bea0-eb1001fa2cbf";

    public static String ddMMyyyy = "dd-MM-yyyy";
    public static String image = "image";
    public static String address = "address";
    public static String hospital_id = "hospital_id";
    public static String hospital_name = "hospital_name";
    public static String Admin = "Admin";
    public static String host = "host";
    public static String port = "port";
    public static String report = "report";
    public static String Token = "Token";
    public static String licenseno = "licenseno";

    public interface Page {
        int HOME = 0;
        int ABOUT = 1;
        int ANALYSIS = 2;
        int HISTORY = 3;
        int HOME_REMEDIES = 4;
        int PATIENT = 5;
        int REPORTS = 6;
        int SETTINGS = 7;
        int VERIFY = 8;
        int PATIENT_ACTIVITY = 9;
        int REPORT_ACTIVITY = 10;
    }

    public interface PermissionRequestCode {
        int LOCATION_NEW_USER = 0;
        int LOCATION_NEW_RECORD = 1;
        int LOCATION_NEW_REPORT = 2;
        int STORAGE = 3;
    }

    public interface PermissionGrantResult {
        int GRANTED = 0;
        int DENIED = 1;
        int DONTASKAGAIN = 2;
    }

}
