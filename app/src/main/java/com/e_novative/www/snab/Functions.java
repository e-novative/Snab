package com.e_novative.www.snab;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * * * * * * * * * * * * * * * * * *
 * Snap
 * Functions and Routines
 * * * * * * * * * * * * * * * * * *
 * Revision History:
 *  22 February, 2016       Authored
 *
 *  * * * * * * * * * * * * * * * * *
 *  Lists of Functions
 *      GetIMEI                     - Get Mobile Phone IMEI
 *      GetAndroidID()              - Get this Android Device Id
 *      GetMobileNumber()           - Get this Mobile telephone number
 *      ReadPreferences             - Reads user Preferences into structure from file
 *      SaveSettings                - Save Preferences structure to file
 *      hasGPSEnabled               - Test if device has GPS enabled
 *      hasNetwork                  - Test if device has Network enabled
 */

public class Functions{

    public static String GetIMEI(Context context)
    {   // Get Telephone IMEI
        String m_deviceID;

        try {
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

            m_deviceID = tm.getDeviceId();

            return m_deviceID;

        }
        catch (Exception ex){
            m_deviceID = "";
            return m_deviceID;
        }
    } // end GetIMEI()

    public static String GetAndroidID(Context context)
    {   //  Get Android ID

        try {
            String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            return android_id;
        }
        catch (Exception ex) {
            return "";
        }
    } // end GetAndroidID()

    public static String GetMobileNumber(Context context)
    {   // Get Mobile telephone number
        String mobileNumber;
        try {
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

            mobileNumber = tm.getLine1Number();

            return mobileNumber;

        }
        catch (Exception ex){
            mobileNumber = "";
            return mobileNumber;
        }
    }   // end GetMobileNumber()

    public static DataStructures.PreferencesStruct ReadPreferences(Context context) {
        // Read settings/preferences into Settings Structure
        //  Revision History:
        //      22 February, 2016   Authored
        //
        // * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        DataStructures.PreferencesStruct Prefs = new DataStructures.PreferencesStruct();
        String filename = context.getResources().getString(R.string.shared_preferences_filename);
        SharedPreferences settings = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        File settings_file = new File(filename);

        // Account Information / User
        if (settings.contains("UserEmailAddress"))
        {
            Prefs.user_email = settings.getString("UserEmailAddress","");
        } else {
            Prefs.user_email = "";
        }

        if (settings.contains("UserFirstName"))
        {
            Prefs.user_firstname = settings.getString("UserFirstName","");
        } else {
            Prefs.user_firstname = "";
        }

        if (settings.contains("UserLastName"))
        {
            Prefs.user_lastname = settings.getString("UserLastName","");
        } else {
            Prefs.user_lastname = "";
        }

        if (settings.contains("UserPassword"))
        {
            Prefs.user_password = settings.getString("UserPassword","");
        } else {
            Prefs.user_password = "";
        }
        
        if (settings.contains("UserInfoChanged")){
            Prefs.user_info_changed = settings.getBoolean("UserInfoChanged", false);
        } else {
            Prefs.user_info_changed = false;
        }

        if (settings.contains("DisplayOnlySnabShots")){
            Prefs.display_Only_Snabshots = settings.getBoolean("DisplayOnlySnabShots", false);
        } else {
            Prefs.display_Only_Snabshots = false;
        }

        return  Prefs;

    }   // End ReadPreferences()

    public static boolean SavePreferences(Context context, DataStructures.PreferencesStruct prefs) {
        // Save settings/preferences from Settings Structure
        //  Revision History:
        //      22 February, 2016   Authored
        //
        // * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
        String filename = context.getResources().getString(R.string.shared_preferences_filename);

        SharedPreferences settings = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = settings.edit();

        edit.putString("UserEmailAddress", prefs.user_email);
        edit.putString("UserFirstName", prefs.user_firstname);
        edit.putString("UserLastName", prefs.user_lastname);
        edit.putString("UserPassword", prefs.user_password);
        edit.putBoolean("UserInfoChanged", prefs.user_info_changed);
        edit.putBoolean("DisplayOnlySnabShots", prefs.display_Only_Snabshots);

        // Save edits
        edit.apply();

        return true;
    }   // end Save Settings()

    public static String GetMD5Hash(String text){
        StringBuffer MD5Hash = new StringBuffer();
        try{
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(text.getBytes());
            byte messageDigest[] = digest.digest();

            for (int i = 0; i < messageDigest.length; i++)
            {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }

        } catch (NoSuchAlgorithmException ex){

        }

        return MD5Hash.toString();
    }   // end GetMD5Has()

}
