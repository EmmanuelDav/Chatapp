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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgottenPassword extends AppCompatActivity {

    EditText rsEmail;
    Button bt_reset;
    FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);
        rsEmail = findViewById(R.id.resetEmail);
        bt_reset = findViewById(R.id.btn_reset);
        mFirebaseAuth = FirebaseAuth.getInstance();

        bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = rsEmail.getText().toString();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(),"No email address found",Toast.LENGTH_LONG).show();
                }else {
                    mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"An email address will" +
                                        " be sent to you to verify and change your password",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(forgottenPassword.this,Login.class));
                            }else {
                                String error  = task.getException().getMessage();
                                Log.d("resetPasswordError",error);
                                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
