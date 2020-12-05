package com.shrewd.healthcard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.shrewd.healthcard.R;
import com.shrewd.healthcard.Utilities.CU;

public class SignUpActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 112;
    private static final String TAG = "SignUpActivity";
    private TextView tvLogin;
    private Context mContext;
    private TextView tvGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private EditText etUsername, etPassword, etConfirmPassword;
    private TextView tvSignUp;
    private SpinKitView progressBar;
    private EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initUI();

        //Set Actionbar
        setTitle("Sign-up");

        ClickListeners();

        //Configure Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Configure facebook login
        ConfigureGoogleSignIn();
    }

    private void initUI() {
        mContext = SignUpActivity.this;
        tvLogin = (TextView) findViewById(R.id.tvLogin_signup);
        tvGoogle = (TextView) findViewById(R.id.tvGoogle);
        etEmail = (EditText) findViewById(R.id.etEmail);
//        etUsername = (EditText) findViewById(R.previousSelectedItemId.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        progressBar = (SpinKitView) findViewById(R.id.progressBar);
        CheckBox cbPassword = (CheckBox) findViewById(R.id.cbPassword);
        CheckBox cbConfirmPassword = (CheckBox) findViewById(R.id.cbConfirmPassword);
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
        cbConfirmPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        Log.e(TAG, "initUI: " + tvSignUp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (CU.updateUI(mContext, mAuth.getCurrentUser(), false, false)) {
                finish();
            }
        }
    }

    private void ClickListeners() {
        tvGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContinueWithGoogle();
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                CU.hideKeyboard((Activity) mContext);
                boolean IsValid = true;
                if (!etEmail.getText().toString().contains("@") && !etEmail.getText().toString().contains(".com")) {
                    etEmail.setError("Invalid email-Id");
                    etEmail.setText("");
                    etEmail.requestFocus();
                    IsValid = false;
                }
                /*if (etUsername.getText() == null) {
                    etUsername.setError("Enter userid");
                    etUsername.setText("");
                    IsValid = false;
                }
                String userid = etUsername.getText().toString();
                if (userid.contains(" ")) {
                    etUsername.setError("userid cannot contains space");
                    etUsername.setText("");
                    IsValid = false;
                }
                if (userid.contains("@gmail.com") ||  userid.contains("~") || userid.contains("!") || userid.contains("@") || userid.contains("#") || userid.contains("$") || userid.contains("%") || userid.contains("^") || userid.contains("&") || userid.contains("*") || userid.contains("(") || userid.contains(")") || userid.contains("?") || userid.contains(",")) {
                    etUsername.setError("userid cannot contains special characters except underscore (_) and comma (,)");
                    etUsername.setText("");
                    IsValid = false;
                }*/
                Log.e(TAG, "onClick: confirmed pswd" + etConfirmPassword.getText().toString());
                Log.e(TAG, "onClick: password" + etPassword.getText().toString());
                if (!etConfirmPassword.getText().toString().equals(etPassword.getText().toString())) {
                    etConfirmPassword.setError("Password Mismatch");
                    etConfirmPassword.setText("");
                    IsValid = false;
                }
                if (etPassword.getText().toString().length() < 6) {
                    etPassword.setError("Password should be at least 6 characters long");
                    etPassword.setText("");
                    IsValid = false;
                }
                if (etPassword.getText() == null) {
                    etPassword.setError("Enter Password");
                    etPassword.setText("");
                    IsValid = false;
                }
                if (IsValid) {
                    mAuth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    clearEditText();
                                                    Toast.makeText(SignUpActivity.this, "Verification email Sent", Toast.LENGTH_SHORT).show();
//                                                    startActivity(new Intent(mContext, LoginActivity.class));
//                                                    finish();
                                                }
                                            });
                                        }
                                        Log.e(TAG, "createUserWithEmail:success");
                                        if (CU.updateUI(mContext, mAuth.getCurrentUser(), false, true))
                                            finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        if (task.getException() != null) {
                                            String exception = task.getException().getMessage();
                                            if (exception.contains("email") && exception.contains("badly") && exception.contains("formatted")) {
                                                etEmail.setError("Invalid email");
                                                etEmail.requestFocus();
                                            } else if (exception.contains("email") && exception.contains("already") && exception.contains("in use")) {
                                                etEmail.setError("email is used");
                                                etEmail.requestFocus();
                                            }
                                            Log.e(TAG, "createUserWithEmail:failure " + exception);
                                        }
                                        Toast.makeText(mContext, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        CU.updateUI(mContext, null);
                                    }
                                    hideProgressBar();
                                }
                            });
                } else {
                    hideProgressBar();
                }
            }
        });
    }

    private void clearEditText() {
        etEmail.setText("");
        etConfirmPassword.setText("");
        etPassword.setText("");
    }

    private void ContinueWithGoogle() {
        showProgressBar();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void ConfigureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
        mGoogleSignInClient.signOut();
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
                            CU.updateUI(mContext, user);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            CU.updateUI(mContext, null);
                        }
                    }
                });
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
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showProgressBar() {
        if (progressBar != null && tvSignUp != null) {
            progressBar.setVisibility(View.VISIBLE);
            tvSignUp.setVisibility(View.GONE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null && tvSignUp != null) {
            progressBar.setVisibility(View.GONE);
            tvSignUp.setVisibility(View.VISIBLE);
        }
    }
}
