package com.shrewd.healthcard.Fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.Activity.LoginActivity;
import com.shrewd.healthcard.Activity.MainActivity;
import com.shrewd.healthcard.Adapter.ReportAdapter;
import com.shrewd.healthcard.ModelClass.Report;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;
import com.shrewd.healthcard.databinding.FragmentReportsBinding;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsFragment extends Fragment {


    private static final String TAG = "ReportsFragment";
    private Context mContext;
    private ArrayList<Report> alReport = new ArrayList<>();
    private ProgressDialog pd;
    private FragmentReportsBinding binding;

    public ReportsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReportsBinding.inflate(getLayoutInflater(), container, false);
        mContext = getContext();

        if (mContext == null)
            return binding.getRoot();
        CU.setActionBar(mContext, CS.Page.REPORTS);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(mContext, "Some error occurred! Please login again", Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            ((Activity) mContext).finish();
        }
//        loadData();


        return binding.getRoot();
    }

    private void loadData() {
        SharedPreferences sp = mContext.getSharedPreferences("GC", Context.MODE_PRIVATE);
        long type = sp.getLong(CS.type, -1);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (type == CS.ADMIN) {
            MainActivity.NFCReportEnabled = true;
            binding.flReportAdmin.setVisibility(View.VISIBLE);
            binding.flReport.setVisibility(View.GONE);

            CU.showProgressbar(mContext);

            db.collection(CS.Report)
                    .orderBy(CS.date, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            alReport.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    Report report = documentSnapshot.toObject(Report.class);
                                    /*History history = new History(documentSnapshot.getString(CS.doctorid), documentSnapshot.getString(CS.patientid),
                                            documentSnapshot.getString(CS.reportid), documentSnapshot.getString(CS.area), documentSnapshot.getString(CS.disease),
                                            (ArrayList<String>) documentSnapshot.get(CS.medicine), (ArrayList<String>) documentSnapshot.get(CS.symptoms), (ArrayList<String>) documentSnapshot.get(CS.vigilance),
                                            documentSnapshot.getTimestamp(CS.date).toDate());*/
                                    Log.e(TAG, "onSuccess: report: " + report.getLab_id());
                                    Log.e(TAG, "onSuccess: report: " + report.getReport_id());
                                    Log.e(TAG, "onSuccess: report: " + report.getType());
                                    Log.e(TAG, "onSuccess: report: " + report.getDate());
                                    Log.e(TAG, "onSuccess: report: " + (report.getImage().size() > 0 ? report.getImage().get(0) : "no image"));
                                    alReport.add(report);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                }
                            }
                            CU.hideProgressbar();
                            setAdapter(alReport, (int) type);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CU.hideProgressbar();
                            setAdapter(alReport, (int) type);
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        } else if (type == CS.LAB) {

            String labassistant_id = sp.getString(CS.labassistant_id, "");
            String lab_id = sp.getString(CS.lab_id, "");
            String lab_name = sp.getString(CS.lab_name, "");
            if (CU.isNullOrEmpty(lab_id) || CU.isNullOrEmpty(lab_name)) {
                CU.toast(mContext, "Failed to fetch reports!\nPlease try again later", Toast.LENGTH_LONG).show();
                return;
            }
            
            binding.flReportAdmin.setVisibility(View.GONE);
            binding.flReport.setVisibility(View.VISIBLE);
            MainActivity.NFCReportEnabled = true;

            CU.showProgressbar(mContext);

            Log.e(TAG, "onCreateView: firebaseUser: " + firebaseUser.getUid());
            db.collection(CS.Report)
                    .orderBy(CS.date, Query.Direction.DESCENDING)
                    .whereEqualTo(CS.lab_id, lab_id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            alReport.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    Report report = documentSnapshot.toObject(Report.class);
                                    Log.e(TAG, "onSuccess: " + report.getPatient_id());
                                    alReport.add(report);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                }
                            }
                            CU.hideProgressbar();
                            setAdapter(alReport, (int) type);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CU.hideProgressbar();
                            setAdapter(alReport, (int) type);
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        } else if (type == CS.PATIENT) {
            binding.flReportAdmin.setVisibility(View.GONE);
            binding.flReport.setVisibility(View.VISIBLE);
            MainActivity.NFCReportEnabled = true;

            CU.showProgressbar(mContext);

            db.collection(CS.Report)
                    .orderBy(CS.date, Query.Direction.DESCENDING)
                    .whereEqualTo(CS.patient_id, firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            alReport.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    Report report = documentSnapshot.toObject(Report.class);
                                    /*History history = new History(documentSnapshot.getString(CS.doctorid), documentSnapshot.getString(CS.patientid),
                                            documentSnapshot.getString(CS.reportid), documentSnapshot.getString(CS.area), documentSnapshot.getString(CS.disease),
                                            (ArrayList<String>) documentSnapshot.get(CS.medicine), (ArrayList<String>) documentSnapshot.get(CS.symptoms), (ArrayList<String>) documentSnapshot.get(CS.vigilance),
                                            documentSnapshot.getTimestamp(CS.date).toDate());*/
                                    Log.e(TAG, "onSuccess: report: " + report.getLab_id());
                                    Log.e(TAG, "onSuccess: report: " + report.getReport_id());
                                    Log.e(TAG, "onSuccess: report: " + report.getType());
                                    Log.e(TAG, "onSuccess: report: " + report.getDate());
                                    Log.e(TAG, "onSuccess: report: " + (report.getImage().size() > 0 ? report.getImage().get(0) : "no image"));
                                    alReport.add(report);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                }
                            }
                            CU.hideProgressbar();
                            setAdapter(alReport, (int) type);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CU.hideProgressbar();
                            setAdapter(alReport, (int) type);
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        } else if (type == CS.DOCTOR) {
            pd = new ProgressDialog(mContext);
            pd.setMessage("Waiting for NFC...");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            MainActivity.NFCReportEnabled = true;
            pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ((MainActivity) mContext).binding.navView.getMenu().performIdentifierAction(R.id.patientFragment, 0);
                    ((MainActivity) mContext).binding.navView.setCheckedItem(R.id.patientFragment);
                }
            });
        }
    }

    private void setAdapter(ArrayList<Report> alReport, int type) {
        binding.rvReport.setVisibility(View.GONE);
        binding.llNoData.noDataContent.setVisibility(View.GONE);
        switch (type) {
            case CS.PATIENT:
            case CS.LAB:
                if (alReport.size() > 0) {
                    binding.rvReport.setVisibility(View.VISIBLE);
                    binding.llNoData.noDataContent.setVisibility(View.GONE);
                    Log.e(TAG, "onSuccess: " + alReport.size());
                    ReportAdapter reportAdapter = new ReportAdapter(mContext, alReport, type);
                    binding.rvReport.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    binding.rvReport.setAdapter(reportAdapter);
                } else {
                    binding.rvReport.setVisibility(View.GONE);
                    binding.llNoData.noDataContent.setVisibility(View.VISIBLE);
                }
                break;
            case CS.ADMIN:
                if (alReport.size() > 0) {
                    binding.rvReportAdmin.setVisibility(View.VISIBLE);
                    binding.llNoDataAdmin.noDataContent.setVisibility(View.GONE);
                    Log.e(TAG, "onSuccess: " + alReport.size());
                    ReportAdapter reportAdapter = new ReportAdapter(mContext, alReport, type);
                    binding.rvReportAdmin.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    binding.rvReportAdmin.setAdapter(reportAdapter);
                } else {
                    binding.rvReportAdmin.setVisibility(View.GONE);
                    binding.llNoDataAdmin.noDataContent.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        loadData();
        super.onResume();
    }

    @Override
    public void onDetach() {
        MainActivity.NFCReportEnabled = false;
        super.onDetach();
    }

    @Override
    public void onPause() {
        if (pd != null) {
            pd.dismiss();
        }
        super.onPause();
    }
}
