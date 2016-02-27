package com.e_novative.www.snab;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/* * * * * * * * * * * * * * * * * * * * * * * * * * *
    Settings and User defaults
    Revision History:
        22 February, 2016       Authored

    Copyright 2016 E-Novative, Inc.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class SettingsActivity extends AppCompatActivity {

    // Layout controls
    // Account Information - identity
    TextView mIdentity_Category;
    LinearLayout mIdentity_expandable;

    // General
    TextView mGeneral_Category;
    LinearLayout mGeneral_expandable;

    // EULA
    TextView mEULA_Category;
    LinearLayout mEULA_expandable;

    // Account Identity
    EditText userEmailAddress_control;
    EditText userFirstName_control;
    EditText userLastName_control;
    EditText userPassword_control;

    ImageView seeUserPassword_control;

    TextView logonAt_control;

    CheckBox displayOnlySnab_control;

    boolean hasEmailChanged = false;
    boolean hasFirstNameChanged = false;
    boolean hasLastNameChanged = false;
    boolean hasPasswordChanged = false;

    // Settings/preferences Structure
    DataStructures.PreferencesStruct Prefs = new DataStructures.PreferencesStruct();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        InitializePage();
    }

    @Override
    protected void onPause() {
        // Pausing or exiting
        super.onPause();

        Prefs.user_info_changed = true;
        Log.d("SNAB", "Saving Prefs");

        if (hasEmailChanged || hasFirstNameChanged || hasLastNameChanged || hasPasswordChanged){
            //  Account information changed. Set Flag for Main
            Prefs.user_info_changed = true;
        }
        // for now save them always on exit
        SetPreferences();

    }   // end onPause()


    private void InitializePage()
    {
        // Initialize this intent
        userEmailAddress_control = (EditText)findViewById(R.id.useremailaddress);
        userFirstName_control = (EditText)findViewById(R.id.userfirstname);
        userLastName_control = (EditText)findViewById(R.id.userlastname);
        userPassword_control = (EditText)findViewById(R.id.userpassword);
        displayOnlySnab_control = (CheckBox) findViewById(R.id.cbxDisplyOnlySnabshots);

        // Read User Preferences
        Prefs = Functions.ReadPreferences(this);
        DisplayPrefs();

        mIdentity_Category = (TextView) findViewById(R.id.identity_category);
        mIdentity_expandable = (LinearLayout) findViewById(R.id.identity_expandable);
        mIdentity_Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIdentity_expandable.getVisibility() == View.GONE) {
                    expand(mIdentity_expandable);
                    collapse(mGeneral_expandable);
                    collapse(mEULA_expandable);
                } else {
                    collapse(mIdentity_expandable);
                }
            }
        });

        mGeneral_Category = (TextView) findViewById(R.id.general_category);
        mGeneral_expandable = (LinearLayout) findViewById(R.id.general_expandable);
        mGeneral_Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGeneral_expandable.getVisibility()==View.GONE){
                    collapse(mIdentity_expandable);
                    expand(mGeneral_expandable);
                    collapse(mEULA_expandable);
                } else {
                    collapse(mGeneral_expandable);
                }
            }
        });

        mEULA_Category = (TextView) findViewById(R.id.eula_category);
        mEULA_expandable = (LinearLayout) findViewById(R.id.eula_expandable);
        mEULA_Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGeneral_expandable.getVisibility()==View.GONE){
                    collapse(mIdentity_expandable);
                    collapse(mGeneral_expandable);
                    expand(mEULA_expandable);
                } else {
                    collapse(mGeneral_expandable);
                }
            }
        });

    }   // end InitializePage()


    // Location Animation Functions
    private void expand(LinearLayout thisLayout){
        //set expandable Visible
        thisLayout.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        thisLayout.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(thisLayout, 0, thisLayout.getMeasuredHeight());
        mAnimator.start();
    }

    private void collapse(LinearLayout thisLayout) {
        //http://gmariotti.blogspot.com/2013/09/expand-and-collapse-animation.html
        int finalHeight = thisLayout.getHeight();

        ValueAnimator mAnimator = slideAnimator(thisLayout, finalHeight, 0);
        final LinearLayout layout = thisLayout;

        layout.setVisibility(View.GONE);

    }

    private ValueAnimator slideAnimator(final LinearLayout thisLayout, int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = thisLayout.getLayoutParams();
                layoutParams.height = value;
                thisLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void DisplayPrefs(){
        // Display Preferences on screen
        userEmailAddress_control.setText(Prefs.user_email);
        userFirstName_control.setText(Prefs.user_firstname);
        userLastName_control.setText(Prefs.user_lastname);
        userPassword_control.setText(Prefs.user_password);
        displayOnlySnab_control.setChecked(Prefs.display_Only_Snabshots);

    }   // end DisplayPrefs()

    private void SetPreferences() {
        // Set then Save Preferences structure
        boolean success;

        Prefs.user_email = userEmailAddress_control.getText().toString();
        Prefs.user_firstname = userFirstName_control.getText().toString();
        Prefs.user_lastname = userLastName_control.getText().toString();
        Prefs.display_Only_Snabshots = displayOnlySnab_control.isChecked();

        if (hasPasswordChanged){
            String MD5Password = Functions.GetMD5Hash(userPassword_control.getText().toString());
            Prefs.user_password = MD5Password;
            Log.d("TRAFFIC", "MD5: " + MD5Password);
        }

        success = Functions.SavePreferences(this, Prefs);

    }   // end SetPreferences()

}
