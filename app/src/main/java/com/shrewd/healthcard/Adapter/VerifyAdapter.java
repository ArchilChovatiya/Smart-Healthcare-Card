package com.shrewd.healthcard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shrewd.healthcard.Activity.VerifyActivity;
import com.shrewd.healthcard.ModelClass.User;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.databinding.ItemVerificationBinding;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class VerifyAdapter extends RecyclerView.Adapter<VerifyAdapter.ViewHolder> {

    private static final String TAG = "HistoryAdapter";
    private final Context mContext;
    private final ArrayList<User> alUser;
    private final LayoutInflater inflater;
    private final ArrayList<String> alUserid;
    private ItemVerificationBinding binding;

    public VerifyAdapter(Context mContext, ArrayList<User> alUser, ArrayList<String> alUserid) {
        this.mContext = mContext;
        this.alUser = alUser;
        this.alUserid = alUserid;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemVerificationBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final User user = alUser.get(position);
        holder.binding.tvName.setText(user.getName());
        holder.binding.tvEmail.setText(user.getEmail());
        holder.binding.llVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VerifyActivity.class);
                intent.putExtra(CS.user_id, alUserid.get(position));
                intent.putExtra(CS.User, user);
                mContext.startActivity(intent);
            }
        });
        switch ((int) user.getType()) {
            case CS.ADMIN:
                holder.binding.ivType.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.verified_user));
                break;
            case CS.DOCTOR:
                holder.binding.ivType.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.doctor));
                break;
            case CS.LAB:
                holder.binding.ivType.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.lab_assistant));
                break;
            case CS.GOVERNMENT:
                holder.binding.ivType.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.government));
                break;
            default:
                holder.binding.ivType.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.patient3));
                break;
        }
        Log.e(TAG, "onBindViewHolder: ");
    }

    @Override
    public int getItemCount() {
        return alUser.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemVerificationBinding binding;

        public ViewHolder(ItemVerificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
