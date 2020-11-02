package com.uam.chatuam;

import android.content.Context;
import android.content.SharedPreferences;

import com.uam.chatuam.model.Usuario;

public class Utils {
    public static Usuario usuario;
    public static Boolean darkTheme;

    public static void guardarTema(Context context, boolean isChecked){
        SharedPreferences settings = context.getSharedPreferences("theme", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("switchkey", isChecked);
        darkTheme = isChecked;
        editor.commit();
    }

    public static Boolean cargarTema(Context context){
        SharedPreferences settings = context.getSharedPreferences("theme", 0);
        boolean silent = settings.getBoolean("switchkey", false);
        return silent;
    }

    public static void configurarTema(Context context){
        SharedPreferences themePreferences = context.getSharedPreferences("theme", 0);
        if (themePreferences.contains("switchkey")) { //How can I ask here?
            darkTheme = cargarTema(context);
        } else {
            guardarTema(context,false);
            darkTheme = false;
        }
    }
}
