package com.rad5.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.SearchManager.QUERY;

public class MainActivity extends AppCompatActivity {
    CircleImageView profilePic;
    TextView userName;
    public static Boolean isActivityRunning;
    FirebaseUser mUser;
    DatabaseReference mDatabaseref;
    private ProgressDialog mDialog;
    private ImageView mNav_imageView;
    private TextView mNav_userName;
    public static Boolean Connected;
    private TextView mUserEmail;
    private static final String TAG = "MainActivityResults";
    UserInput mUserInput;
    private String name, resultName;
    private ViewPager mViewpager;

    public interface UserInput {
        public void onSearchPressEnter(String input);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profilePic = findViewById(R.id.User_Image);
        userName = findViewById(R.id.User_name);
        Toolbar toolbar = findViewById(R.id.tooBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        setNavigationDrawer();
        mViewpager = findViewById(R.id.Pager);
        getCurrentUserEmail(mUserEmail);
        isInternetConnected();
        UserInterface(mViewpager);

    }


    private void displayNumOfUnreadMessagesInConnected(final ViewPager mViewpager) {
        mDatabaseref = FirebaseDatabase.getInstance().getReference("Chats");
        mDatabaseref.addValueEventListener(new ValueEventListener() {
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

        mDatabaseref = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        mDatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mDialog.dismiss();
                Users users = dataSnapshot.getValue(Users.class);
                userName.setText(users.getUsername());
                name = users.getUsername();
                mNav_userName.setText(users.getUsername());
                Log.d("users", dataSnapshot.getValue().toString());
                if (users.getImageUrl().equals("default")) {
                    profilePic.setImageResource(R.drawable.ic_action_name);
                } else {
                    Glide.with(getApplicationContext()).
                            load(users.getImageUrl())
                            .into(profilePic);
                    Glide.with(getApplicationContext()).
                            load(users.getImageUrl()).
                            into(mNav_imageView);
                    saveOfflineData();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Database error", databaseError.getMessage());
            }
        });

    }


    private void ProgressDialog() {
        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Uploading Users");
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
                    displayUserDataIfConnected();
                    displayNumOfUnreadMessagesInConnected(mViewpager);
                    Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();

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

            // if connected with internet
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
        getMenuInflater().inflate(R.menu.value, menu);
        MenuItem search = menu.findItem(R.id.usersSearch);
        SearchView mSearchView = (SearchView) search.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Log.d(TAG,"Search_input "+s);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),
                        Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }
        return false;
    }

    public void status(String status) {
        mDatabaseref = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        mDatabaseref.updateChildren(hashMap);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;
    }

    private String getMessages(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
        return text;
    }

    private void setNavigationDrawer() {
        final DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
        NavigationView nView = findViewById(R.id.navigationView);
        View header = nView.getHeaderView(0);
        mNav_imageView = header.findViewById(R.id.nav_userImage);
        mNav_userName = header.findViewById(R.id.nav_userName);
        mUserEmail = header.findViewById(R.id.nav_userEmail);
        nView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.nav_profile:
                        getMessages("Import your own action");
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.nav_setting:
                        getMessages("Import your own action for setting");
                        drawerLayout.closeDrawers();
                        menuItem.setChecked(true);
                        return true;

                    case R.id.nav_share:
                        menuItem.setChecked(true);
                        getMessages("Import your own Action for share");
                        drawerLayout.closeDrawers();
                        return true;

                }
                return false;
            }
        });

    }

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

    private void saveOfflineData() {
        SharedPreferences mPreference = getSharedPreferences("UserOfflineData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString("Username", name);
        editor.apply();


    }

    private void getSavedOOfflineData() {
        SharedPreferences mp = getSharedPreferences("UserOfflineData", Context.MODE_PRIVATE);
        resultName = mp.getString("Username", "");
        Log.d(TAG, "Shared Preference added successfully");
        Log.d(TAG, "                                     " + resultName);

    }

    public void UserInterface(ViewPager pager) {
        getSavedOOfflineData();

        displayUserDataIfNotConnected(pager);

    }

    private void displayUserDataIfNotConnected(ViewPager pager) {
        userName.setText(resultName);
    }
}
