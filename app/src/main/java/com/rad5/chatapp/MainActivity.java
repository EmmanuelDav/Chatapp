package com.rad5.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rad5.chatapp.Adapters.FragmentaAdapter;
import com.rad5.chatapp.Fragments.Chat_fragment;
import com.rad5.chatapp.Fragments.Users_fragment;
import com.rad5.chatapp.Fragments.profileFragment;
import com.rad5.chatapp.Models.Users;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView profilePic;
    TextView userName;
    FirebaseUser mUser;
    DatabaseReference mDatabaseref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profilePic =  findViewById(R.id.User_Image);
        userName = findViewById(R.id.User_name);
        Toolbar toolbarv  = findViewById(R.id.tooBar);
        setSupportActionBar(toolbarv);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseref = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        mDatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                userName.setText(users.getUsername());
                Log.d("users",dataSnapshot.getValue().toString());
                if (users.getImageUrl().equals("default")){
                    profilePic.setImageResource(R.drawable.ic_action_name);
                }else{
                    Glide.with(getApplicationContext()).
                            load(users.getImageUrl())
                            .into(profilePic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Database error",databaseError.getMessage());
            }
        });
        ViewPager mViewpager = findViewById(R.id.Pager);
        TabLayout layout = findViewById(R.id.tablayout);
        addViewPager(mViewpager);
        layout.setupWithViewPager(mViewpager);
    }

    private void addViewPager(ViewPager Pager) {
        FragmentaAdapter pagerAdapter = new FragmentaAdapter(getSupportFragmentManager());
        pagerAdapter.addFragments(new Chat_fragment(),"Users");
        pagerAdapter.addFragments(new Users_fragment(),"Chats");
        pagerAdapter.addFragments(new profileFragment(),"Profile");
        Pager.setAdapter(pagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.value,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),welcome.class));
                finish();
        }
        return false;
    }
}
