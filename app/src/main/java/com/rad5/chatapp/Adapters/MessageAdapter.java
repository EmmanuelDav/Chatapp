package com.rad5.chatapp.Adapters;

import android.content.Context;
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
import com.rad5.chatapp.Models.Chats;
import com.rad5.chatapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSM_TYPE_LEFT = 0;
    public static final int MSM_TYPE_RIGHT = 1;

    List<Chats> chats;
    Context context;
    String imageurl;


    public MessageAdapter(List<Chats> chats, Context context, String imageurl) {
        this.chats = chats;
        this.context = context;
        this.imageurl = imageurl;
    }

    FirebaseUser fuser;

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSM_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chats mchat = chats.get(position);
        holder.show_message.setText(mchat.getMessage());
       // Log.d("usermessage",mchat.getMessage());
        if (imageurl.equals("default")){
            holder.prof_pix.setImageResource(R.drawable.profile_white);
        }else {
            Glide.with(context).load(imageurl).into(holder.prof_pix);
        }

       if (position == chats.size() -1){
           if (mchat.isisSeen()){
               holder.txt_seen.setText("seen");
           }else {
               holder.txt_seen.setText("delivered");
           }
       }else {
           holder.txt_seen.setVisibility(View.GONE);
       }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView prof_pix;
        public TextView show_message,txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            prof_pix = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.SeenMessage);
        }
    }
    @Override
    public int getItemViewType(int position) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(fuser.getUid())){
            return MSM_TYPE_RIGHT;
        }else {
            return MSM_TYPE_LEFT;
        }
    }
}





