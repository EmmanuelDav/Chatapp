package com.rad5.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {
    CircleImageView profilePic;
    TextView userName;
    public static Boolean isActivityRunning;
    FirebaseUser mUser;
    DatabaseReference mDatabaseref;
    private ProgressDialog mDialog;
    private ImageView mNav_imageView;
    private TextView mNav_userName;
    private TextView mUserEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressDialog();
        profilePic = findViewById(R.id.User_Image);
        userName = findViewById(R.id.User_name);
        Toolbar toolbarv = findViewById(R.id.tooBar);

        setSupportActionBar(toolbarv);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabaseref = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        mDatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mDialog.dismiss();
                Users users = dataSnapshot.getValue(Users.class);
                userName.setText(users.getUsername());
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Database error", databaseError.getMessage());
            }
        });
        setNavigationDrawer();

        final ViewPager mViewpager = findViewById(R.id.Pager);

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
        getCurrentUserEmail(mUserEmail);
    }


    private void ProgressDialog() {
        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Uploading Users");
        mDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.value, menu);
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
        View  header = nView.getHeaderView(0);
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

    private void getCurrentUserEmail(TextView email){
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = mUser.getEmail();
        if (mUser ==null){
            email.setText(userEmail);
        }


    }
}
