package com.shrewd.healthcard.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.Activity.MainActivity;
import com.shrewd.healthcard.Adapter.VerifyAdapter;
import com.shrewd.healthcard.ModelClass.User;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;
import com.shrewd.healthcard.databinding.FragmentVerifyBinding;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyFragment extends Fragment {


    private Context mContext;
    private static final String TAG = "VerifyFragment";
    private ArrayList<User> alUser = new ArrayList<>();
    private ArrayList<String> alUserid = new ArrayList<>();
    private FragmentVerifyBinding binding;

    public VerifyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVerifyBinding.inflate(getLayoutInflater(), container, false);
        mContext = getContext();

        if (mContext == null)
            return binding.getRoot();
        CU.setActionBar(mContext, CS.Page.VERIFY);

        Log.e(TAG, "onCreateView: ");
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart: ");
        Log.e(TAG, "onAttach: ");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CU.showProgressbar(mContext);

        db.collection(CS.User)
                .orderBy(CS.type)
                .whereGreaterThan(CS.type, -1)
                .orderBy(CS.reg_date, Query.Direction.DESCENDING)
                .whereEqualTo(CS.verified, false)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        alUser.clear();
                        alUserid.clear();
                        Log.e(TAG, "onSuccess: " + queryDocumentSnapshots.size());
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            try {
                                if (documentSnapshot.getLong(CS.type) == 4) {
                                    continue;
                                }
                                User user = documentSnapshot.toObject(User.class);
                                if (user == null) {
                                    continue;
                                }
                                user.setUser_id(documentSnapshot.getId());
//                                user.setContactno(documentSnapshot.getLong(CS.contactno));
                                Log.e(TAG, "onSuccess: " + user.getType());
                                Log.e(TAG, "onSuccess: " + user.getAddress());
                                Log.e(TAG, "onSuccess: " + user.getEmail());
                                Log.e(TAG, "onSuccess: " + user.getName());
                                Log.e(TAG, "onSuccess: " + user.getProof());
                                Log.e(TAG, "onSuccess: " + user.getContact_no());
                                Log.e(TAG, "onSuccess: " + user.getDob());
                                Log.e(TAG, "onSuccess: " + user.getGender());
                                Log.e(TAG, "onSuccess: " + user.getReg_date());
                                Log.e(TAG, "onSuccess: " + user.isVerified());
                                alUser.add(user);
                                alUserid.add(documentSnapshot.getId());
                            } catch (Exception ex) {
                                Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                            }
                        }
                        CU.hideProgressbar();
                        setAdapter(alUser, alUserid);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CU.hideProgressbar();
                        setAdapter(alUser, alUserid);
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
        super.onStart();
    }

    private void setAdapter(ArrayList<User> alUser, ArrayList<String> alUserid) {
        if (alUser.size() > 0) {
            binding.rvVerify.setVisibility(View.VISIBLE);
            binding.llNoData.noDataContent.setVisibility(View.GONE);
            Log.e(TAG, "onSuccess: " + alUser.size());
            VerifyAdapter verifyAdapter = new VerifyAdapter(mContext, alUser, alUserid);
            binding.rvVerify.setLayoutManager(new LinearLayoutManager(mContext));
            binding.rvVerify.setAdapter(verifyAdapter);
        } else {
            binding.rvVerify.setVisibility(View.GONE);
            binding.llNoData.noDataContent.setVisibility(View.VISIBLE);
        }
    }
}
