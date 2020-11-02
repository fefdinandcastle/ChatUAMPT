package com.uam.chatuam.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.uam.chatuam.R;
import com.uam.chatuam.Theme;
import com.uam.chatuam.Utils;

public class SettingsFragment extends Fragment {

    Switch themeSwitch;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        themeSwitch =  root.findViewById(R.id.settings_switch);
        //themeSwitch.setChecked(Utils.darkTheme);
        //themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        //    @Override
        //    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        //        if (compoundButton.isChecked()){
        //            Theme.cambiarATema(getActivity(),Theme.Cuajimalpa_Dark_NoActionBar);
        //           Utils.guardarTema(getContext(),true);
        //        }else{
        //            Theme.cambiarATema(getActivity(),Theme.Cuajimalpa_Light_NoActionBar);
        //            Utils.guardarTema(getContext(),false);
        //        }
        //    }
        //});
        return root;
    }
}