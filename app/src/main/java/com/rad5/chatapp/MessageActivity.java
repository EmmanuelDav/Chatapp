package com.rad5.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rad5.chatapp.Models.Chats;
import com.rad5.chatapp.Models.Users;
import com.rad5.chatapp.Adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    ImageButton imageButton;
    EditText editText;
    CircleImageView mImageView;
    TextView mTextView;
    Intent intent;
    DatabaseReference myref;
    MessageAdapter chatAdapter;
    List<Chats> mChats;
    FirebaseUser fuser;
    RecyclerView mRecyclerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mTextView = findViewById(R.id.User_names);
        mImageView = findViewById(R.id.User_Images);
        imageButton = findViewById(R.id.btn_send);
        editText = findViewById(R.id.chat_text);
        Toolbar bar = findViewById(R.id.tooBarl);

        setSupportActionBar(bar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mRecyclerview = findViewById(R.id.chat_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(layoutManager);


        bar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        intent = getIntent();
        final String userId = intent.getStringExtra("user_id");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        myref = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                mTextView.setText(user.getUsername());
                if (!user.getImageUrl().equals("default")) {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(mImageView);
                } else {
                    mImageView.setImageResource(R.drawable.ic_action_name);
                }
                readMessages(fuser.getUid(), userId, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Display error", databaseError.getMessage());

            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sms = editText.getText().toString();
                if (sms.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "no message to send", Toast.LENGTH_LONG).show();
                } else {
                    sendmessage(fuser.getUid(), userId, sms);
                }
                editText.setText("");

            }
        });


    }

    public void sendmessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        reference.child("Chats").push().setValue(hashMap);

    }


    public void readMessages(final String Myid, final String MUserId, final String mImageUrl) {
        mChats = new ArrayList<>();
        myref = FirebaseDatabase.getInstance().getReference("Chats");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chats chats = snapshot.getValue(Chats.class);
                    Log.d("chats", snapshot.getValue().toString());
                    assert chats != null;
                    if (chats.getReceiver().equals(Myid) && chats.getSender().equals(MUserId)||
                            chats.getReceiver().equals(MUserId) && chats.getSender().equals(Myid)) {
                        mChats.add(chats);
                    }
                    chatAdapter = new MessageAdapter(mChats, MessageActivity.this, mImageUrl);
                    mRecyclerview.setAdapter(chatAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("chatdisplay", databaseError.getMessage());

            }
        });

    }
    public void status(String status){
        myref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("status",status);
        myref.updateChildren(hashMap);

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
}
