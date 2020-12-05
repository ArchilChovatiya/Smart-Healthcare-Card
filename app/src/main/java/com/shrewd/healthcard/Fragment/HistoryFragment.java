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
import com.shrewd.healthcard.databinding.FragmentHistoryBinding;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private Context mContext;
    private ArrayList<History> alHistory = new ArrayList<>();
    private FirebaseUser firebaseUser;
    private static final String TAG = "HistoryFragment";
    private FragmentHistoryBinding binding;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(getLayoutInflater(), container, false);
        mContext = getContext();

        if (mContext == null)
            return binding.getRoot();
        CU.setActionBar(mContext, CS.Page.HISTORY);

        SharedPreferences sp = mContext.getSharedPreferences("GC", MODE_PRIVATE);
        long type = sp.getLong(CS.type, 1);
        Log.e(TAG, "onCreateView: " + type);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CU.showProgressbar(mContext);


        if (type == CS.ADMIN) {
            binding.flAdmin.setVisibility(View.VISIBLE);
            binding.flPatient.setVisibility(View.GONE);
            db.collection(CS.History)
                    .orderBy(CS.date, Query.Direction.DESCENDING)
//                    .whereEqualTo(CS.patientid, firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            alHistory.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                try {
                                    /*History history = new History(documentSnapshot.getString(CS.doctorid), documentSnapshot.getString(CS.patientid),
                                            documentSnapshot.getString(CS.reportid), documentSnapshot.getString(CS.area), documentSnapshot.getString(CS.disease),
                                            (ArrayList<String>) documentSnapshot.get(CS.medicine), (ArrayList<String>) documentSnapshot.get(CS.symptoms), (ArrayList<String>) documentSnapshot.get(CS.vigilance),
                                            documentSnapshot.getTimestamp(CS.date).toDate());*/
                                    History history = documentSnapshot.toObject(History.class);
                                    alHistory.add(history);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                }
                            }
                            CU.hideProgressbar();
                            setAdapter(alHistory, type);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CU.hideProgressbar();
                            setAdapter(alHistory, type);
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        } else {
            binding.flAdmin.setVisibility(View.GONE);
            binding.flPatient.setVisibility(View.VISIBLE);
            db.collection(CS.History)
                    .orderBy(CS.date, Query.Direction.DESCENDING)
                    .whereEqualTo(CS.patient_id, firebaseUser.getUid())
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
                            setAdapter(alHistory, type);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CU.hideProgressbar();
                            setAdapter(alHistory, type);
                            Log.e(TAG, "onFailure: " + e.getMessage());
                        }
                    });

        }

        return binding.getRoot();
    }

    private void setAdapter(ArrayList<History> alHistory, long type) {
        switch ((int) type) {
            case CS.ADMIN:
                if (alHistory.size() > 0) {
                    binding.rvHistoryAdmin.setVisibility(View.VISIBLE);
                    binding.llNoDataAdmin.noDataContent.setVisibility(View.GONE);
                    Log.e(TAG, "onSuccess: " + alHistory.size());
                    HistoryAdapter historyAdapter = new HistoryAdapter(mContext, alHistory, CS.Page.HISTORY);
                    binding.rvHistoryAdmin.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    binding.rvHistoryAdmin.setAdapter(historyAdapter);
                } else {
                    binding.rvHistoryAdmin.setVisibility(View.GONE);
                    binding.llNoDataAdmin.noDataContent.setVisibility(View.VISIBLE);
                }
                break;
            case CS.PATIENT:
                if (alHistory.size() > 0) {
                    binding.rvHistory.setVisibility(View.VISIBLE);
                    binding.llNoData.noDataContent.setVisibility(View.GONE);
                    Log.e(TAG, "onSuccess: " + alHistory.size());
                    HistoryAdapter historyAdapter = new HistoryAdapter(mContext, alHistory, CS.Page.HISTORY);
                    binding.rvHistory.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
                    binding.rvHistory.setAdapter(historyAdapter);
                } else {
                    binding.rvHistory.setVisibility(View.GONE);
                    binding.llNoData.noDataContent.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(mContext, LoginActivity.class));
            ((Activity) mContext).finish();
        }
        super.onAttach(context);
    }
}
