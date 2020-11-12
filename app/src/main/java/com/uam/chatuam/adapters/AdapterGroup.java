package com.uam.chatuam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uam.chatuam.R;
import com.uam.chatuam.model.ChatObject;

import java.util.ArrayList;

public class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.GroupViewHolder> {

    private ArrayList<ChatObject> chats;
    private Context context;
    private OnChatListener mOnGroupListener;

    public AdapterGroup(ArrayList<ChatObject> chats, OnChatListener onChatListener) {
        this.chats = chats;
        this.mOnGroupListener = onChatListener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group, viewGroup, false);
        context = viewGroup.getContext();
        return new GroupViewHolder(v,mOnGroupListener);
    }


    @Override
    public void onBindViewHolder(@NonNull final GroupViewHolder usuarioViewHolder, int i) {
        usuarioViewHolder.tvGroup.setText(chats.get(i).getNombreChat());
        final int index = i;
        /*usuarioViewHolder.tvGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Tocaste "+chats.get(index).getNombreChat(),Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvGroup;
        OnChatListener onChatListener;

        public GroupViewHolder(@NonNull View itemView, OnChatListener onChatListener) {
            super(itemView);
            tvGroup = itemView.findViewById(R.id.tv_group);
            this.onChatListener = onChatListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onChatListener.onChatClick(getAdapterPosition());
        }
    }

    public interface OnChatListener{
        void onChatClick(int position);
    }
}
