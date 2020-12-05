package com.shrewd.healthcard.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shrewd.healthcard.Adapter.ReportAdapter;
import com.shrewd.healthcard.ModelClass.Report;
import com.shrewd.healthcard.ModelClass.User;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReportActivity extends AppCompatActivity {

    private static final String TAG = "ReportActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;
    private static final int PICK_REPORT = 102;
    private RecyclerView rvReport;
    private LinearLayout llNoData;
    private Context mContext;
    private CoordinatorLayout crdPatient;
    private ArrayList<Report> alReport = new ArrayList<>();
    private FloatingActionButton fabAdd;
    User user;
    private Uri filePathUri;
    private MaterialTextView tvFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mContext = ReportActivity.this;

        crdPatient = (CoordinatorLayout) findViewById(R.id.crdPatient);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        rvReport = (RecyclerView) findViewById(R.id.rvReport);
        llNoData = (LinearLayout) findViewById(R.id.llNoData);

        Intent intent = getIntent();
        String patient_id = intent.getStringExtra(CS.user_id);
        long type = intent.getLongExtra(CS.type, CS.LAB);

        if (type == CS.LAB) {
            fabAdd.setVisibility(View.VISIBLE);
        } else {
            fabAdd.setVisibility(View.GONE);
        }

        if (patient_id == null)
            return;

        //region set Actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Report");
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(mContext, "Some error occurred!Please login again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.e(TAG, "onCreate: " + patient_id);
        db.collection(CS.User)
                .document(patient_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            user = documentSnapshot.toObject(User.class);
                            if (user == null) {
                                return;
                            }
                            user.setUser_id(documentSnapshot.getId());
                            Log.e(TAG, "onSuccess: " + documentSnapshot.getString(CS.name));
                            ActionBar actionBar = getSupportActionBar();
                            if (actionBar != null) {
                                actionBar.setDisplayHomeAsUpEnabled(true);
                                actionBar.setTitle("Reports - " + user.getName());
                            }
                        }
                    }
                });

        /*db.collection(CS.Patient)
                .whereEqualTo(CS.userid, patient_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot dsPatient : queryDocumentSnapshots.getDocuments()) {
                            db.collection(CS.Report)
                                    .orderBy(CS.date, Query.Direction.DESCENDING)
                                    .whereEqualTo(CS.patientid, dsPatient.getString(CS.patientid))
//                                    .whereEqualTo(CS.doctorid, firebaseUser.getUid())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            Log.e(TAG, "onSuccess: " + queryDocumentSnapshots.size());
                                            alReport.clear();
                                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                try {
                                                    Report report = documentSnapshot.toObject(Report.class);
                                                    alReport.add(report);
                                                } catch (Exception ex) {
                                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                                }
                                            }
                                            CU.hideProgressbar();
                                            setAdapter(alReport);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            CU.hideProgressbar();
                                            setAdapter(alReport);
                                            Log.e(TAG, "onFailure: " + e.getMessage());
                                        }
                                    });
                        }
                    }
                });*/

        db.collection(CS.Report)
                .orderBy(CS.date, Query.Direction.DESCENDING)
                .whereEqualTo(CS.patient_id, patient_id)
