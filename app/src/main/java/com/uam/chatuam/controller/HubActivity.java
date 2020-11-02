package com.uam.chatuam.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uam.chatuam.R;
import com.uam.chatuam.Theme;
import com.uam.chatuam.Utils;
import com.uam.chatuam.fragments.HomeFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HubActivity extends AppCompatActivity implements HomeFragment.OnUEASelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);
        //if(Utils.darkTheme=false)
        //    Theme.cambiarATema(this,Theme.Cuajimalpa_Light_NoActionBar);
        //else if(Utils.darkTheme=true){
        //    Theme.cambiarATema(this,Theme.Cuajimalpa_Dark_NoActionBar);
        //}
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_hub, R.id.navigation_dashboard).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        //Log.d("UEAEnChats",Utils.usuario.getUeas().get(0).getChats().size()+"");
    }

    public class UEASelectAsync extends AsyncTask<Void,Void,Void> {
        LoadingDialog dialog;
        Activity activity;
        int position;

        public UEASelectAsync(Activity activity,int position) {
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
            //startActivity(new Intent(HubActivity.this, GroupActivity.class));
            Intent intent = new Intent(HubActivity.this, GroupActivity.class);
            intent.putExtra("index",position);
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

    @Override
    public void onUEAItemPicked(int position) {
        HubActivity.UEASelectAsync UEAselectasync = new HubActivity.UEASelectAsync(this,position);
        UEAselectasync.execute();
    }

}