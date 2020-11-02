package com.uam.chatuam.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uam.chatuam.R;
import com.uam.chatuam.Utils;
import com.uam.chatuam.adapters.AdapterHub;

public class HomeFragment extends Fragment implements AdapterHub.OnGroupListener {

    private RecyclerView rvUEAS;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLmanager;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        rvUEAS = root.findViewById(R.id.rvUEAS);

        rvUEAS.setHasFixedSize(true);

        mLmanager = new GridLayoutManager(getContext(),2);
        rvUEAS.setLayoutManager(mLmanager);

        mAdapter = new AdapterHub(Utils.usuario.getUeas(),this);
        rvUEAS.setAdapter(mAdapter);

        FloatingActionButton floatingActionButton = root.findViewById(R.id.fab_addUEA);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //agregarUEA();
            }
        });

        return root;
    }

    /*void agregarUEA(){
        ArrayList<Chat> chats = new ArrayList<Chat>();
        chats.add(new Chat("Grupo",new ArrayList<Mensaje>()));
        chats.add(new Chat("Profesor",new ArrayList<Mensaje>()));
        UEA uea = new UEA(getRandomString(),chats);
        (Utils.usuario.getUeas()).add(uea);
        rvUEAS.setAdapter(mAdapter);
        //Toast.makeText(getContext(),"Se ha agregado",Toast.LENGTH_SHORT).show();
    }*/

    static String getRandomString(){
        int r = (int) (Math.random()*5);
        String name = new String [] {"Cálculo II","Sistemas Operativos","Arquitectura de Computadoras",
                                     "Taller de Literacidad","Biologia","Bases de datos","Proyecto de Ingenieria de Software II",
                                     "Filosofia"}[r];
        return name;
    }

    @Override
    public void onGroupClick(int position) {
        //Intent intent = new Intent(getActivity(), GroupActivity.class);
        //intent.putExtra("index",position);
        //startActivity(intent);

        ((OnUEASelectedListener)getActivity()).onUEAItemPicked(position);
    }

    public interface OnUEASelectedListener{
        void onUEAItemPicked(int position);
    }




}