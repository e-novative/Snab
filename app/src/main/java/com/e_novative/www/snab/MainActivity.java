package com.e_novative.www.snab;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    // SNAB Prototype
    //  Revision History:
    //      17 February, 2016           Basic Framework
    //      22 February, 2016           Preferences menu
    //      26 February, 2016           TakePhoto/UploadPhoto
    //
    //  * * * * * * * * * * * * * ** * * * * * * * * * * * * *
    // Public Variable
    private static final String TAG = "SNAB";
    private static final int thumbSizeW = 480;
    private static final int thumbSizeH = 380;

    private static final int TAKE_PICTURE = 1;
    private Uri imageURI;
    private Bitmap bitmap;

    private ImageView takePhoto;           // snab logo acts a button
    private ImageView uploadPhoto;
    private LinearLayout myGallery;         // picture gallery

    private String thisFilename;

    // Settings/preferences Structure
    DataStructures.PreferencesStruct Prefs = new DataStructures.PreferencesStruct();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Initialize();
    }

    @Override
    protected void onPause() {
        // Pausing or exiting this App
        super.onPause();

        }   // end onPause()

    @Override
    public void onResume() {
        // Resuming functions (called after onCreate when starting fresh)
        super.onResume();

        // Reload Settings
        Prefs = Functions.ReadPreferences(MainActivity.this);

        // Keep Screen on  (Turn off on exit)
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    } // end onResume()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Select Menu option to display
        if (id == R.id.action_settings) {
            switch (item.getItemId()){
                case R.id.action_settings:
                    doSettingsMenu();
                    break;

                case R.id.action_terms:
                    doTermsMenu();
                    break;

                default:
                    return false;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Initialize(){
        // Initialize stuff for this page/intent
        Prefs = Functions.ReadPreferences(MainActivity.this);

        // snab logo is used to start Camera
        takePhoto = (ImageView) findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(cameraListener);

        myGallery = (LinearLayout)findViewById(R.id.mygallery);

      //  displayMyGallery();

        // Upload Button / enabled after photo is taken or selected.
        uploadPhoto = (ImageView) findViewById(R.id.upload_photo);
        uploadPhoto.setClickable(false);
        uploadPhoto.setAlpha(.5f);
        // Upload Picture Listener
        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto.setClickable(false);
                uploadPhoto.setAlpha(.5f);

                Toast.makeText(MainActivity.this, "Uploading to Snab", Toast.LENGTH_LONG).show();

                // Upload Photo to SNAB account
                if (!Prefs.has_read_EULA) {
                    // Display Eula and registration
                }
            }
        });

    }   // end Initialize()

    private void displayMyGallery() {
        // Display my Picture Gallery / filtered for snab_
        //  Reference:   http://android-er.blogspot.com/2012/07/implement-gallery-like.html
        String ExternalStorageDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

        String targetPath = ExternalStorageDirectoryPath;

        File targetDirector = new File(targetPath);

        File[] files = targetDirector.listFiles();
        for (File file : files){
            // Filter for 'snab' or all images
            if (Prefs.display_Only_Snabshots){
                if (file.getAbsolutePath().contains("snab_")){
                    myGallery.addView(insertPhoto(file.getAbsolutePath()));
                }
            } else {
                myGallery.addView(insertPhoto(file.getAbsolutePath()));
            }
        }
    }   // end displayMyGallery()

    View insertPhoto(String path) {
        Bitmap bm = decodeSampledBitmapFromUri(path, thumbSizeW, thumbSizeH);

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new ViewGroup.LayoutParams(thumbSizeW, thumbSizeH));
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bm);

        layout.addView(imageView);
        return layout;
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
        Bitmap bm = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        return inSampleSize;
    }

    // OnClickListener to launch camera
    private View.OnClickListener cameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takePhoto(v);
        }
    };
    // launch Image Capture intent. Save photo using unique filename.
    private void takePhoto(View v){
        MakeFilename();
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), thisFilename);
        imageURI = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    private void MakeFilename(){
        // Make a filename using timestamp.
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyy_hhmmss");
        thisFilename = "snab_" + s.format(new Date()) + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
       super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK){

            Uri selectedImage = imageURI;
            getContentResolver().notifyChange(selectedImage, null);

            // Display Description Dialog
            PhotoDescription_Dialog();

            // after taking photo, update Gallery to allow user to select it.
            //xxxx

            Toast.makeText(MainActivity.this, "Successfully Snabbed photo", Toast.LENGTH_LONG).show();
            uploadPhoto.setClickable(true);
            uploadPhoto.setAlpha(1f);

        } else {
            Toast.makeText(MainActivity.this, "Picture Cancelled", Toast.LENGTH_LONG).show();
        }
    }

    // Menu Options Intents
    private void doSettingsMenu() {
        // Launch Settings/Preferences Intent
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void doTermsMenu(){
        // Launch Terms and Conditions Intent
       // Intent intent = new Intent(this,  )
    }

    private void PhotoDescription_Dialog(){
        // Popup dialog box to describe photo
        final Dialog photoDescriptionDialog = new Dialog(MainActivity.this);
        photoDescriptionDialog.setTitle(R.string.photoDescriptionTitle);

        photoDescriptionDialog.setContentView(R.layout.photo_description_dialog);

        // Stop Reason PostDelayed timeout
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            // Timeout if user sleeping
                photoDescriptionDialog.dismiss();
            }
        }, 3000000);

        photoDescriptionDialog.show();

    }   // end photoDescriptionDialog()
}
