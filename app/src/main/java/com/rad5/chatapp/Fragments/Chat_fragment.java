package com.rad5.chatapp.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    public Chat_fragment() {
    }

    RecyclerView mRecyclerview;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_fragment, container, false);
        mRecyclerview = view.findViewById(R.id.RecyclerView);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mDatabaseRefrence = FirebaseDatabase.getInstance().getReference("Users");
        mfirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        MyUsers = new ArrayList<>();
        Asyntaskview Asytask = new Asyntaskview(getActivity());
        Asytask.execute();
        return view;
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
                    MyUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Users users = snapshot.getValue(Users.class);
                        assert users != null;
                        if (!users.getId().equals(mfirebaseUser.getUid())) {
                            MyUsers.add(users);
                            Log.d("LogUsers", users.toString());

                        }
                    }
                    mUserAdapter = new UserAdapter(MyUsers, getContext(),true);
                    mRecyclerview.setAdapter(mUserAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("displayerror", databaseError.getMessage());

                }

            });
            return null;
        }


    }

//  @Override
//    public void onStart() {
//        super.onStart();
//        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserAdapter>(firebaseRecyclerOptions) {
//            @Override
//            protected void onBindViewHolder(@NonNull UserAdapter holder, int position, @NonNull final Users model) {
//                holder.textView.setText(model.getUsername());
//                if (model.getImageUrl().equals("default")) {
//                    holder.imageView.setImageResource(R.drawable.ic_action_name);
//                } else {
//                    Glide.with(getContext()).load(model.getImageUrl()).into(holder.imageView);
//                }
//                mDatabaseRefrence.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.d("displayUser",dataSnapshot.getValue().toString());
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(getContext(), MessageActivity.class);
//                        intent.putExtra("Userid", model.getId());
//                        getContext().startActivity(intent);
//                    }
//                });
//
//            }
//
//            @NonNull
//            @Override
//            public UserAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
//                return new UserAdapter(view);
//            }
//        };
//        mRecyclerview.setAdapter(firebaseRecyclerAdapter);
//        firebaseRecyclerAdapter.startListening();
//
//    }


}
