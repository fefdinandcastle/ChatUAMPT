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
import com.uam.chatuam.model.UEA;

import java.util.ArrayList;

public class AdapterHub extends RecyclerView.Adapter<AdapterHub.UsuarioViewHolder> {

    private ArrayList<UEA> ueas;
    private Context context;
    private OnGroupListener mOnGroupListener;

    public AdapterHub(ArrayList<UEA> ueas, OnGroupListener onGroupListener) {
        this.ueas = ueas;
        this.mOnGroupListener = onGroupListener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_hub, viewGroup, false);
        context = viewGroup.getContext();
        return new UsuarioViewHolder(v,mOnGroupListener);
    }


    @Override
    public void onBindViewHolder(@NonNull final UsuarioViewHolder usuarioViewHolder, int i) {
        usuarioViewHolder.tvUEA.setText(ueas.get(i).getNombre());
        final int index = i;
        usuarioViewHolder.tvUEA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Tocaste "+ueas.get(index).getNombre(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ueas.size();
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvUEA;
        OnGroupListener onGroupListener;

        public UsuarioViewHolder(@NonNull View itemView, OnGroupListener onGroupListener) {
            super(itemView);
            tvUEA = itemView.findViewById(R.id.tv_hub);
            this.onGroupListener = onGroupListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onGroupListener.onGroupClick(getAdapterPosition());
        }
    }

    public interface OnGroupListener{
        void onGroupClick(int position);
    }
}
