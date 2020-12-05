package com.shrewd.healthcard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CU;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 111;
    private static final String TAG = "LoginActivity";
    private static final String EMAIL = "email";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    public SpinKitView progressBar;
    private Context mContext;
    private TextView tvGoogle;
    private TextView tvSignUp;
    private TextView tvLogin;
    private EditText etEmail, etPassword;
    private TextView tvForgotPassword;
    private ImageView ivBack;
    private CheckBox cbPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");

        //UI
        initUI();

        //Configuration
        ConfigureGoogleSignIn();
    }

    private void initUI() {
        mContext = LoginActivity.this;
        progressBar = (SpinKitView) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        tvGoogle = (TextView) findViewById(R.id.tvGoogle);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        cbPassword = (CheckBox) findViewById(R.id.cbPassword);
        cbPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        ClickListeners();
        /*Blurry.with(mContext)
                .from(BitmapFactory.decodeResource(getResources(),R.mipmap.memes_doodle))
                .into((ImageView) findViewById(R.previousSelectedItemId.ivBackground));*/
    }

    private void ClickListeners() {
        tvGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContinueWithGoogle();
            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SignUpActivity.class));
                finish();
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                tvLogin.setText("");
                CU.hideKeyboard((LoginActivity) mContext);
                if (etEmail.getText().toString().trim().equals("") || !CU.isValidEmail(etEmail.getText().toString().trim())) {
                    etEmail.setError("Invalid email");
                    etEmail.requestFocus();
                    hideProgressBar();
                } else if (etPassword.getText().toString().trim().equals("")) {
                    etPassword.setError("Enter Password");
                    etPassword.requestFocus();
                    hideProgressBar();
                } else {
                    LogInWithCredential();
                }
//                hideProgressBar();
                tvLogin.setText("Log in");
            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                if (!CU.isNullOrEmpty(email) && CU.isValidEmail(email)) {
                    mAuth.sendPasswordResetEmail(etEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(mContext, "email sent", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    etEmail.setError("Invalid email");
                    etEmail.requestFocus();
                }
            }
        });
    }

    private void LogInWithCredential() {
        Log.e(TAG, "LogInWithCredential: email: " + etEmail.getText().toString().trim());
        Log.e(TAG, "LogInWithCredential: password: " + etPassword.getText().toString().trim());
        mAuth.signInWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.e(TAG, "signInWithEmail:success " + user.getUid());
                            clearEditText();
                            if (CU.updateUI(mContext, user))
                                finish();
                        } else {
                            Log.e(TAG, "signInWithEmail:Failure ");
                            Toast.makeText(mContext, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            clearEditText();
                            if (CU.updateUI(mContext, null))
                                finish();
                        }
                        hideProgressBar();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressBar();
                    }
                });
    }

    private void ConfigureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
        mGoogleSignInClient.signOut();
    }

    private void clearEditText() {
        etEmail.setText("");
        etEmail.requestFocus();
        etPassword.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && CU.updateUI(mContext, user, false, true)) {
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                hideProgressBar();
                Log.e(TAG, "Google sign in failed", e);
                Toast.makeText(mContext, "Faild: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(mContext, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            CU.updateUI(mContext, null);
                            clearEditText();
                            hideProgressBar();
                        }
                        // ...
                    }
                });
    }

    private void ContinueWithGoogle() {
        showProgressBar();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void showProgressBar() {
        if (progressBar != null && tvLogin != null) {
            progressBar.setVisibility(View.VISIBLE);
            tvLogin.setVisibility(View.GONE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null && tvLogin != null) {
            progressBar.setVisibility(View.GONE);
            tvLogin.setVisibility(View.VISIBLE);
        }
    }
}
