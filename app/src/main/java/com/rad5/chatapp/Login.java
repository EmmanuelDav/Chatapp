package com.rad5.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText email, password;
    Button login;
    FirebaseAuth auth;
    TextView mfPassword;
    FirebaseUser mFirebaseUser;
    ProgressBar mProgressBar;
    public static boolean isActivityRunning;
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser!= null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }else {


        }
        isActivityRunning = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.Login_password);
        login = findViewById(R.id.Userlogin);
        mProgressBar = findViewById(R.id.progressBar);
        mfPassword = findViewById(R.id.forgottenPassword);
        auth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {
                    Toast.makeText(getApplicationContext(), "empty email address or password", Toast.LENGTH_LONG).show();
                    hideDialog();
                } else {
                    auth.signInWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        hideDialog();
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user.isEmailVerified()) {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        }else {
                                            Toast.makeText(getApplicationContext(), "Please check your email to verify your account", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();

                                        }
                                    } else {
                                        hideDialog();


                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Login error", e.getMessage());
                        }
                    });

                }

            }
        });
        mfPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), forgottenPassword.class));
            }
        });


    }

    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;
    }

    public void signup(View view) {
        startActivity(new Intent(this,Register.class));
    }
}
