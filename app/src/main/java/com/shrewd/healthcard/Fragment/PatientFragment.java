package com.shrewd.healthcard.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.shrewd.healthcard.Adapter.HistoryAdapter;
import com.shrewd.healthcard.ModelClass.History;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;
import com.shrewd.healthcard.databinding.FragmentPatientBinding;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientFragment extends Fragment {

    private Context mContext;
    private String TAG = "PatientFragment";
    private FirebaseUser firebaseUser;
    private ArrayList<History> alHistory = new ArrayList<>();
    private FragmentPatientBinding binding;

    public PatientFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPatientBinding.inflate(getLayoutInflater(), container, false);
        mContext = getContext();

        if (mContext == null)
            return binding.getRoot();
        CU.setActionBar(mContext, CS.Page.PATIENT);

        MainActivity.NFCPatientEnabled = true;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(mContext, LoginActivity.class));
            ((Activity) mContext).finish();
        }

        SharedPreferences sp = mContext.getSharedPreferences("GC", MODE_PRIVATE);
        long type = sp.getLong(CS.type, 1);
        Log.e(TAG, "onCreateView: " + type);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CU.showProgressbar(mContext);

        if (type == CS.ADMIN) {
            db.collection(CS.History)
                    .orderBy(CS.date, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            alHistory.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    History history = documentSnapshot.toObject(History.class);
                                    Log.e(TAG, "onSuccess: " + history.getDisease());
                                    Log.e(TAG, "onSuccess: " + history.getArea());
                                    Log.e(TAG, "onSuccess: " + history.getPatient_id());
                                    Log.e(TAG, "onSuccess: " + history.getDoctor_id());
                                    alHistory.add(history);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                }
                            }
                            CU.hideProgressbar();
                            setAdapter(alHistory, CS.ADMIN);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CU.hideProgressbar();
                            setAdapter(alHistory, CS.ADMIN);
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        } else if (type == CS.DOCTOR){
            db.collection(CS.History)
                    .orderBy(CS.date, Query.Direction.DESCENDING)
                    .whereEqualTo(CS.doctor_id, firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            alHistory.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    History history = documentSnapshot.toObject(History.class);
                                    alHistory.add(history);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                }
                            }
                            CU.hideProgressbar();
                            setAdapter(alHistory, CS.DOCTOR);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CU.hideProgressbar();
                            setAdapter(alHistory, CS.DOCTOR);
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        }

        CU.setLayout(type, binding.flDoctor, null, binding.flLab, null, binding.flAdmin);
        return binding.getRoot();
    }

    private void setAdapter(ArrayList<History> alHistory, int type) {
        switch (type) {
            case CS.DOCTOR:
                if (alHistory.size() > 0) {
                    binding.rvHistory.setVisibility(View.VISIBLE);
                    binding.llNoData.noDataContent.setVisibility(View.GONE);
                    Log.e(TAG, "onSuccess: " + alHistory.size());
                    HistoryAdapter historyAdapter = new HistoryAdapter(mContext, alHistory, CS.Page.PATIENT);
                    binding.rvHistory.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    binding.rvHistory.setAdapter(historyAdapter);
                } else {
                    binding.rvHistory.setVisibility(View.GONE);
                    binding.llNoData.noDataContent.setVisibility(View.VISIBLE);
                }
                break;
            case CS.ADMIN:
                if (alHistory.size() > 0) {
                    binding.rvHistoryAdmin.setVisibility(View.VISIBLE);
                    binding.llNoDataAdmin.noDataContent.setVisibility(View.GONE);
                    Log.e(TAG, "onSuccess: " + alHistory.size());
                    HistoryAdapter historyAdapter = new HistoryAdapter(mContext, alHistory, CS.Page.PATIENT);
                    binding.rvHistoryAdmin.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    binding.rvHistoryAdmin.setAdapter(historyAdapter);
                } else {
                    binding.rvHistoryAdmin.setVisibility(View.GONE);
                    binding.llNoDataAdmin.noDataContent.setVisibility(View.VISIBLE);
                }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.NFCPatientEnabled = false;
    }
}