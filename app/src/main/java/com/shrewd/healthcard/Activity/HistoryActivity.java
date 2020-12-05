package com.shrewd.healthcard.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.ModelClass.History;
import com.shrewd.healthcard.ModelClass.Hospital;
import com.shrewd.healthcard.ModelClass.Laboratory;
import com.shrewd.healthcard.ModelClass.User;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";
    private Context mContext;
    private String reportName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mContext = HistoryActivity.this;

        SharedPreferences sp = getSharedPreferences("GC", MODE_PRIVATE);
        long userType = sp.getLong(CS.type, -1);

        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvReportDate = findViewById(R.id.tvReportDate);

        TextView tvDisease = findViewById(R.id.tvDisease);
        TextView tvMedicine = findViewById(R.id.tvMedicine);
        TextView tvSymptoms = findViewById(R.id.tvSymptoms);
        TextView tvVigilance = findViewById(R.id.tvVigilance);
        TextView tvArea = findViewById(R.id.tvArea);
        TextView tvReport = findViewById(R.id.tvReport);
        TextView tvPatient = findViewById(R.id.tvPatient);

        final ImageView ivReport = findViewById(R.id.ivReport);
        final LinearLayout llReport = findViewById(R.id.llReport);
        final LinearLayout llPatient = findViewById(R.id.llPatient);

        final LinearLayout llMedicine = findViewById(R.id.llMedicine);
        final LinearLayout llSymptoms = findViewById(R.id.llSymptoms);
        final LinearLayout llVigilance = findViewById(R.id.llVigilance);
        final LinearLayout llArea = findViewById(R.id.llArea);

        final View vReport = findViewById(R.id.vReport);
        final View vMedicine = findViewById(R.id.vMedicine);
        final View vSymptoms = findViewById(R.id.vSymptoms);
        final View vVigilance = findViewById(R.id.vVigilance);
        final View vArea = findViewById(R.id.vArea);

        final TextView tvDoctor = findViewById(R.id.tvDoctor);
        final TextView tvHospital = findViewById(R.id.tvHospital);

        ivReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dg = new Dialog(mContext);
                dg.setContentView(R.layout.dg_image_preview);
                ImageView iv = dg.findViewById(R.id.ivImg);
                TextView tvName = dg.findViewById(R.id.tvName);
                if (!reportName.equals(""))
                    tvName.setText(reportName);
                iv.setImageDrawable(ivReport.getDrawable());
                Window window = dg.getWindow();
                if (window != null) {
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen._500sdp));
                    window.setBackgroundDrawable(getDrawable(R.drawable.bg_dg_newuser));
                    window.setGravity(Gravity.BOTTOM);
                }
                dg.show();
            }
        });

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        History history = intent.getParcelableExtra(CS.History);
        if (history == null) {
            CU.toast(mContext, "Some error occurred!\nPlease try again", Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }
        int type = intent.getIntExtra(CS.type, -1);
        if (type == CS.REPORT) {
            tvReportDate.setVisibility(View.VISIBLE);
            tvDate.setVisibility(View.GONE);
            llMedicine.setVisibility(View.GONE);
            llSymptoms.setVisibility(View.GONE);
            llVigilance.setVisibility(View.GONE);
            llArea.setVisibility(View.GONE);
            vMedicine.setVisibility(View.GONE);
            vSymptoms.setVisibility(View.GONE);
            vVigilance.setVisibility(View.GONE);
            vArea.setVisibility(View.GONE);
//            llPatient.setVisibility(View.VISIBLE);
        } else {
            tvReportDate.setVisibility(View.GONE);
            tvDate.setVisibility(View.VISIBLE);
            llMedicine.setVisibility(View.VISIBLE);
            llSymptoms.setVisibility(View.VISIBLE);
            llVigilance.setVisibility(View.VISIBLE);
            llArea.setVisibility(View.VISIBLE);
            vMedicine.setVisibility(View.VISIBLE);
            vSymptoms.setVisibility(View.VISIBLE);
            vVigilance.setVisibility(View.VISIBLE);
            vArea.setVisibility(View.VISIBLE);
//            llPatient.setVisibility(View.GONE);
        }

        //region set Actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(history.getPatient_name());
        }

        Log.e(TAG, "onCreate: " + history);
        Log.e(TAG, "onCreate: " + history.getArea());
        Log.e(TAG, "onCreate: " + history.getDisease());
        Log.e(TAG, "onCreate: " + history.getDoctor_id());

        Date date = history.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("E dd, MMM yyyy hh:mm aa");
        tvDate.setText(sdf.format(date));
        tvDisease.setText(history.getDisease());
        tvArea.setText(history.getArea());

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd, MMM yyyy");
        if (actionBar != null && userType == CS.PATIENT) {
            actionBar.setTitle(sdf1.format(date));
        } else if (actionBar != null && (userType == CS.ADMIN || userType == CS.LAB)) {
            actionBar.setTitle(history.getPatient_name());
        }

        final StringBuilder medicine = new StringBuilder();
        for (int i = 0; i < history.getMedicine().size(); i++) {
            if (i == 0) {
                medicine.append("■ ").append(history.getMedicine().get(i));
            } else {
                medicine.append("\n■ ").append(history.getMedicine().get(i));
            }
        }

        StringBuilder symptoms = new StringBuilder();
        for (int i = 0; i < history.getSymptoms().size(); i++) {
            if (i == 0) {
                symptoms.append("■ ").append(history.getSymptoms().get(i));
            } else {
                symptoms.append("\n■ ").append(history.getSymptoms().get(i));
            }
        }

        StringBuilder vigilance = new StringBuilder();
        for (int i = 0; i < history.getVigilance().size(); i++) {
            if (i == 0) {
                vigilance.append("■ ").append(history.getVigilance().get(i));
            } else {
                vigilance.append("\n■ ").append(history.getVigilance().get(i));
            }
        }

        tvMedicine.setText(medicine);
        tvSymptoms.setText(symptoms);
        tvVigilance.setText(vigilance);

        try {
            db.collection(CS.User)
                    .document(history.getDoctor_id())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user;
                            if (documentSnapshot.exists() && (user = documentSnapshot.toObject(User.class)) != null) {
                                tvDoctor.setText(user.getName() + "\n" + user.getContact_no() + "\n" + user.getEmail());
                                Log.e(TAG, "onSuccess: doctor name: " + user.getName());

                                db.collection(CS.Hospital)
                                        .document(user.getHospital_id())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot docHospital) {
                                                Hospital hospital = docHospital.toObject(Hospital.class);
                                                if (hospital != null) {
                                                    tvHospital.setText(hospital.getName() + ",\n" + hospital.getAddress() + "\n" + hospital.getContact_no());
                                                    Log.e(TAG, "onSuccess: hospital name: " + hospital.getName());
                                                }
                                            }
                                        });
                            }
                        }
                    });
        } catch (Exception ex) {
            Log.e(TAG, "onSuccess: error: " + ex.getMessage());
        }

        if (!CU.isNullOrEmpty(history.getReport_id())) {
            db.collection(CS.Report)
                    .document(history.getReport_id())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            llReport.setVisibility(View.GONE);
                            vReport.setVisibility(View.GONE);

                            if (documentSnapshot.exists()) {
                                reportName = documentSnapshot.getString(CS.type);
                                if (type == CS.REPORT) {
                                    db.collection(CS.Laboratory)
                                            .whereEqualTo(CS.lab_id, documentSnapshot.getString(CS.lab_id))
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for (DocumentSnapshot dsLab : queryDocumentSnapshots.getDocuments()) {
                                                        Laboratory lab = dsLab.toObject(Laboratory.class);

                                                        if (userType == CS.ADMIN) {
                                                            tvReport.setText(reportName + "\n(" + lab.getName() + ",\n" + lab.getAddress() + ")\n" + lab.getContact_no());
                                                        } else {
                                                            tvReport.setText(reportName);
                                                        }
                                                        break;
                                                    }
                                                }
                                            });
                                } else {
                                    tvReport.setText(reportName + "\n" + sdf1.format(documentSnapshot.getDate(CS.date)));
                                }
                                Log.e(TAG, "onSuccess: " + ((ArrayList<String>) documentSnapshot.get(CS.image)).get(0));
                                ArrayList<String> alReportImage = (ArrayList<String>) documentSnapshot.get(CS.image);
                                if (alReportImage != null && alReportImage.size() > 0) {
                                    llReport.setVisibility(View.VISIBLE);
                                    vReport.setVisibility(View.VISIBLE);
                                    tvReportDate.setText(sdf.format(documentSnapshot.getDate(CS.date)));
                                    Glide.with(mContext).load(alReportImage.get(0)).placeholder(R.drawable.broken_report).into(ivReport);
                                }
                            }
                        }
                    });
        } else {
            llReport.setVisibility(View.GONE);
            vReport.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