//                                    .whereEqualTo(CS.doctorid, firebaseUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.e(TAG, "onSuccess: " + queryDocumentSnapshots.size());
                        alReport.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            try {
                                Report report = documentSnapshot.toObject(Report.class);
                                alReport.add(report);
                            } catch (Exception ex) {
                                Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                            }
                        }
                        CU.hideProgressbar();
                        setAdapter(alReport);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CU.hideProgressbar();
                        setAdapter(alReport);
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sp = getSharedPreferences("GC", MODE_PRIVATE);
                String lab_id = sp.getString(CS.lab_id, "");
                String lab_name = sp.getString(CS.lab_name, "");
                if (CU.isNullOrEmpty(lab_id) || CU.isNullOrEmpty(lab_name)) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                    finishAffinity();
                    CU.toast(mContext, "Some error occurred!\nTry log-in again", Toast.LENGTH_LONG).show();
                    return;
                }

                Dialog dg = new Dialog(mContext);
                dg.setContentView(R.layout.dg_new_report);
                TextInputEditText etReportType = dg.findViewById(R.id.etReportType);
                tvFile = dg.findViewById(R.id.tvFile);
                MaterialButton btnUpload = dg.findViewById(R.id.btnUpload);

                MaterialButton btnSubmit = dg.findViewById(R.id.btnSubmit);
                SpinKitView progressBar = dg.findViewById(R.id.progressBar);

                Window window = dg.getWindow();
                if (window != null) {
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(getDrawable(R.drawable.bg_dg_newuser));
                    window.setGravity(Gravity.BOTTOM);
                }
                dg.setCanceledOnTouchOutside(false);

                progressBar.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);

                btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFileChooserIntent();
                    }
                });

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (CU.isNullOrEmpty(etReportType)) {
                            etReportType.setError("Must not be empty");
                            etReportType.requestFocus();
                            return;
                        }

                        if ((CU.isNullOrEmpty(tvFile) || tvFile.getText().toString().equals("Choose File") || filePathUri == null)) {
                            tvFile.setError("Verification Proof required");
                            tvFile.requestFocus();
                            Toast.makeText(mContext, "Please upload report!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        progressBar.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);

                        db.collection(CS.User)
                                .document(patient_id)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot dsUser) {
                                        User userPatient = dsUser.toObject(User.class);
                                        if (userPatient == null) {
                                            return;
                                        }
                                        final StorageReference sRef = FirebaseStorage.getInstance().getReference("Users/" + System.currentTimeMillis() + "." + MimeTypeMap.getFileExtensionFromUrl(filePathUri.toString()));
                                        sRef.putFile(filePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Report report = new Report(
                                                                String.valueOf(System.currentTimeMillis()), //Report id
                                                                lab_id,
                                                                lab_name,
                                                                etReportType.getText().toString().trim(),
                                                                new ArrayList<>(Arrays.asList(uri.toString())),
                                                                new Date(System.currentTimeMillis()),
                                                                patient_id,
                                                                userPatient.getName() // Patient name
                                                        );
                                                        db.collection(CS.Report)
                                                                .document(report.getReport_id())
                                                                .set(report)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(ReportActivity.this, "Report added succesfully!", Toast.LENGTH_LONG).show();
                                                                        dg.dismiss();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        dg.dismiss();
                                                                    }
                                                                });
                                                        progressBar.setVisibility(View.GONE);
                                                        btnSubmit.setVisibility(View.VISIBLE);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dg.dismiss();
                                                        progressBar.setVisibility(View.GONE);
                                                        btnSubmit.setVisibility(View.VISIBLE);
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                btnSubmit.setVisibility(View.VISIBLE);
                                                dg.dismiss();
                                            }
                                        });
                                    }
                                });

                        dg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                                db.collection(CS.Report)
                                        .orderBy(CS.date, Query.Direction.DESCENDING)
                                        .whereEqualTo(CS.patient_id, patient_id)
//                                    .whereEqualTo(CS.doctorid, firebaseUser.getUid())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                Log.e(TAG, "onSuccess: " + queryDocumentSnapshots.size());
                                                alReport.clear();
                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                                    try {
                                                        Report report = documentSnapshot.toObject(Report.class);
                                                        alReport.add(report);
                                                    } catch (Exception ex) {
                                                        Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                                    }
                                                }
                                                CU.hideProgressbar();
                                                setAdapter(alReport);

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                CU.hideProgressbar();
                                                setAdapter(alReport);
                                                Log.e(TAG, "onFailure: " + e.getMessage());
                                            }
                                        });
                            }
                        });
                    }
                });

                dg.show();
            }
        });
        CU.showProgressbar(mContext);
    }

    private void setAdapter(ArrayList<Report> alReport) {
        if (alReport.size() > 0) {
            rvReport.setVisibility(View.VISIBLE);
            llNoData.setVisibility(View.GONE);
            Log.e(TAG, "onSuccess: " + alReport.size());
            ReportAdapter reportAdapter = new ReportAdapter(mContext, alReport, CS.DOCTOR);
            rvReport.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
            rvReport.setAdapter(reportAdapter);
        } else {
            rvReport.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
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

    private void getFileChooserIntent() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            String[] mimeTypes = {"image/*"};
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(Intent.createChooser(intent, "Choose valid report..."), PICK_REPORT);
        }
    }

    private boolean checkPermission(final String permission) {
        if (ContextCompat.checkSelfPermission(mContext, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((MainActivity) mContext, permission)) {

                AlertDialog.Builder builder = new AlertDialog
                        .Builder(mContext);
                builder.setMessage("• The permission is needed to upload report\n"
                        + "Do you want to give permissions?");
                builder.setTitle("Permission needed");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((MainActivity) mContext,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions((MainActivity) mContext,
                        new String[]{permission},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_REPORT:
                    if (tvFile != null && data != null && data.getData() != null) {
                        Uri uri = data.getData();
                        filePathUri = Uri.fromFile(new File(CU.getPath(mContext, uri)));
                        tvFile.setText(uri.getPath());
                        tvFile.setError(null);
                        Log.e(TAG, "onActivityResult: " + filePathUri);
                        Log.e(TAG, "onActivityResult: extension: " + MimeTypeMap.getFileExtensionFromUrl(filePathUri.toString()));
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0) {
//                getFileChooserIntent();
                String[] mimeTypes = {"image/*"};
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(Intent.createChooser(intent, "Choose valid proof..."), PICK_REPORT);
            } else {
                Toast.makeText(mContext, "Cannot upload file, due to denied permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        int type = intent.getIntExtra(CS.type, CS.LAB);
        MainActivity.fromReport = type == CS.DOCTOR;
        super.onBackPressed();
    }
}
