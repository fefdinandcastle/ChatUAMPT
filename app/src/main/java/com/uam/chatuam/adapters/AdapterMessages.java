package com.uam.chatuam.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uam.chatuam.R;
import com.uam.chatuam.Utils;
import com.uam.chatuam.model.Mensaje;

import java.util.ArrayList;

public class AdapterMessages extends RecyclerView.Adapter {

    private ArrayList<Mensaje> mensajes;
    private Context context;
    private final int Yo = 0, Otro = 1;

    public AdapterMessages(ArrayList<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case Yo:
                MessageMeViewHolder vh1 = (MessageMeViewHolder) holder;
                configureViewHolder1(vh1, position);
                break;
            case Otro:
                MessageOtherViewHolder vh2 = (MessageOtherViewHolder) holder;
                configureViewHolder2(vh2, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    private void configureViewHolder1(MessageMeViewHolder vh1, int position) {
        vh1.tvMessage.setText((mensajes.get(position)).getMensaje());
        vh1.tvHour.setText((mensajes.get(position)).getTiempo());
    }

    private void configureViewHolder2(MessageOtherViewHolder vh2,int position) {
        vh2.tvMessage.setText((mensajes.get(position)).getMensaje());
        vh2.tvHour.setText((mensajes.get(position)).getTiempo());
    }


    @Override
    public int getItemViewType(int position) {
        String messageUser = (mensajes.get(position)).getUsuario();
        if(messageUser.equals(Utils.usuario.getMatricula())) {
            return Yo;
        }else {
            return Otro;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view;
        switch (viewType) {
            case Yo:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_me, parent, false);
                return new MessageMeViewHolder(view);
            case Otro:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_other, parent, false);
                return new MessageOtherViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_me, parent, false);
                return new MessageMeViewHolder(view);
        }
    }

    public class MessageMeViewHolder extends RecyclerView.ViewHolder  {

        TextView tvMessage;
        TextView tvHour;

        public MessageMeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvHour = itemView.findViewById(R.id.tv_hour);
        }
    }

    public class MessageOtherViewHolder extends RecyclerView.ViewHolder  {

        TextView tvMessage;
        TextView tvHour;

        public MessageOtherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvHour = itemView.findViewById(R.id.tv_hour);
        }
    }


}
