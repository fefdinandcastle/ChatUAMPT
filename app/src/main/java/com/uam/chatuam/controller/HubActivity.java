package com.uam.chatuam.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uam.chatuam.R;
import com.uam.chatuam.Theme;
import com.uam.chatuam.Utils;
import com.uam.chatuam.fragments.HomeFragment;
import com.uam.chatuam.interfaces.SettingsListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HubActivity extends AppCompatActivity implements HomeFragment.OnUEASelectedListener, SettingsListener {

    TextView welcomeMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!Utils.darkTheme) setTheme(R.style.CuajimalpaLightNoActionBar);
        else setTheme(R.style.CuajimalpaDarkNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_hub, R.id.navigation_dashboard).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        initializeViews();
        //Log.d("UEAEnChats",Utils.usuario.getUeas().get(0).getChats().size()+"");
    }

    public void initializeViews(){
        welcomeMsg = (TextView) findViewById(R.id.txt_welcome_msg);
        if(Utils.usuario.getTipo()=="AL"){
            welcomeMsg.setText(getString(R.string.welcome_msg)+": "+Utils.usuario.getNombre()+"\n"+getString(R.string.user_id_al)+": "+Utils.usuario.getMatricula());
        }else{
            welcomeMsg.setText(getString(R.string.welcome_msg)+": "+Utils.usuario.getNombre()+"\n"+getString(R.string.user_id_pr)+": "+Utils.usuario.getMatricula());
        }

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

    @Override
    public void onThemeChanged(boolean b) {
        SharedPreferences sharedPrefs = getSharedPreferences("chatuam", MODE_PRIVATE);
        SharedPreferences.Editor ed;
        ed = sharedPrefs.edit();
        ed.putBoolean("theme",b);
        ed.commit();
        Utils.darkTheme=sharedPrefs.getBoolean("theme",false);
        this.recreate();
    }

    @Override
    public void logoutRequested() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName() );
        intent .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}