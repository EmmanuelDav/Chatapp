package com.rad5.chatapp.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rad5.chatapp.Adapters.UserAdapter;
import com.rad5.chatapp.MainActivity;
import com.rad5.chatapp.Models.Chatlist;
import com.rad5.chatapp.Models.Users;
import com.rad5.chatapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import static android.app.SearchManager.QUERY;
import static com.rad5.chatapp.MainActivity.Connected;
import static com.rad5.chatapp.SettingsFragment.Key_Pref_Example_Switch;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_Chat extends Fragment implements MainActivity.UserInput {
    private RecyclerView mRecyclerView;
    private List<Users> mUsers;
    FirebaseUser mFirebaseUser;
    private List<Chatlist> mList;
    private static final String TAG = "fragmentChatActivity";
    public static boolean fragmentChatActivity = false;
    private DatabaseReference mReference;
    private UserAdapter mUserAdapter;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case QUERY:
                    Log.d(TAG, "debugger" + intent.getStringExtra("UsersInput"));
                    // filterSearch(intent.getStringExtra("UserInput"));
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(QUERY);
        // getContext().registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_fragment, container, false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(QUERY);
        getContext().registerReceiver(mBroadcastReceiver, filter);
        mRecyclerView = v.findViewById(R.id.RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mList = new ArrayList<>();
        mReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(mFirebaseUser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chatlist mchatlist = snapshot.getValue(Chatlist.class);
                    mList.add(mchatlist);
                }
                chatlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        sendTokenToFirebase();
        return v;
    }
    private void sendTokenToFirebase() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference refrence = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("UserToken", token);
                    refrence.updateChildren(hashMap);
                } else {
                    Log.d(TAG, "tokenError" + task.getException());
                }
            }
        });
    }

    private void chatlist() {
        mUsers = new ArrayList<>();
        mReference = FirebaseDatabase.getInstance().getReference("Users");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users users = snapshot.getValue(Users.class);
                    for (Chatlist chatlist : mList) {
                        if (users.getId().equals(chatlist.getId())) {
                            mUsers.add(users);
                        }
                    }
                }
                mUserAdapter = new UserAdapter(mUsers, getContext(), true);
                mRecyclerView.setAdapter(mUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void filterSearch(String SearchInput) {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("Username")
                .startAt(SearchInput)
                .endAt(SearchInput + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mUsers.clear();
                    Users user = snapshot.getValue(Users.class);
                    if (!mFirebaseUser.getUid().equals(user.getId())) {
                        mUsers.add(user);
                    }
                    mUserAdapter = new UserAdapter(mUsers, getContext(), false);
                    mRecyclerView.setAdapter(mUserAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSearchPressEnter(String input) {
        Log.d(TAG, "    " + input);
        filterSearch(input);
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentChatActivity = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentChatActivity = true;
    }
}
