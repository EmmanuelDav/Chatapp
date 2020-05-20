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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rad5.chatapp.Adapters.UserAdapter;
import com.rad5.chatapp.FCM.Data;
import com.rad5.chatapp.FCM.FCM;
import com.rad5.chatapp.FCM.FirebaseMessage;
import com.rad5.chatapp.Models.Chats;
import com.rad5.chatapp.Models.Users;
import com.rad5.chatapp.Adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;

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
    public static Boolean isActivityRunning;
    RecyclerView mRecyclerview;
    ValueEventListener isSeenListener;
    private String mUserId;
    public Set<String> mToken;
    public String mServerkey;
    public static final String TAG = "MessageActivity";
    public static final String baseUrl = "https://fcm.googleapis.com/fcm/";
    private String mLastmessages;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_message);
        mTextView = findViewById(R.id.User_names);
        mImageView = findViewById(R.id.User_Images);
        imageButton = findViewById(R.id.btn_send);
        editText = findViewById(R.id.chat_text);
        Toolbar bar = findViewById(R.id.tooBarl);
        mToken = new HashSet<>();


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
                startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        intent = getIntent();
        mUserId = intent.getStringExtra("user_id");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        myref = FirebaseDatabase.getInstance().getReference("Users").child(mUserId);
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
                readMessages(fuser.getUid(), mUserId, user.getImageUrl());
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
                if (sms.isEmpty() || sms.equals(" ")) {
                    Toast.makeText(getApplicationContext(), "no message to send", Toast.LENGTH_LONG).show();
                } else {
                    sendmessage(fuser.getUid(), mUserId, sms);
                    sendNotifictionmessagetoUser(sms, username);


                }
                editText.setText("");

            }
        });
        getServerKey();
        getToken();

        getUsernmae();
        MessageSeen(mUserId);

    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityRunning = true;
        getUsernmae();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityRunning = false;
    }

    public void sendNotifictionmessagetoUser(String mms, String id) {
        Log.d(TAG, "creating retrofit for notification_message");
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(loggingInterceptor);

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        FCM fcmApi = mRetrofit.create(FCM.class);
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Authorization", "key=" + mServerkey);

        for (String token : mToken) {
            Data data = new Data();
            data.setMessage(mms);
            data.setuserID(id);
            FirebaseMessage firebaseMessage = new FirebaseMessage();
            firebaseMessage.setData(data);
            firebaseMessage.setTo(token);
            Log.d(TAG, "D/F  " + firebaseMessage.getData().toString());
            Log.d(TAG, "D/F  " + data.getMessage());
            Call<ResponseBody> call = fcmApi.send(header, firebaseMessage);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d(TAG, "D/F response_Successful   " + response.message());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, "response Failure " + t.toString());
                    Toast.makeText(MessageActivity.this, "error", Toast.LENGTH_SHORT).show();

                }
            });

        }


    }

    public void getUsernmae() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                username = users.getUsername();
                Log.d(TAG, "userName " + username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getServerKey() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("ServerKey")
                .orderByValue();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                mServerkey = snapshot.getValue().toString();

                Log.d(TAG, "mServerKey   =" + mServerkey);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getToken() {
        mToken.clear();
        DatabaseReference refrence = FirebaseDatabase.getInstance().getReference("Users").child(mUserId).child("UserToken");
        refrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String token = dataSnapshot.getValue(String.class);
                mToken.add(token);
                Log.d(TAG, "mTokenKey =" + mToken);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void MessageSeen(final String UserId) {
        myref = FirebaseDatabase.getInstance().getReference("Chats");
        isSeenListener = myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);
                    if (chats.getReceiver().equals(fuser.getUid()) && chats.getSender().equals(UserId)) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isSeen", true);
                        snapshot.getRef().updateChildren(map);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendmessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isSeen", false);
        reference.child("Chats").push().setValue(hashMap);
        final DatabaseReference mchatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(mUserId);

        mchatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mchatRef.child("id").setValue(mUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void readMessages(final String Myid, final String MUserId, final String mImageUrl) {
        mChats = new ArrayList<>();
        mChats.clear();
        myref = FirebaseDatabase.getInstance().getReference("Chats");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    if (chats.getReceiver().equals(Myid) && chats.getSender().equals(MUserId) ||
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

    public void status(String status) {
        myref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        myref.updateChildren(hashMap);

    }

    @Override
    protected void onPause() {
        super.onPause();
        myref.removeEventListener(isSeenListener);
        status("offline");


    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

}
