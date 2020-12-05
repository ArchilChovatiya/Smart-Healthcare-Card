package com.shrewd.healthcard.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.shrewd.healthcard.Fragment.AnalysisFragment;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.ViewHolder> {

    private static final String TAG = "AnalysisAdapter";
    private final Context mContext;
    private final LayoutInflater inflater;
    private ArrayList<PieEntry> pcPatientpieEntries = new ArrayList<>();
    private PieDataSet pcPatientpieDataSet;
    private PieData pcPatientpieData;
    private int clientTextColor;
    private String clientMessage;
    private ClientThread clientThread;
    private String message;
    private ArrayList<BarEntry> lineEntries;
    private int FLAG = 0;

    public AnalysisAdapter(Context mContext) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CS.History)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        HashMap<String, Float> map = new HashMap<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            if (map.containsKey(doc.getString(CS.disease))) {
                                try {
                                    map.put(doc.getString(CS.disease), map.get(doc.getString(CS.disease)) + 1);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onSuccess: error: " + ex.getMessage());
                                }
                            } else {
                                map.put(doc.getString(CS.disease), 1f);
                            }
                        }

                        Set<String> set = map.keySet();

                        Log.e(TAG, "onSuccess: ***");
                        pcPatientpieEntries = new ArrayList<>();
                        for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
                            String s = it.next();
                            Log.e(TAG, "onSuccess: " + s);
                            Log.e(TAG, "onSuccess: " + map.get(s));
                            pcPatientpieEntries.add(new PieEntry(map.get(s), s));
                        }


                    }
                });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_analysis, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (position) {
            case 0:
                holder.bcPatient.setVisibility(View.GONE);
                holder.pcPatient.setVisibility(View.VISIBLE);
                holder.lcPatient.setVisibility(View.GONE);
                break;
            case 1:
                holder.bcPatient.setVisibility(View.VISIBLE);
                holder.pcPatient.setVisibility(View.GONE);
                holder.lcPatient.setVisibility(View.GONE);
                break;
            case 2:
                holder.bcPatient.setVisibility(View.GONE);
                holder.pcPatient.setVisibility(View.GONE);
                holder.lcPatient.setVisibility(View.VISIBLE);
                break;
        }

        if (pcPatientpieEntries.size() > 0) {
            pcPatientpieDataSet = new PieDataSet(pcPatientpieEntries, "");
            pcPatientpieData = new PieData(pcPatientpieDataSet);
            holder.pcPatient.setData(pcPatientpieData);
            pcPatientpieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            pcPatientpieDataSet.setSliceSpace(2f);
            pcPatientpieDataSet.setValueTextColor(Color.WHITE);
            pcPatientpieDataSet.setValueTextSize(10f);
            pcPatientpieDataSet.setSliceSpace(5f);
            holder.pcPatient.animateXY(1500, 500);
            Log.e(TAG, "onSuccess: ***");
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (FLAG == 1) {
                    BarDataSet set1 = new BarDataSet(lineEntries, "The year 2017");
                    set1.setColors(ColorTemplate.MATERIAL_COLORS);
                    ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                    dataSets.add(set1);

                    BarData data = new BarData(dataSets);

                    data.setValueTextSize(10f);
                    data.setBarWidth(0.9f);

                    holder.bcPatient.setTouchEnabled(false);
                    holder.bcPatient.setData(data);
                    holder.bcPatient.animateY(1500);
                    FLAG = 0;
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final PieChart pcPatient;
        private final LineChart lcPatient;
        private final BarChart bcPatient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pcPatient = itemView.findViewById(R.id.pcPatient);
            lcPatient = itemView.findViewById(R.id.lcPatient);
            bcPatient = itemView.findViewById(R.id.bcPatient);
        }
    }

    class ClientThread implements Runnable {

        private final String host;
        private final Long port;
        private final int type;
        private Socket socket;
        private BufferedReader input;

        public ClientThread(String host, Long port, int type) {
            this.host = host;
            this.port = port;
            this.type = type;
        }

        @Override
        public void run() {

            try {

                InetAddress serverAddr = InetAddress.getByName(host);
                showMessage("Connecting to Server...", clientTextColor, true);

                socket = new Socket(serverAddr, port.intValue());

                if (socket.isBound()) {
                    showMessage("Connected to Server...", clientTextColor, true);
                }

                /*String str = type + "#21 10.23 45 0.25" +
                        "22 11.23 46 0.50" +
                        "23 12.23 47 0.75" +
                        "24 13.23 48 1.00" +
                        "25 14.23 49 1.25" +
                        "26 15.23 40 0.50" +
                        "27 16.23 41 0.75" +
                        "28 17.23 42 2.00" +
                        "29 18.23 43 2.25";*/
                clientMessage = type + "";
                showMessage(clientMessage, Color.BLUE, false);
                if (null != clientThread) {
                    if (clientMessage.length() > 0) {
                        clientThread.sendMessage(clientMessage);
                    }
//            edMessage.setText("");
                } else {
                    Log.e(TAG, "run: client thread is null");
                }


                while (!Thread.currentThread().isInterrupted()) {

                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    message = input.readLine();

                    if (message != null && !message.equals("Server Disconnected...")) {
                        Log.e(TAG, "run: " + message);
                        ((Activity) mContext).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                String[] alData = message.split(" ");
                                /*for (int i = 0; i < alData.size(); ++i) {
                                    Toast.makeText(mContext, "" + alData.get(i), Toast.LENGTH_SHORT).show();
                                }*/
//                                lcPatientlineEntries = new ArrayList();
                                lineEntries = new ArrayList<>();

                                for (int i = 0; i < alData.length; i++) {
//                                    Log.e(TAG, "run: " + Float.parseFloat(String.valueOf(alData[i])));
                                    try {
//                                        lcPatientlineEntries.add(new Entry(i+10,i));
                                        Log.e(TAG, "run: " + alData[i]);
                                        lineEntries.add(new BarEntry(Float.valueOf(alData[i]), (int) i));
                                    } catch (Exception ex) {
                                        Log.e(TAG, "run: " + ex.getMessage());
                                    }
                                }

                                FLAG = 1;

                                /*for (int i = 0; i < alData.length; i++) {
//                                    Log.e(TAG, "run: " + Float.parseFloat(String.valueOf(alData[i])));
                                    try {
//                                        lcPatientlineEntries.add(new Entry(i+10,i));
                                        Log.e(TAG, "run: " + new Entry(Float.valueOf(alData[i]), i));
                                        lineEntries.add(new Entry(Float.valueOf(alData[i]).floatValue(), i));
                                    } catch (Exception ex) {
                                        Log.e(TAG, "run: " + ex.getMessage());
                                    }
                                }*/
//                                lineEntries.add(new Entry(2f, 0));

                                /*lineEntries.add(new Entry(2f, 0));
                                lineEntries.add(new Entry(4f, 1));
                                lineEntries.add(new Entry(6f, 1));
                                lineEntries.add(new Entry(8f, 3));
                                lineEntries.add(new Entry(7f, 4));
                                lineEntries.add(new Entry(3f, 3));*/

                                /*lcPatientlineDataSet = new LineDataSet(lineEntries, "");
                                lcPatientlineData = new LineData(lcPatientlineDataSet);
                                lcPatient.setData(lcPatientlineData);
                                lcPatientlineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                                lcPatientlineDataSet.setValueTextColor(Color.BLACK);
                                lcPatientlineDataSet.setValueTextSize(18f);
                                lcPatient.animateY(1500);*/

                            }
                        });
                    }
                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted();
                        message = "Server Disconnected...";
                        showMessage(message, Color.RED, false);
                        break;
                    }
                    showMessage("Server: " + message, clientTextColor, true);

                }

            } catch (NullPointerException e3) {
                showMessage("error returned", Color.RED, true);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        void sendMessage(final String message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (null != socket) {
                            /*PrintWriter out = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream())),
                                    true);*/
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            out.println(message);

                        }
                    } catch (Exception e) {
                        Log.e(TAG, "run: error: " + e.getMessage());
                    }
                }
            }).start();
        }

        public void showMessage(final String message, final int color, final Boolean value) {
        /*handler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: " + message);
//                msgList.addView(textView(message, color, value));
            }
        });*/
            Log.e(TAG, "showMessage: " + message);


        }

    }
}
