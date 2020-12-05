package com.shrewd.healthcard.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shrewd.healthcard.ModelClass.Laboratory;
import com.shrewd.healthcard.ModelClass.Report;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;

import java.text.SimpleDateFormat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ReportHistoryActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_history);

        mContext = ReportHistoryActivity.this;

        SharedPreferences sp = getSharedPreferences("GC", MODE_PRIVATE);
        long userType = sp.getLong(CS.type, -1);

        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvLab = findViewById(R.id.tvLab);
        TextView tvReportType = findViewById(R.id.tvReportType);
        TextView tvPatient = findViewById(R.id.tvPatient);
        final ImageView ivReport = findViewById(R.id.ivReport);

        Intent intent = getIntent();
        Report report = intent.getParcelableExtra(CS.report);
        if (report == null) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("E dd, MMM yyyy hh:mm aa");
        tvDate.setText(sdf.format(report.getDate()));
        tvReportType.setText(report.getType());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //region set Actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Report");
        }
        tvPatient.setText(report.getPatient_name());

        db.collection(CS.Laboratory)
                .document(report.getLab_id())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dsLab) {
                        Laboratory laboratory = dsLab.toObject(Laboratory.class);
                        if (laboratory != null) {
                            String labDetail = laboratory.getName() + ",\n" + laboratory.getAddress() + "\n" + laboratory.getContact_no();
                            tvLab.setText(labDetail);
                        }
                    }
                });

        if (report.getImage().size() > 0) {
            Glide.with(mContext)
                    .load(report.getImage().get(0))
                    .placeholder(R.drawable.broken_report)
                    .into(ivReport);
        }

        ivReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dg = new Dialog(mContext);
                dg.setContentView(R.layout.dg_image_preview);
                ImageView iv = dg.findViewById(R.id.ivImg);
                TextView tvName = dg.findViewById(R.id.tvName);
                tvName.setText(!CU.isNullOrEmpty(tvPatient) ? tvPatient.getText().toString() + " (" + report.getType() + ")" : report.getType());
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
