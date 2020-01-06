package com.rad5.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rad5.chatapp.MessageActivity;
import com.rad5.chatapp.Models.Users;
import com.rad5.chatapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Viewholder> {

    List<Users> mUsers;
    Context mContext;

    public UserAdapter(List<Users> users, Context context) {
        mUsers = users;
        mContext = context;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("user_id",users.getId());
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

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.profile_name);
            imageView = itemView.findViewById(R.id.profile_pix);
        }
    }
}
