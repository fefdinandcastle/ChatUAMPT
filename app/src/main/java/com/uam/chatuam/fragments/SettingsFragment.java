package com.uam.chatuam.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.maciejkozlowski.fragmentutils.FragmentUtils;
import com.uam.chatuam.R;
import com.uam.chatuam.Theme;
import com.uam.chatuam.Utils;
import com.uam.chatuam.interfaces.SettingsListener;

public class SettingsFragment extends Fragment {

    Switch themeSwitch;
    SettingsListener listener;
    CardView btn_logout;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);
        initializeViews();
        themeSwitch.setChecked(Utils.darkTheme);
        //themeSwitch.setChecked(Utils.darkTheme);
        initializeListeners();

        return root;
    }

    void initializeListeners(){
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                listener.onThemeChanged(isChecked);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.logoutRequested();
            }
        });
    }

    void initializeViews(){
        themeSwitch =  root.findViewById(R.id.settings_switch);
        btn_logout = (CardView) root.findViewById(R.id.settings_logout);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = FragmentUtils.getListener(this, SettingsListener.class);
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        replaceFragmentListener = FragmentUtils.getListener(this, ReplaceFragmentListener.class);
    }*/

}