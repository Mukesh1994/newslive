package com.codegear.newslive.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.codegear.newslive.LoginActivity;

import java.util.HashMap;

public class PreferenceStorage {


    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    Context _context;

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_NAME = "name";

    public static final String KEY_USERNAME = "username";

    public static final String KEY_EMAIL = "email";

    public PreferenceStorage(Context context){
        this._context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }


    public void createLoginSession(String name,String username, String email){

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }


    public void checkLogin(){

        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);
        }

    }


    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        return user;
    }


    public void logoutUser(){
        editor.clear();
        editor.commit();


        Intent i = new Intent(_context, LoginActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        _context.startActivity(i);
    }


    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
