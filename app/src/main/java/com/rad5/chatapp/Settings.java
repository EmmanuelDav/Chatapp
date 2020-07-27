package com.rad5.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction sFragmentTransaction = getSupportFragmentManager().beginTransaction();
        sFragmentTransaction.replace(android.R.id.content,new SettingsFragment());
        sFragmentTransaction.commit();
    }
}