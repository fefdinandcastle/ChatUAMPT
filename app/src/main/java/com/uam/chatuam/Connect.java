package com.uam.chatuam;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connect {
    private static Connection connection;
    private static String usuario = "dbmasteruser";
    private static String password = "chatuam20";
    private static String host = "ls-36108e309ce802399938b4e5d624eba78274c264.cans7cvrt2zk.us-east-1.rds.amazonaws.com:3306";
    private static String database = "uam";
    private static String url="";

    @SuppressLint("NewApi")
    public Connection getConnection() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;

        url="jdbc:mysql://"+host+"/"+database+"?user="+usuario+"&password="+password+"&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT";


        try {
            Class.forName("com.mysql.jdbc.Driver");

            url="jdbc:mysql://"+host+"/"+database+"?user="+usuario+"&password="+password+"&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT";
            conn = DriverManager.getConnection(url);

        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}
