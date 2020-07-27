package com.rad5.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rad5.chatapp.Adapters.FragmentaAdapter;
import com.rad5.chatapp.Fragments.fragment_Users;
import com.rad5.chatapp.Fragments.fragment_Chat;
import com.rad5.chatapp.Fragments.profileFragment;
import com.rad5.chatapp.Models.Chats;
import com.rad5.chatapp.Models.Users;

import java.util.HashMap;
import java.util.Objects;

import static android.app.SearchManager.QUERY;
import static com.rad5.chatapp.Fragments.fragment_Chat.fragmentChatActivity;
import static com.rad5.chatapp.Fragments.fragment_Users.fragmentUser;
import static com.rad5.chatapp.Fragments.profileFragment.fragmentProfile;
import static com.rad5.chatapp.SettingsFragment.Key_Pref_Example_Switch;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ImageView profilePic;
    TextView userName;
    FirebaseUser mUser;
    DatabaseReference mDatabaseRef;
    private Dialog mDialog;
    private ImageView mNav_imageView;
    public static boolean isUserFragmentVisible;
    private TextView mNav_userName, mUserEmail;
    public static Boolean Connected;
    private static final String TAG = "MainActivityResults";
    UserInput mUserInput;
    private String name, resultName;
    private ViewPager mViewpager;
    private Toolbar toolbar;
    private ActionBarDrawerToggle mToggle;


    public interface UserInput {public void onSearchPressEnter(String input);}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        SharedPreferences sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchPref = sSharedPreferences.getBoolean(Key_Pref_Example_Switch, false);
        Toast.makeText(this, Boolean.toString(switchPref), Toast.LENGTH_LONG).show();
        setDrawable(toolbar);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        //isInternetConnected();
        displayUserDataIfConnected();
        openFragment(new fragment_Chat());
    }

    void Init() {
        profilePic = findViewById(R.id.User_Image);
        userName = findViewById(R.id.User_name);
        toolbar = findViewById(R.id.tooBar);
        mViewpager = findViewById(R.id.Pager);
        setNavigationForAll();
        getCurrentUserEmail(mUserEmail);
    }


    private void displayNumOfUnreadMessagesInConnected(final ViewPager mViewpager) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Chats");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FragmentaAdapter pagerAdapter = new FragmentaAdapter(getSupportFragmentManager());
                int unread = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Chats chat = dataSnapshot1.getValue(Chats.class);
                    if (chat.getReceiver().equals(mUser.getUid()) && !chat.isisSeen()) {
                        unread++;
                    }
                }
                if (unread == 0) {
                    pagerAdapter.addFragments(new fragment_Chat(), "Chat");
                } else {
                    pagerAdapter.addFragments(new fragment_Chat(), "(" + unread + ")" + "Chat");
                }
                pagerAdapter.addFragments(new fragment_Users(), "Users");
                pagerAdapter.addFragments(new profileFragment(), "Profile");
                mViewpager.setAdapter(pagerAdapter);
                TabLayout layout = findViewById(R.id.tablayout);
                layout.setupWithViewPager(mViewpager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void displayUserDataIfConnected() {
        ProgressDialog();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDialog.dismiss();
                Users users = dataSnapshot.getValue(Users.class);
                userName.setText(users.getUsername());
                name = users.getUsername();
                mNav_userName.setText(users.getUsername());
                Log.d("users", dataSnapshot.getValue().toString());
                if (users.getImageUrl().equals("default")) {
                    profilePic.setImageResource(R.drawable.profile);
                } else {
                    Glide.with(getApplicationContext()).
                            load(users.getImageUrl())
                            .into(profilePic);
                    Glide.with(getApplicationContext()).
                            load(users.getImageUrl()).
                            into(mNav_imageView);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Database error", databaseError.getMessage());
            }
        });
    }


    private void ProgressDialog() {
        mDialog = new Dialog(MainActivity.this);
        mDialog.setContentView(R.layout.dialog);
        mDialog.show();
    }

    public void isInternetConnected() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Boolean isConnected = snapshot.getValue(Boolean.class);
                if (isConnected) {
                    Connected = true;
                    //displayNumOfUnreadMessagesInConnected(mViewpager);
                    Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                    Connected = false;
                }
                Log.d(TAG, "InternetConnection  " + (Connected));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }

    public final boolean isInternetOn() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            Log.i(TAG, "Internet Connected");
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            Log.i(TAG, "No Internet Connected");

            return false;
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem search = menu.findItem(R.id.usersSearch);
        SearchView mSearchView = (SearchView) search.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent();
                intent.putExtra("UsersInput", s);
                intent.setAction(QUERY);
                sendBroadcast(intent);
                mUserInput.onSearchPressEnter(s);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }
    public void status(String status) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        mDatabaseRef.updateChildren(hashMap);
    }
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        SharedPreferences sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchPref = sSharedPreferences.getBoolean(Key_Pref_Example_Switch, false);
        Toast.makeText(this, Boolean.toString(switchPref), Toast.LENGTH_LONG).show();
    }

    private void setNavigationForAll() {
        NavigationView nView = findViewById(R.id.navigationView);
        View header = nView.getHeaderView(0);
        mNav_imageView = header.findViewById(R.id.nav_userImage);
        mNav_userName = header.findViewById(R.id.nav_userName);
        mUserEmail = header.findViewById(R.id.nav_userEmail);
        nView.setNavigationItemSelectedListener(this);
        BottomNavigationView sBottomNavigationView = findViewById(R.id.button_nav);
        sBottomNavigationView.setOnNavigationItemSelectedListener(navigationButton);
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationButton = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem pMenuItem) {
            switch (pMenuItem.getItemId()) {
                case R.id.butt_home:
                    if (fragmentChatActivity == false) {
                        openFragment(new fragment_Chat());
                    }
                    pMenuItem.setChecked(true);
                    break;
                case R.id.butt_prof:
                    if (fragmentProfile == false) {
                        openFragment(new profileFragment());
                    }
                    pMenuItem.setChecked(true);
                    break;
            }
            return false;
        }
    };


    private void getCurrentUserEmail(TextView email) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = mUser.getEmail();
        if (mUser != null) {
            email.setText(userEmail);
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        Fragment fragment1 = fragment;
        try {
            mUserInput = (UserInput) fragment1;
        } catch (ClassCastException e) {
            Log.d(TAG, e + " m  error found");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem pMenuItem) {
        DrawerLayout sDrawerLayout = findViewById(R.id.drawerlayout);
        switch (pMenuItem.getItemId()) {
            case R.id.findPeople:
                if (fragmentUser == false) {
                    openFragment(new fragment_Users());
                }
                sDrawerLayout.closeDrawers();
                pMenuItem.setChecked(true);
                break;
            case R.id.contact_us:
                Toast.makeText(this, "Clicked contact us", Toast.LENGTH_LONG).show();
                sDrawerLayout.closeDrawers();
                pMenuItem.setChecked(true);
                break;
            case R.id.nav_notif:
                Toast.makeText(this, "Clicked Notification", Toast.LENGTH_LONG).show();
                sDrawerLayout.closeDrawers();
                pMenuItem.setChecked(true);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, Login.class));
                finish();
                break;
        }
        return false;
    }


    public  void openFragment(Fragment sFragment) {
        Log.d(TAG, "!Chat  Fragment visibility = " + fragmentChatActivity);
        Log.d(TAG, "!User Fragment visibility = " + fragmentUser);
        Log.d(TAG, "!Profile Fragment visibility = " + fragmentProfile);
        String backStateName = sFragment.getClass().getName();
        boolean fragmentInBack = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);
        if (!fragmentInBack) {
            FragmentTransaction sFragmentTransaction =  getSupportFragmentManager().beginTransaction();
            sFragmentTransaction.replace(R.id.frameLayout, sFragment);
            sFragmentTransaction.addToBackStack(backStateName);
            sFragmentTransaction.commit();
        }
    }

    private void setDrawable(Toolbar pToolbar) {
        setSupportActionBar(pToolbar);
        DrawerLayout nDrawerLayout = findViewById(R.id.drawerlayout);
        mToggle = new ActionBarDrawerToggle(this, nDrawerLayout, pToolbar, R.string.open, R.string.close);
        nDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                startActivity(new Intent(this,Settings.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}