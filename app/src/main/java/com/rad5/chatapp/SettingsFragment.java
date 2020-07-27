package com.rad5.chatapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String Key_Pref_Example_Switch = "Notification_value";

    @Override
    public void onCreatePreferences(Bundle pBundle, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
    }
}
