package com.rad5.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    EditText mUsername, mEmail, mPassword;
    Button registerButton;
    FirebaseAuth mAuthUser;
    DatabaseReference mDatabase;
    Dialog mDialog;
    public static boolean isActivityRunning;
    SignInButton mGoogleSignIn;
    private GoogleSignInClient googleSignInClient;
    private static final int reqCode = 111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        IntIds();
        googleSignIn();

    }

    private void IntIds() {
        mUsername = findViewById(R.id.Username);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        registerButton = findViewById(R.id.button);
        mAuthUser = FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
                    hideDialog();
                    Toast.makeText(Register.this, "Fill Missing Fields", Toast.LENGTH_LONG).show();
                } else if (password.length() < 6) {
                    hideDialog();
                    Toast.makeText(Register.this, "Password must be up to seven digits", Toast.LENGTH_LONG).show();
                } else {
                    registration(username, password, email);
                }
            }
        });
    }

    public void confirmEmailAddress() {
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if (User != null) {
            User.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Verification email sent", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "error occurred while  sending email", Toast.LENGTH_LONG).show();
                    }
                }

            });
            startActivity(new Intent(getApplicationContext(), Login.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    private void registration(final String Username, String Password, String email) {
        mAuthUser.createUserWithEmailAndPassword(email, Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser mUser = mAuthUser.getCurrentUser();
                            String Userid = mUser.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(Userid);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", Userid);
                            hashMap.put("Username", Username);
                            hashMap.put("ImageUrl", "default");
                            mDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        hideDialog();
                                        confirmEmailAddress();
                                    }
                                }
                            });
                        } else {
                            hideDialog();
                            String error = task.getException().getMessage();
                            Toast.makeText(Register.this, error, Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("login error", e.getMessage());
            }
        });
    }

    private void showDialog() {
        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.dialog);
        mDialog.show();
    }

    private void hideDialog() {
        mDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;
    }

    private void googleSignIn() {
        mGoogleSignIn = findViewById(R.id.googleignin);
        GoogleSignInOptions mGnOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.clientID))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, mGnOption);
        mGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mSignInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(mSignInIntent, reqCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case reqCode:
                    Task<GoogleSignInAccount> STask = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount sAccount = STask.getResult(ApiException.class);
                        if (sAccount != null) {
                            String personName = sAccount.getDisplayName();
                            String personEmail = sAccount.getEmail();
                            String personId = sAccount.getId();
                            Uri personPhoto = sAccount.getPhotoUrl();
                            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(personId);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", personId);
                            hashMap.put("Username", personName);
                            hashMap.put("ImageUrl", personPhoto);
                            mDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> pTask) {
                                    hideDialog();
                                }
                            });
                        }
                    } catch (ApiException pE) {
                        Log.d("Register", "Error occured do to " + pE.getCause());
                    }
            }
        }
    }
}
