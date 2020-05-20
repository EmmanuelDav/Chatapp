package com.rad5.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rad5.chatapp.MessageActivity;
import com.rad5.chatapp.Models.Chats;
import com.rad5.chatapp.Models.Users;
import com.rad5.chatapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {

    List<Users> mUsers;
    Context mContext;
    private Boolean ischat;
    String theLastmessages;
    public static final String TAG = "lastchat";

    public UserAdapter(List<Users> users, Context context, Boolean ischat) {
        mUsers = users;
        mContext = context;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public UserAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.Viewholder holder, int position) {
        final Users users = mUsers.get(position);
        holder.textView.setText(users.getUsername());
        if (users.getImageUrl().equals("default")) {
            holder.imageView.setImageResource(R.drawable.ic_action_name);
        } else {
            Glide.with(mContext).load(users.getImageUrl()).into(holder.imageView);
        }

        if (ischat){
            lastMessage(users.getId(),holder.lastMessage);
        }else{
            holder.lastMessage.setVisibility(View.GONE);
        }

        if (ischat) {
            if (users.getStatus().equals("online")) {
                holder.img_off.setVisibility(View.GONE);
                holder.img_on.setVisibility(View.VISIBLE);
            } else {
                holder.img_off.setVisibility(View.VISIBLE);
                holder.img_on.setVisibility(View.GONE);
            }
        } else {
            holder.img_off.setVisibility(View.GONE);
            holder.img_on.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("user_id", users.getId());
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        public TextView textView;
        public CircleImageView imageView;
        ImageView img_off, img_on;
        TextView lastMessage;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.profile_name);
            imageView = itemView.findViewById(R.id.profile_pix);

            img_off = itemView.findViewById(R.id.img_off);
            img_on = itemView.findViewById(R.id.img_on);
            lastMessage = itemView.findViewById(R.id.lastMessage);

        }
    }

    public void lastMessage(final String UserId, final TextView LAstMessages) {
        theLastmessages = "default";
        final FirebaseUser mfirebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mdataRef = FirebaseDatabase.getInstance().getReference("Chats");
        mdataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats mchat = snapshot.getValue(Chats.class);
                    if (mchat.getReceiver().equals(mfirebaseuser.getUid()) && mchat.getSender().equals(UserId) ||
                            mchat.getReceiver().equals(UserId) && mchat.getSender().equals(mfirebaseuser.getUid())) {

                        theLastmessages = mchat.getMessage();
                        //Log.d(TAG,"lastmessage = " + mchat.getMessage());
                    }
                }
                switch (theLastmessages) {
                    case "default":
                        LAstMessages.setText(" ");
                        break;

                    default:
                        LAstMessages.setText(theLastmessages);
                        break;
                }

                theLastmessages = "default";


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
