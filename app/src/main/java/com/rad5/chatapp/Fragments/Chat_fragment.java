package com.rad5.chatapp.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rad5.chatapp.Models.Users;
import com.rad5.chatapp.R;
import com.rad5.chatapp.Adapters.UserAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat_fragment extends Fragment {

    List<Users> MyUsers;
    FirebaseUser mfirebaseUser;
    DatabaseReference mDatabaseRefrence;
    UserAdapter mUserAdapter;
    public ProgressDialog pDialog;
    EditText search;

    public Chat_fragment() {
    }

    RecyclerView mRecyclerview;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_fragment, container, false);
        mRecyclerview = view.findViewById(R.id.RecyclerView);
        search = view.findViewById(R.id.user_search);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mDatabaseRefrence = FirebaseDatabase.getInstance().getReference("Users");
        mfirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        MyUsers = new ArrayList<>();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchInput(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Asyntaskview Asytask = new Asyntaskview(getActivity());
        Asytask.execute();
        return view;
    }

    private void searchInput(String s) {
        mfirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query mQuary = FirebaseDatabase.getInstance().getReference("Users").orderByChild("Username")
                .startAt(s).endAt(s + "\f8ff");
        mQuary.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot panshot : dataSnapshot.getChildren()) {
                    MyUsers.clear();
                    Users users = panshot.getValue(Users.class);
                    if (!mfirebaseUser.getUid().equals(users.getId())) {
                        MyUsers.add(users);
                    }
                }
                mUserAdapter = new UserAdapter(MyUsers, getContext(), false);
                mRecyclerview.setAdapter(mUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class Asyntaskview extends AsyncTask<String, String, String> {
        Context mContext;

        public Asyntaskview(Context context) {
            mContext = context;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }


        @Override
        protected String doInBackground(String... strings) {
            mDatabaseRefrence.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (search.getText().toString().equals("")) {
                        MyUsers.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Users users = snapshot.getValue(Users.class);
                            assert users != null;
                            if (!users.getId().equals(mfirebaseUser.getUid())) {
                                MyUsers.add(users);
                            }
                        }
                        mUserAdapter = new UserAdapter(MyUsers, getContext(), true);
                        mRecyclerview.setAdapter(mUserAdapter);
                        mUserAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("displayerror", databaseError.getMessage());

                }

            });
            return null;
        }


    }


}
