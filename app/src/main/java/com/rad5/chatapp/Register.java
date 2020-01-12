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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    EditText mUsername, mEmail, mPasswod;
    Button registerButton;
    FirebaseAuth mAuthUser;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        IntIds();

    }

    private void IntIds() {
        mUsername = findViewById(R.id.Username);
        mEmail = findViewById(R.id.email);
        mPasswod = findViewById(R.id.password);
        registerButton = findViewById(R.id.button);
        mAuthUser = FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String passwod = mPasswod.getText().toString();
                String email = mEmail.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(passwod) || TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "fill in the missing field", Toast.LENGTH_LONG).show();
                } else if (passwod.length() < 6) {
                    Toast.makeText(Register.this, "Password mut be more that six numbers", Toast.LENGTH_LONG).show();

                } else{
                        registration(username, passwod, email);
                    }
                }


        });
    }

    private void registration(final String Username, String Pssword, String email) {
        mAuthUser.createUserWithEmailAndPassword(email,Pssword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser mUser = mAuthUser.getCurrentUser();
                            String Userid =mUser.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(Userid);
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",Userid);
                            hashMap.put("Username", Username);
                            hashMap.put("ImageUrl","default");
                            mDatabase.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(Register.this,MainActivity.class));
                                    }
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("login error",e.getMessage());

            }
        });

    }


}