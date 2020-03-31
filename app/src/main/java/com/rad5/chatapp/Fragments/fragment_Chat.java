package com.rad5.chatapp.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rad5.chatapp.Adapters.UserAdapter;
import com.rad5.chatapp.Models.Chatlist;
import com.rad5.chatapp.Models.Users;
import com.rad5.chatapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_Chat extends Fragment {

    private RecyclerView mRecyclerView;

    private List<Users> mUsers;
    FirebaseUser mFirebaseUser;
    private List<Chatlist> mList;
    private static final String TAG = "fragmentActivity";
    private DatabaseReference mReference;
    private UserAdapter mUserAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_fragment, container, false);

        mRecyclerView = v.findViewById(R.id.recyclerview);
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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist mchatlist =  snapshot.getValue(Chatlist.class);
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
                }else {
                    Log.d(TAG,"tokenError" + task.getException());
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
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users users = snapshot.getValue(Users.class);
                    for (Chatlist chatlist : mList){
                        if (users.getId().equals(chatlist.getId())){
                            mUsers.add(users);

                        }
                    }

                }
                mUserAdapter = new UserAdapter(mUsers,getContext(),true);
                mRecyclerView.setAdapter(mUserAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
