package com.example.mqtt_module.mqtt_conn;

/**
 * Created by Personal on 9/25/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.HashMap;

public class
SessionManager {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    public static final String PREF_NAME = "SmartHomePref";
    public static final String CA_CRT = "ca_crt";
    public static final String CLIENT_CRT = "client_crt";
    public static final String CLIENT_KEY = "client_key";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void storeCertificate(String ca_crt, String client_crt, String client_key) {

        editor.putString(CA_CRT, ca_crt);
        editor.putString(CLIENT_CRT, client_crt);
        editor.putString(CLIENT_KEY, client_key);
        editor.commit();

    }

    public HashMap<String, String> getCertificate() {
        HashMap<String, String> certificate = new HashMap<String, String>();
        // user name
        certificate.put(CA_CRT, pref.getString(CA_CRT, null));
        certificate.put(CLIENT_CRT, pref.getString(CLIENT_CRT, null));
        certificate.put(CLIENT_KEY, pref.getString(CLIENT_KEY, null));
        // return user
        return certificate;
    }

}
