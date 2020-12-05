package com.shrewd.healthcard.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shrewd.healthcard.ModelClass.User;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CS;
import com.shrewd.healthcard.databinding.ActivityVerifyBinding;

import java.io.IOException;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class VerifyActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";
    private Context mContext;
    public NfcAdapter mNfcAdapter;
    public static boolean NFCEnabled = false;
    private ProgressDialog pd;
    private ActivityVerifyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = VerifyActivity.this;

        initNFC();
        pd = new ProgressDialog(mContext);
        pd.setMessage("Waiting for NFC Tag...");
        pd.setCanceledOnTouchOutside(false);

        //region set Actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Verify");
        }

        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvContactNo = findViewById(R.id.tvContactNo);
        TextView tvGender = findViewById(R.id.tvGender);
        TextView tvDesignation = findViewById(R.id.tvDesignation);
        TextView tvDOB = findViewById(R.id.tvDOB);
        TextView tvTime = findViewById(R.id.tvTime);
        TextView tvLicenseNo = findViewById(R.id.tvLicenseNo);
        View vLicenseNo = findViewById(R.id.vLicenseNo);
        LinearLayout llLicenseNo = findViewById(R.id.llLicenseNo);

        MaterialButton btnVerify = findViewById(R.id.btnVerify);
        MaterialButton btnReject = findViewById(R.id.btnReject);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
        }

        final ImageView ivVerify = findViewById(R.id.ivVerify);
        final LinearLayout llVerify = findViewById(R.id.llVerify);
        final TextView tvArea = findViewById(R.id.tvArea);

        Intent intent = getIntent();
        final User user = intent.getParcelableExtra(CS.User);

        if (user.getType() == CS.PATIENT) {
            llVerify.setVisibility(View.GONE);
            btnReject.setVisibility(View.GONE);
            btnVerify.setText("Register with NFC");
        } else {
            llVerify.setVisibility(View.VISIBLE);
            btnReject.setVisibility(View.VISIBLE);
            btnVerify.setText("Verify");
        }

        llLicenseNo.setVisibility(user.getType() == CS.DOCTOR ? View.VISIBLE : View.GONE);
        vLicenseNo.setVisibility(user.getType() == CS.DOCTOR ? View.VISIBLE : View.GONE);

        tvLicenseNo.setText(user.getDoc_license_no());

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set type to -1 to reject
                db.collection(CS.User)
                        .document(user.getUser_id())
                        .update(CS.type, -1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                Toast.makeText(mContext, "Profile rejected", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getType() != CS.PATIENT) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(CS.User)
                            .document(user.getUser_id())
                            .update(CS.verified,true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void v) {
                                    Toast.makeText(mContext, "Profile approved", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(mContext, "Profile approved", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                    pd.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Log.e(TAG, "onFailure: " + e.getMessage());
                                }
                            });
                } else {
                    pd.show();
                    NFCEnabled = true;
                }
            }
        });

        tvUsername.setText(user.getName());
        tvEmail.setText(user.getEmail());
        tvContactNo.setText(String.valueOf(user.getContact_no()));
        switch ((int) user.getGender()) {
            case CS.Male:
                tvGender.setText("Male");
                break;
            case CS.Female:
                tvGender.setText("Female");
                break;
            case CS.NA:
                tvGender.setText("Not Specified");
                break;
        }

        switch ((int) user.getType()) {
            case CS.DOCTOR:
                tvDesignation.setText("Doctor");
                break;
            case CS.LAB:
                tvDesignation.setText("Lab Assistant");
                break;
            case CS.GOVERNMENT:
                tvDesignation.setText("Government");
                break;
            case CS.ADMIN:
                tvDesignation.setText("Admin");
                break;
            default:
                tvDesignation.setText("Patient");
                break;
        }
        tvDOB.setText(new SimpleDateFormat("dd-MM-yyyy").format(user.getDob()) + "");
        tvArea.setText(user.getAddress());
        tvTime.setText(user.getReg_date() + "");

        Glide.with(mContext).load(user.getProof()).into(ivVerify);

        ivVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dg = new Dialog(mContext);
                dg.setContentView(R.layout.dg_image_preview);
                ImageView iv = dg.findViewById(R.id.ivImg);
                TextView tvName = dg.findViewById(R.id.tvName);
                tvName.setText("Proof");
                iv.setImageDrawable(ivVerify.getDrawable());
                Window window = dg.getWindow();
                if (window != null) {
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen._500sdp));
                    window.setBackgroundDrawable(getDrawable(R.drawable.bg_dg_newuser));
                    window.setGravity(Gravity.BOTTOM);
                }
                dg.show();
            }
        });
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

    @Override
    protected void onStart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        NFCEnabled = false;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    public void initNFC() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Toast.makeText(mContext, "This device doesn't support NFC", Toast.LENGTH_SHORT).show();
            ((Activity) mContext).finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to continue!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
            return;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NFCEnabled) {
            Intent intent1 = getIntent();
            User user = intent1.getParcelableExtra(CS.User);
            String userid = intent1.getStringExtra(CS.user_id);
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                    || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) && user != null && user.getType() == CS.PATIENT) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                try {
                    NdefRecord mimeRecord = NdefRecord.createMime("text/plain", userid.getBytes());
                    NdefMessage message = new NdefMessage(new NdefRecord[]{mimeRecord});
                    if (isTagEmpty(tag) && writeTag(message, tag)) {
                        Toast.makeText(this, "NFC Tag Registered", Toast.LENGTH_SHORT)
                                .show();
                        if (user != null) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection(CS.User)
                                    .document(user.getUser_id())
                                    .update(CS.verified, true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void v) {
                                            Toast.makeText(mContext, "Profile approved", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                            pd.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Log.e(TAG, "onFailure: " + e.getMessage());
                                        }
                                    });
                        } else {
                            Log.e(TAG, "onNewIntent: " + "User null");
                            pd.dismiss();
                        }
                    } else {
                        Toast.makeText(mContext, "NFC already registered! Try again with different NFC", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "onNewIntent: " + e.getMessage());
                    pd.dismiss();
                }
            }

        } else {
            Toast.makeText(mContext, "Cannot register NFC here!", Toast.LENGTH_LONG).show();
            pd.dismiss();
        }
    }

    private boolean isTagEmpty(Tag tag) {
        if (tag != null) {
//            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            Ndef ndef = Ndef.get(tag);
            try {
                NdefMessage ndefMessage = ndef.getCachedNdefMessage();
                if (ndefMessage != null) {
                    NdefRecord[] records = ndefMessage.getRecords();
                    if (records.length > 0) {
                        for (NdefRecord ndefRecord : records) {
                            if (ndefRecord.getPayload().length > 0) {
                                byte[] payload = ndefRecord.getPayload();
                                String id = new String(payload);
                                Log.e(TAG, "onNewIntent: ");
                                return false;
//                                Toast.makeText(mContext, "id: " + id, Toast.LENGTH_SHORT).show();
                            } else {
                                return true;
                            }
                        }
                    } else {
                        return true;
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "onNewIntent: " + ex.getMessage());
                Toast.makeText(mContext, "Read error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "onNewIntent: tag is null");
        }
        return false;
    }

    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(getApplicationContext(),
                            "Error: tag not writable", Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(mContext, "Write Error", Toast.LENGTH_SHORT).show();
                    /*mTextValue.setError(String.format(getString(R.string.error_value_toobig),
                            size, ndef.getMaxSize()));*/
                    return false;
                }
                ndef.writeNdefMessage(message);

                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean writeToNFC(Ndef ndef, String userid) {
        if (ndef != null) {
            try {
                ndef.connect();
                ndef.writeNdefMessage(new NdefMessage(userid.getBytes()));
//                NdefRecord mimeRecord = NdefRecord.createMime("text/plain", userid.getBytes(Charset.forName("US-ASCII")));
//                ndef.writeNdefMessage(new NdefMessage(mimeRecord));
                ndef.close();
                //Write Successful
                return true;
            } catch (IOException | FormatException e) {
                Log.e(TAG, "writeToNFC: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

}
