package com.shrewd.healthcard.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.shrewd.healthcard.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BarAdapter extends RecyclerView.Adapter<BarAdapter.ViewHolder> {

    private static final String TAG = "BarAdapter";
    private final List<String> alData;
    private final Context mContext;
    private final LayoutInflater inflater;
    ArrayList<BarEntry> yVals1;

    public BarAdapter(Context mContext, List<String> alData) {
        this.mContext = mContext;
        this.alData = alData;
        inflater = LayoutInflater.from(mContext);
        getEntries();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_bar_chart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BarDataSet set1;

        set1 = new BarDataSet(yVals1, "The year 2017");
        set1.setColors(ColorTemplate.MATERIAL_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);

        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        holder.chart.setTouchEnabled(false);
        holder.chart.setData(data);
        holder.chart.animateY(1500);

    }

    @Override
    public int getItemCount() {
        return alData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final BarChart chart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chart = itemView.findViewById(R.id.chart1);
        }
    }

    private void getEntries() {
        yVals1 = new ArrayList<>();
        try {
            for (int i = 0; i < alData.size(); i++) {
                yVals1.add(new BarEntry(i, Float.valueOf(alData.get(i))));
            }
        } catch (Exception ex) {
            Log.e(TAG, "getEntries: "+ex.getMessage() );
        }
    }
}
