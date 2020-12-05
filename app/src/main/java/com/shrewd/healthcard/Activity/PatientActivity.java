package com.shrewd.healthcard.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.Adapter.HistoryAdapter;
import com.shrewd.healthcard.ModelClass.History;
import com.shrewd.healthcard.ModelClass.User;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;
import com.shrewd.healthcard.databinding.DgNewRecordBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PatientActivity extends AppCompatActivity {

    private static final String TAG = "PatientActivity";
    private RecyclerView rvHistory;
    private LinearLayout llNoData;
    private Context mContext;
    private CoordinatorLayout crdPatient;
    private ArrayList<History> alHistory = new ArrayList<>();
    private FloatingActionButton fabAdd;
    User userPatient;
    private String patientid;
    public static final int REPORT = 0;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        mContext = PatientActivity.this;
        crdPatient = (CoordinatorLayout) findViewById(R.id.crdPatient);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);

        Intent intent = getIntent();
        patientid = intent.getStringExtra(CS.user_id);

        if (patientid == null)
            return;

        //region set Actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Patient");
        }

        rvHistory = (RecyclerView) findViewById(R.id.rvHistory);
        llNoData = (LinearLayout) findViewById(R.id.llNoData);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        }

        Log.e(TAG, "onCreate: " + patientid);
        db.collection(CS.User)
                .document(patientid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dsPatient) {
                        if (dsPatient.exists()) {
                            userPatient = dsPatient.toObject(User.class);
                            if (userPatient == null) {
                                return;
                            }
                            ActionBar actionBar = getSupportActionBar();
                            if (actionBar != null) {
                                actionBar.setDisplayHomeAsUpEnabled(true);
                                actionBar.setTitle("Records - " + userPatient.getName());
                            }

                            fabAdd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Dialog dg = new Dialog(mContext);
                                    DgNewRecordBinding bndNewRecord = DgNewRecordBinding.inflate(getLayoutInflater());
                                    dg.setContentView(bndNewRecord.getRoot());

                                    Window window = dg.getWindow();
                                    if (window != null) {
                                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        window.setBackgroundDrawable(getDrawable(R.drawable.bg_dg_newuser));
                                        window.setGravity(Gravity.BOTTOM);
                                    }
                                    dg.setCanceledOnTouchOutside(false);

                                    bndNewRecord.btnSubmit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (CU.isNullOrEmpty(bndNewRecord.etDisease)) {
                                                bndNewRecord.etDisease.setError("Field required");
                                                bndNewRecord.etDisease.setText("");
                                                bndNewRecord.etDisease.requestFocus();
                                                return;
                                            }
                                            if (CU.isNullOrEmpty(bndNewRecord.etMedicines)) {
                                                bndNewRecord.etMedicines.setError("Field required");
                                                bndNewRecord.etMedicines.setText("");
                                                bndNewRecord.etMedicines.requestFocus();
                                                return;
                                            }
                                            if (CU.isNullOrEmpty(bndNewRecord.etSymptoms)) {
                                                bndNewRecord.etSymptoms.setError("Field required");
                                                bndNewRecord.etSymptoms.setText("");
                                                bndNewRecord.etSymptoms.requestFocus();
                                                return;
                                            }
                                            if (CU.isNullOrEmpty(bndNewRecord.etVigilance)) {
                                                bndNewRecord.etVigilance.setError("Field required");
                                                bndNewRecord.etVigilance.setText("");
                                                bndNewRecord.etVigilance.requestFocus();
                                                return;
                                            }

                                            CU.showProgressbar(mContext);

                                            ArrayList<String> alMedicine = new ArrayList<>(Arrays.asList(bndNewRecord.etMedicines.getText().toString().trim().split("\n")));
                                            ArrayList<String> alSymptoms = new ArrayList<>(Arrays.asList(bndNewRecord.etSymptoms.getText().toString().trim().split("\n")));
                                            ArrayList<String> alVigilance = new ArrayList<>(Arrays.asList(bndNewRecord.etVigilance.getText().toString().trim().split("\n")));
                                            ArrayList<String> alAllergies = new ArrayList<>(Arrays.asList(bndNewRecord.etAllergies.getText().toString().trim().split("\n")));

                                            SharedPreferences sp = getSharedPreferences("GC", MODE_PRIVATE);
                                            String doctorid = sp.getString(CS.doctor_id, "");
                                            String doctorname = sp.getString(CS.doctor_name, "");

                                            if (!CU.isNullOrEmpty(doctorid) && !CU.isNullOrEmpty(doctorname)) {

                                                if (!CU.requestPermissions(mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CS.PermissionRequestCode.LOCATION_NEW_USER)) {
                                                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                        return;
                                                    }
                                                    fusedLocationClient.requestLocationUpdates(
                                                            CU.getLocationRequest(),
                                                            new LocationCallback() {
                                                                @Override
                                                                public void onLocationResult(LocationResult locationResult) {
                                                                    if (locationResult == null || locationResult.getLastLocation() == null) {
                                                                        CU.snackBar(mContext, "Failed to fetch location", Snackbar.LENGTH_LONG);
                                                                        return;
                                                                    }
                                                                    if (fusedLocationClient != null) {
                                                                        fusedLocationClient.removeLocationUpdates(this);
                                                                    }
                                                                    Location location = locationResult.getLastLocation();

                                                                    try {
                                                                        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                                                                        Address address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
                                                                        String city_name = address.getLocality();
                                                                        String state_name = address.getAdminArea();
                                                                        String country_name = address.getCountryName();

                                                                        History history = new History(
                                                                                doctorid,
                                                                                doctorname,
                                                                                userPatient.getUser_id(),
                                                                                userPatient.getName(),
                                                                                "",
                                                                                (!CU.isNullOrEmpty(city_name) ? city_name + ", " + state_name + ", " + country_name : userPatient.getAddress()),
                                                                                bndNewRecord.etDisease.getText().toString().trim(),
                                                                                alMedicine,
                                                                                alSymptoms,
                                                                                alVigilance,
                                                                                new Date(System.currentTimeMillis()),
                                                                                bndNewRecord.etReportSuggestion.getText().toString().trim(),
                                                                                location.getLatitude(),
                                                                                location.getLongitude()
                                                                        );

                                                                        alAllergies.addAll(userPatient.getAllergies());
                                                                        dsPatient.getReference().update(CS.allergy, alAllergies);

                                                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                        db.collection(CS.History)
                                                                                .add(history)
                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentReference documentReference) {
                                                                                        CU.hideProgressbar();
                                                                                        Toast.makeText(mContext, "Data added successfully", Toast.LENGTH_SHORT).show();
                                                                                        dg.dismiss();
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(mContext, "Failed to add data", Toast.LENGTH_SHORT).show();
                                                                                        dg.dismiss();
                                                                                    }
                                                                                });
                                                                    } catch (Exception ex) {
                                                                        Log.e(TAG, "onLocationResult: " + ex.getMessage());
                                                                    }
                                                                }
                                                            },
                                                            Looper.getMainLooper()
                                                    );
                                                }
                                            }

                                            dg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialog) {
                                                    loadPatientHistory(patientid);
                                                }
                                            });

                                        }
                                    });

                                    dg.show();
                                }
                            });
                        }
                    }
                });

        CU.showProgressbar(mContext);
        loadPatientHistory(patientid);

    }

    private void loadPatientHistory(String patientid) {
        CU.getFirestore().collection(CS.History)
                .orderBy(CS.date, Query.Direction.DESCENDING)
                .whereEqualTo(CS.patient_id, patientid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        CU.hideProgressbar();
                        Log.e(TAG, "onSuccess: " + queryDocumentSnapshots.size());
                        alHistory.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            try {
                                History history = documentSnapshot.toObject(History.class);
                                alHistory.add(history);
                            } catch (Exception ex) {
                                Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                            }
                        }
                        setAdapter(alHistory);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CU.hideProgressbar();
                        CU.snackBar(mContext, "Failed to load records!", Snackbar.LENGTH_LONG);
                    }
                });
    }

    private void setAdapter(ArrayList<History> alHistory) {
        if (alHistory.size() > 0) {
            rvHistory.setVisibility(View.VISIBLE);
            llNoData.setVisibility(View.GONE);
            Log.e(TAG, "onSuccess: " + alHistory.size());
            HistoryAdapter historyAdapter = new HistoryAdapter(mContext, alHistory, CS.Page.PATIENT_ACTIVITY);
            rvHistory.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
            rvHistory.setAdapter(historyAdapter);
        } else {
            rvHistory.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, REPORT, Menu.NONE, "Report").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            case REPORT:
                if (CU.isNullOrEmpty(patientid)) {
                    CU.snackBar(mContext, "Failed to fetch reports!\nPlease try again later", Snackbar.LENGTH_LONG);
                    return false;
                }
                Intent intent1 = new Intent(mContext, ReportActivity.class);
                intent1.putExtra(CS.user_id, patientid);
                intent1.putExtra(CS.type, (long) CS.DOCTOR);
                startActivity(intent1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}