package com.shrewd.healthcard.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.shrewd.healthcard.Activity.HistoryActivity;
import com.shrewd.healthcard.ModelClass.History;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.Utilities.CU;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private static final String TAG = "HistoryAdapter";
    private final Context mContext;
    private final ArrayList<History> alHistory;
    private final LayoutInflater inflater;
    private final int type, page;

    public HistoryAdapter(Context mContext, ArrayList<History> alHistory, int page) {
        this.mContext = mContext;
        this.alHistory = alHistory;
        inflater = LayoutInflater.from(mContext);
        this.page = page;
        SharedPreferences sp = mContext.getSharedPreferences("GC", Context.MODE_PRIVATE);
        type = (int) sp.getLong(CS.type, -1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final History history = alHistory.get(position);

        setTools(holder, type);
        if (page == CS.Page.PATIENT_ACTIVITY) {
            holder.tvDoctor.setText(history.getDoctor_name() != null ? CS.Dr + history.getDoctor_name() : "N/A");
        } else if (type == CS.DOCTOR) {
            holder.tvPatient.setText(history.getPatient_name() != null ? history.getPatient_name() : "N/A");
        } else if (type == CS.PATIENT) {
            holder.tvDoctor.setText(history.getDoctor_name() != null ? CS.Dr + history.getDoctor_name() : "N/A");
        } else if (type == CS.ADMIN) {
            holder.tvPatient.setText(history.getPatient_name());
            holder.tvDoctor.setText(CS.Dr + history.getDoctor_name());
        }

        holder.tvDiseaseHistory.setText(history.getDisease());
        holder.tvDiseasePatient.setText(history.getDisease());

        Date cDate = history.getDate();
        String fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
        holder.tvDateHistory.setText(fDate);
        holder.tvDatePatient.setText(fDate);
        holder.llHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, HistoryActivity.class);
                intent.putExtra(CS.History, history);
                if (type != CS.LAB || CU.isNullOrEmpty(history.getReport_suggestion())) {
//                    intent.putExtra(CS.type, CS.REPORT);
                    mContext.startActivity(intent);
                } else {
                    Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dg_report_suggesstion);
                    MaterialButton btnOk = dialog.findViewById(R.id.btnOk);
                    TextView tvSuggestedReport = dialog.findViewById(R.id.tvSuggestedReport);
                    tvSuggestedReport.setText(history.getReport_suggestion());
                    Window window = dialog.getWindow();
                    if (window != null) {
                        window.setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_dg_rounded));
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            }
        });
        Log.e(TAG, "onBindViewHolder: ");
    }

    private void setTools(ViewHolder holder, int type) {
        holder.llHistoryPatient.setVisibility(View.GONE);
        holder.llPatient.setVisibility(View.GONE);
        if (page == CS.Page.PATIENT_ACTIVITY) {
            holder.llHistoryPatient.setVisibility(View.VISIBLE);
        } else if (type == CS.DOCTOR) {
            holder.llPatient.setVisibility(View.VISIBLE);
        } else if (type == CS.ADMIN) {
            holder.llHistoryPatient.setVisibility(View.VISIBLE);
            holder.llPatient.setVisibility(View.VISIBLE);
        } else {
            holder.llHistoryPatient.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return alHistory.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDateHistory, tvDatePatient, tvDiseaseHistory, tvDiseasePatient, tvDoctor, tvPatient;
        private final LinearLayout llHistoryPatient, llPatient, llHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llHistoryPatient = (LinearLayout) itemView.findViewById(R.id.llHistoryPatient);
            llHistory = (LinearLayout) itemView.findViewById(R.id.llHistory);

            llPatient = (LinearLayout) itemView.findViewById(R.id.llPatient);
            tvDiseaseHistory = (TextView) itemView.findViewById(R.id.tvDiseaseHistory);
            tvDateHistory = (TextView) itemView.findViewById(R.id.tvDateHistory);
            tvDiseasePatient = (TextView) itemView.findViewById(R.id.tvDiseasePatient);
            tvDoctor = (TextView) itemView.findViewById(R.id.tvDoctor);
            tvPatient = (TextView) itemView.findViewById(R.id.tvPatient);
            tvDatePatient = (TextView) itemView.findViewById(R.id.tvDatePatient);
        }
    }
}
