package com.e_novative.www.snab;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.io.IOException;
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
    private static final int SELECT_PICTURE = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";


    private Uri imageURI;
    private Bitmap bitmap;

    private ImageView takePhoto;           // snab logo acts a button
    private ImageView uploadPhoto;
    private String mCurrentPhotoPath;
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
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    public void Initialize(){
        // Initialize stuff for this page/intent
        Prefs = Functions.ReadPreferences(MainActivity.this);

        // snab logo is used to start Camera
        takePhoto = (ImageView) findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(cameraListener);

      //  myGallery = (LinearLayout)findViewById(R.id.mygallery);

      //  displayMyGallery();

        // Upload Button / enabled after photo is taken or selected.
        uploadPhoto = (ImageView) findViewById(R.id.upload_photo);
        // Upload Picture Listener
        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // in onCreate or any event where your want the user to
                // select a file

                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, SELECT_PICTURE);

                // Upload Photo to SNAB account
                if (!Prefs.has_read_EULA) {
                    // Display Eula and registration
                }
            }
        });

    }   // end Initialize()
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    dispatchTakePictureIntent();

                } else {

                    Toast.makeText(MainActivity.this, "No camera permission", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;

       // if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir =  Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES+"/snab");

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

       // } else {
       //     Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
       // }

        return storageDir;
    }
    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    imageURI=Uri.fromFile(f);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageURI );
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }




            startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_B);


    }

    private View.OnClickListener cameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            takePhoto(v);
        }
    };
    private void takePhoto(View v){
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)|| (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)){

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
        else
        {
            dispatchTakePictureIntent();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
       super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_TAKE_PHOTO_B && resultCode == Activity.RESULT_OK){
            galleryAddPic();
            StartContentActivity(mCurrentPhotoPath);

        } else  if (requestCode == SELECT_PICTURE&& resultCode == Activity.RESULT_OK) {
           // Uri selectedImageUri = data.getData();

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            String imagePath =picturePath;
            StartContentActivity(imagePath);
        }
        else{
            Toast.makeText(MainActivity.this, "Picture Cancelled", Toast.LENGTH_LONG).show();
        }
    }

    private void StartContentActivity(String message) {
        Intent intent = new Intent(this, NewContentActivity.class);
        intent.putExtra(NewContentActivity.EXTRA_MESSAGE, message);
        startActivity(intent);
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


}
