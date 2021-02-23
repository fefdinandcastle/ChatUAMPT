package com.uam.chatuam.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.maciejkozlowski.fragmentutils.FragmentUtils;
import com.uam.chatuam.R;
import com.uam.chatuam.Theme;
import com.uam.chatuam.Utils;
import com.uam.chatuam.interfaces.SettingsListener;

public class SettingsFragment extends Fragment {

    Switch themeSwitch;
    SettingsListener listener;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        themeSwitch =  root.findViewById(R.id.settings_switch);
        themeSwitch.setChecked(Utils.darkTheme);
        //themeSwitch.setChecked(Utils.darkTheme);
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                listener.onThemeChanged(isChecked);
            }
        });
        return root;
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