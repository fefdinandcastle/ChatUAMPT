package com.uam.chatuam;

import android.app.Activity;
import android.content.Intent;

public class Theme {
    private static int mTema;
    public final static int Cuajimalpa_Light_NoActionBar = 0;
    public final static int Cuajimalpa_Light_ActionBar = 1;
    public final static int Cuajimalpa_Dark_NoActionBar = 2;
    public final static int Cuajimalpa_Dark_ActionBar = 3;

    public static void cambiarATema(Activity activity, int theme) {
        mTema = theme;
        Intent intent = activity.getIntent();
        activity.finish();
        //activity.startActivity(new Intent(activity, activity.getClass()));
        activity.startActivity(intent);
        cambiarTema(activity);

    }

    public static void cambiarTema(Activity activity) {
        switch (mTema) {
            case Cuajimalpa_Light_NoActionBar:
                activity.setTheme(R.style.CuajimalpaLightNoActionBar);
                break;
            case Cuajimalpa_Light_ActionBar:
                activity.setTheme(R.style.CuajimalpaLightActionBar);
                break;
            case Cuajimalpa_Dark_NoActionBar:
                activity.setTheme(R.style.CuajimalpaDarkNoActionBar);
                break;
            case Cuajimalpa_Dark_ActionBar:
                activity.setTheme(R.style.CuajimalpaDarkActionBar);
                break;
            default:
        }
    }
}
