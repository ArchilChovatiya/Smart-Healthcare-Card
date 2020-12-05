package com.shrewd.healthcard.Utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shrewd.healthcard.Activity.MainActivity;
import com.shrewd.healthcard.BuildConfig;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.databinding.ProgressBarBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class CU {

    private static final String TAG = "CU";
    private static Snackbar snackbar;
    private static Toast globeToast;
    private static FirebaseFirestore db;
    private static Dialog progressDialog;
    private static LocationRequest locationRequest;

    public static FirebaseFirestore getFirestore() {
        try {
            if (db == null) {
                db = FirebaseFirestore.getInstance();
            }
        } catch (Exception ex) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }

    public static void setActionBar(Context mContext, int type) {
        setActionBar(mContext, type, "");
    }

    public static void setActionBar(Context mContext, int type, String title) {
        switch (type) {
            case CS.Page.ABOUT:
                ((MainActivity) mContext).binding.tvTitle.setText("About us");
                break;
            case CS.Page.ANALYSIS:
                ((MainActivity) mContext).binding.tvTitle.setText("Analysis");
                break;
            case CS.Page.HISTORY:
                ((MainActivity) mContext).binding.tvTitle.setText("Patient History");
                break;
            case CS.Page.HOME:
                ((MainActivity) mContext).binding.tvTitle.setText("Home");
                break;
            case CS.Page.HOME_REMEDIES:
                ((MainActivity) mContext).binding.tvTitle.setText("Home Remedies");
                break;
            case CS.Page.PATIENT:
                ((MainActivity) mContext).binding.tvTitle.setText("Patient");
                break;
            case CS.Page.REPORTS:
                ((MainActivity) mContext).binding.tvTitle.setText("Reports");
                break;
            case CS.Page.SETTINGS:
                ((MainActivity) mContext).binding.tvTitle.setText("Settings");
                break;
            case CS.Page.VERIFY:
                ((MainActivity) mContext).binding.tvTitle.setText("Verify");
                break;
        }
    }

    public static void displaySelectedFragment(Fragment fragment, FragmentManager fragmentManager, int id) {
        if (fragment == null) {
            Log.e("Fragment Null", "onNavigationItemSelected: ");
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(id, fragment);
        try {
            ft.commitAllowingStateLoss();
        } catch (Exception ex) {
            Log.e(TAG, "displaySelectedFragment: " + ex);
        }
    }

    public static void showProgressBar(View progressBar) {
        if (progressBar != null && progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            if (progressBar == null) {
                Log.e(TAG, "showProgressBar: progressbar null");
            } else {
                Log.e(TAG, "showProgressBar: already visible");
            }
        }
    }

    public static void hideProgressBar(View progressBar) {
        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        } else {
            if (progressBar == null) {
                Log.e(TAG, "showProgressBar: progressbar null");
            } else {
                Log.e(TAG, "showProgressBar: already visible");
            }
        }
    }

    public static boolean isNetworkEnabled(Context mContext) {
        if (mContext == null)
            return false;
        ConnectivityManager connectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectManager.getActiveNetworkInfo();
        if (netInfo != null) {
            if (netInfo.isConnected()) {
                Log.e("true", "isNetworkEnabled: ");
                return true;
            }
        }
        Log.e("false", "isNetworkEnabled: ");
        return false;
    }

    public static void reloadFragment(int id, FragmentManager fragmentManager) {
        // Reload current fragment
        Fragment frg = null;
        frg = fragmentManager.findFragmentById(id);
        if (frg == null) {
            Log.e(TAG, "reloadFragment: " + "fragment null");
        } else {
            Log.e(TAG, "reloadFragment: " + frg.getClass());
            final FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
        }
    }

    public static void setLayout(long op, FrameLayout flDoctor, FrameLayout flGovernment, FrameLayout flLab, FrameLayout flPatient, FrameLayout flAdmin) {
        switch ((int) op) {
            case CS.DOCTOR:
                if (flDoctor != null)
                    flDoctor.setVisibility(View.VISIBLE);
                if (flGovernment != null)
                    flGovernment.setVisibility(View.GONE);
                if (flLab != null)
                    flLab.setVisibility(View.GONE);
                if (flPatient != null)
                    flPatient.setVisibility(View.GONE);
                if (flAdmin != null)
                    flAdmin.setVisibility(View.GONE);
                break;
            case CS.PATIENT:
                if (flDoctor != null)
                    flDoctor.setVisibility(View.GONE);
                if (flGovernment != null)
                    flGovernment.setVisibility(View.GONE);
                if (flLab != null)
                    flLab.setVisibility(View.GONE);
                if (flPatient != null)
                    flPatient.setVisibility(View.VISIBLE);
                if (flAdmin != null)
                    flAdmin.setVisibility(View.GONE);
                break;
            case CS.LAB:
                if (flDoctor != null)
                    flDoctor.setVisibility(View.GONE);
                if (flGovernment != null)
                    flGovernment.setVisibility(View.GONE);
                if (flLab != null)
                    flLab.setVisibility(View.VISIBLE);
                if (flPatient != null)
                    flPatient.setVisibility(View.GONE);
                if (flAdmin != null)
                    flAdmin.setVisibility(View.GONE);
                break;
            case CS.GOVERNMENT:
                if (flDoctor != null)
                    flDoctor.setVisibility(View.GONE);
                if (flGovernment != null)
                    flGovernment.setVisibility(View.VISIBLE);
                if (flLab != null)
                    flLab.setVisibility(View.GONE);
                if (flPatient != null)
                    flPatient.setVisibility(View.GONE);
                if (flAdmin != null)
                    flAdmin.setVisibility(View.GONE);
                break;
            case CS.ADMIN:
                if (flDoctor != null)
                    flDoctor.setVisibility(View.GONE);
                if (flGovernment != null)
                    flGovernment.setVisibility(View.GONE);
                if (flLab != null)
                    flLab.setVisibility(View.GONE);
                if (flPatient != null)
                    flPatient.setVisibility(View.GONE);
                if (flAdmin != null)
                    flAdmin.setVisibility(View.VISIBLE);
                break;
        }
    }

    public static void setStatusBar(Context mContext, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.white));// set status background white
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidMobile(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches());
    }

    public static boolean hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    public static boolean showKeyboard(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null)
            return false;
        View view = ((Activity) mContext).getCurrentFocus();
        if (view != null) {
            imm.showSoftInput(view, 0);
            return true;
        }
        return false;
    }

    public static void showKeyboardForceFully(Context mContext) {
        try {
            if (!isKeyboardActive(((Activity) mContext).findViewById(R.id.main_layout))) {
                Log.e(TAG, "showKeyboardForceFully: keyboard showed");
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                Log.e(TAG, "showKeyboardForceFully: keyboard already showing");
            }
        } catch (Exception ex) {
            Log.e(TAG, "showKeyboardForceFully: " + ex.getMessage());
        }
    }

    public static boolean isKeyboardActive(View contentView) {
        Rect r = new Rect();
        contentView.getWindowVisibleDisplayFrame(r);
        int screenHeight = contentView.getRootView().getHeight();
        int keypadHeight = screenHeight - r.bottom;
        return (keypadHeight > screenHeight * 0.15);
    }

    public static void hideKeyboardForceFully(Context mContext) {
        try {
            if (isKeyboardActive(((Activity) mContext).findViewById(R.id.main_layout))) {
                Log.e(TAG, "hideKeyboardForceFully: keyboard hide");
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            } else {
                Log.e(TAG, "hideKeyboardForceFully: keyboard already hidden");
            }
        } catch (Exception ex) {
            Log.e(TAG, "hideKeyboardForceFully: " + ex.getMessage());
        }
    }


    public static boolean updateUI(Context mContext, FirebaseUser currentUser, boolean IsDefaultVerified, boolean IsRecentlySignedUp) {
        if (currentUser != null) {
            if (IsDefaultVerified || currentUser.isEmailVerified() || (currentUser.getEmail() != null && currentUser.getEmail().equals("admin@tfe.com"))) {
                mContext.startActivity(new Intent(mContext, MainActivity.class));
                return true;
//                Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
            } else {
                if (!IsRecentlySignedUp)
                    Toast.makeText(mContext, "email Unverified, Kindly check your mailbox for verification", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "updateUI: " + "user null");
        }
        return false;
    }

    public static boolean updateUI(Context mContext, FirebaseUser currentUser) {
        return updateUI(mContext, currentUser, false, false);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static boolean isNullOrEmpty(Object obj) {
        if (obj instanceof TextInputEditText) {
            TextInputEditText et = (TextInputEditText) obj;
            if (et.getText() != null && !et.getText().toString().trim().equals("")) {
                Log.e(TAG, "isNullOrEmpty: TextInputEditText false" + et.getText());
                return false;
            }
            Log.e(TAG, "isNullOrEmpty: TextInputEditText true" + et.getText());
        } else if (obj instanceof MaterialTextView) {
            MaterialTextView et = (MaterialTextView) obj;
            if (et.getText() != null && !et.getText().toString().trim().equals("")) {
                Log.e(TAG, "isNullOrEmpty: MaterialTextView false" + et.getText());
                return false;
            }
            Log.e(TAG, "isNullOrEmpty: MaterialTextView true" + et.getText());
        } else if (obj instanceof EditText) {
            EditText et = (EditText) obj;
            if (et.getText() != null && !et.getText().toString().trim().equals("")) {
                Log.e(TAG, "isNullOrEmpty: EditText false" + et.getText());
                return false;
            }
            Log.e(TAG, "isNullOrEmpty: EditText true" + et.getText());
        } else if (obj instanceof TextView) {
            TextView et = (TextView) obj;
            if (et.getText() != null && !et.getText().toString().trim().equals("")) {
                Log.e(TAG, "isNullOrEmpty: TextView false" + et.getText());
                return false;
            }
            Log.e(TAG, "isNullOrEmpty: TextView true" + et.getText());
        } else if (obj instanceof String) {
            String str = (String) obj;
            if (!str.trim().equals("")) {
                Log.e(TAG, "isNullOrEmpty: TextView false" + str);
                return false;
            }
            Log.e(TAG, "isNullOrEmpty: TextView true" + str);
        } else {
            if (obj != null && !"".equals(obj.toString())) {
                Log.e(TAG, "isNullOrEmpty: false");
                return false;
            }
            Log.e(TAG, "isNullOrEmpty: true");
        }
        return true;
    }

    public static boolean isDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = sdf.parse(date);
            Log.e(TAG, "isDate: " + d);
            return true;
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
            return false;
        }
    }

    public static Date getDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = sdf.parse(date);
            Log.e(TAG, "isDate: " + d);
            return d;
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
            return new Date();
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return "";
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return "";
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    public static void navigateTo(Context mContext, int id) {
        navigateTo(mContext, id, null);
    }

    public static void navigateTo(Context mContext, int id, Bundle bundle) {
        NavController navController = Navigation.findNavController((Activity) mContext, R.id.nav_host_fragment);
        navController.navigate(id, bundle);
    }

    public static Toast toast(Context mContext, String msg, int duration) {
        try {
            if (globeToast != null) {
                globeToast.cancel();
            }
            Toast toast = Toast.makeText(mContext, msg, duration);
            View view = toast.getView();
            view.getBackground().setColorFilter(mContext.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            TextView text = view.findViewById(android.R.id.message);
            text.setTextColor(mContext.getColor(R.color.white));
            globeToast = toast;
            return globeToast;
        } catch (Exception ex) {
            return Toast.makeText(mContext, msg, duration);
        }
    }

    public static void snackBar(Context mContext, String msg, int duration) {
        snackBar(((Activity) mContext).findViewById(android.R.id.content), msg, duration, "", null);
    }

    public static Snackbar snackBar(View rootView, String msg, int duration) {
        return snackBar(rootView, msg, duration, "", null);
    }

    public static Snackbar snackBar(Context mContext, String msg, int duration, String action, View.OnClickListener onClickListener) {
        return snackBar(((Activity) mContext).findViewById(android.R.id.content), msg, duration, action, onClickListener);
    }

    public static Snackbar snackBar(View rootView, String msg, int duration, String action, View.OnClickListener onClickListener) {
        try {
            if (snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
            }
            snackbar = Snackbar
                    .make(rootView, msg, duration);
            if (!isNullOrEmpty(action)) {
                snackbar.setAction(action, onClickListener);
            }
            View snackbarView = snackbar.getView();
            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setMaxLines(5);  //Or as much as you need
            snackbar.show();
            return snackbar;
        } catch (Exception ignored) {

        }
        return null;
    }

    public static void hideSnackBar() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    public static void showProgressbar(Context mContext) {
        showProgressbar(mContext, false);
    }

    public static void showProgressbar(Context mContext, boolean isCancelable) {
        if (mContext == null)
            return;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = new Dialog(mContext);
        ProgressBarBinding binding = ProgressBarBinding.inflate(((Activity) mContext).getLayoutInflater());
        progressDialog.setContentView(binding.getRoot());
        Window window = progressDialog.getWindow();
        if (window != null) {
            window.getAttributes().windowAnimations = R.style.ZoomingDialogAnimation;
            window.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.transparent));
            window.setLayout(mContext.getResources().getDimensionPixelSize(R.dimen._120sdp), mContext.getResources().getDimensionPixelSize(R.dimen._120sdp));
            window.setGravity(Gravity.CENTER);
        }
        progressDialog.setCancelable(isCancelable);
        Glide.with(mContext)
                .asGif()
                .circleCrop()
                .load(R.drawable.health)
                .into(binding.ivLoader);
        try {
            progressDialog.show();
        } catch (Exception ignored) {
        }
    }

    public static void hideProgressbar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {
            }
        }
    }

    public static long getMidnightTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static boolean requestPermissions(final Context mContext, final String[] permissions, final int requestCode, boolean compulsory) {
        if (mContext == null || permissions.length == 0) {
            return false;
        }
        boolean requested = false;
        for (String per : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, per)
                    != PackageManager.PERMISSION_GRANTED) {
                String msg;
                if (requestCode == CS.PermissionRequestCode.STORAGE) {
                    msg = "•  Permission needed to save report";
                } else if (requestCode == CS.PermissionRequestCode.LOCATION_NEW_USER ||
                        requestCode == CS.PermissionRequestCode.LOCATION_NEW_RECORD ||
                        requestCode == CS.PermissionRequestCode.LOCATION_NEW_REPORT) {
                    compulsory = true;
                    msg = "• Permission needed to fetch location";
                } else {
                    msg = "• Permission needed";
                }
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, per)) {

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                    builder.setMessage(msg
                            + "\nWant to give permissions?");
                    builder.setTitle("Needs of permissions");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) mContext,
                                    permissions,
                                    requestCode);
                        }
                    });
                    AlertDialog alertDialog = builder.show();
                    alertDialog.setCancelable(!compulsory);
                } else {
                    SharedPreferences sp = mContext.getSharedPreferences("GC", Context.MODE_PRIVATE);
                    int permissionResult = sp.getInt(per, CS.PermissionGrantResult.GRANTED);
                    if (permissionResult == CS.PermissionGrantResult.DONTASKAGAIN) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                        builder.setTitle("Cannot proceed further without permission");
                        builder.setMessage(msg);

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mContext.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                            }
                        });
                        AlertDialog alertDialog = builder.show();
                        alertDialog.setCancelable(!compulsory);
                    } else {
                        ActivityCompat.requestPermissions((Activity) mContext,
                                permissions,
                                requestCode);
                    }
                }
                requested = true;
            }
        }
        return requested;
    }

    public static boolean requestPermissions(final Context mContext, final String[] permissions, final int requestCode) {
        return requestPermissions(mContext, permissions, requestCode, false);
    }

    public static LocationRequest getLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
