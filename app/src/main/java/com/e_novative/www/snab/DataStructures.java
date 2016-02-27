package com.e_novative.www.snab;

/**
 * Created by AGrossman on 2/22/2016.
 */
public class DataStructures {

    // Preferences/Settings Structure
    public static class PreferencesStruct {
        public boolean user_info_changed;       // Set by Settings. Cleared when PostUpdate complete
        public String user_email;               // User Email address
        public String user_firstname;
        public String user_lastname;
        public String user_password;

        public boolean has_read_EULA;

        public boolean display_Only_Snabshots;

    }   // end PreferencesStruct()


}
