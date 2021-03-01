package com.uam.chatuam;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.uam.chatuam.model.Usuario;

import java.util.ArrayList;

public class Utils {
    public static Usuario usuario;
    public static Boolean darkTheme;
    public static ArrayList<Usuario> usuarios;

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

    public static String getUserName(String matricula){
        String s="";
        for(int i = 0;i<usuarios.size();i++){
            if(usuarios.get(i).getMatricula().equals(matricula)){
                s = usuarios.get(i).getNombre();
                break;
            }
        }
        return s;
    }


    public static void mostrarNotificacion(Context context,String chatID,String mensaje){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("Nuevos mensajes en "+chatID)
                .setContentText(mensaje);

        Intent intent= new  Intent(context,context.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(1, notificationBuilder.build());
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

}
