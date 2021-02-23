package com.uam.chatuam.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.UiAutomation;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.uam.chatuam.R;
import com.uam.chatuam.Utils;
import com.uam.chatuam.adapters.AdapterGroup;
import com.uam.chatuam.adapters.AdapterHub;

public class GroupActivity extends AppCompatActivity implements AdapterGroup.OnChatListener {

    int ueaIndex;
    private RecyclerView rvChats;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!Utils.darkTheme) setTheme(R.style.CuajimalpaLightActionBar);
        else setTheme(R.style.CuajimalpaDarkActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_unidad_cuajimalpa)));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        ueaIndex=intent.getIntExtra("index",0);
        setTitle((Utils.usuario.getUeas()).get(ueaIndex).getNombre());

        rvChats = findViewById(R.id.rvGroups);
        rvChats.setHasFixedSize(true);
        mLmanager = new LinearLayoutManager(this);
        rvChats.setLayoutManager(mLmanager);
        mAdapter = new AdapterGroup(((Utils.usuario.getUeas()).get(ueaIndex)).getChats(),this);
        rvChats.setAdapter(mAdapter);
        Log.d("HOLA",Utils.usuario.getUeas().get(ueaIndex).getChats().size()+"");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChatClick(int position) {
        //Intent intent = new Intent(this, ChatActivity.class);
        //intent.putExtra("ueaIndex",ueaIndex);
        //intent.putExtra("chatIndex",position);
        //startActivity(intent);
        GroupActivity.GroupSelectAsync groupselectasync = new GroupActivity.GroupSelectAsync(this,position);
        groupselectasync.execute();
    }

    public class GroupSelectAsync extends AsyncTask<Void,Void,Void> {
        LoadingDialog dialog;
        Activity activity;
        int position;

        public GroupSelectAsync(Activity activity,int position) {
            this.activity = activity;
            this.position = position;
            dialog = new LoadingDialog(this.activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.startLoadingDialog();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismissDialog();
            Intent intent = new Intent(GroupActivity.this, ChatActivity.class);
            intent.putExtra("ueaIndex",ueaIndex);
            intent.putExtra("chatIndex",position);
            startActivity(intent);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Thread closeActivity = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        //hacer
                    } catch (Exception e) {
                        e.getLocalizedMessage();
                    }
                }
            });
            closeActivity.run();
            return null;
        }
    }

}