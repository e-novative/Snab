package com.e_novative.www.snab;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.URI;

/**
 * Created by tonyk_000 on 2/26/2016.
 */
public class NewContentActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.e_novative.www.snab.MESSAGE";
    private Toolbar mToolbar;
    private TextView mToolbarTitle;

    private TextView mLocation;
    private ImageView mGeoIcon;

    private EditText mAddHashtags;
    private ImageView mHashIcon;

    private TextView mHashHint;

    private ImageView mAttachedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_content);
        setToolbar();

        mLocation = (TextView)findViewById(R.id.location);
        mGeoIcon = (ImageView)findViewById(R.id.geoIcon);

        mAddHashtags = (EditText)findViewById(R.id.add_hashtags);
        mHashIcon = (ImageView)findViewById(R.id.hashIcon);
        mAttachedImage = (ImageView)findViewById(R.id.attached_image);
        mHashHint = (TextView)findViewById(R.id.hash_hint);
        Intent intent = getIntent();
        String message = intent.getStringExtra(NewContentActivity.EXTRA_MESSAGE);
       // File f = new File(message);
        //Uri contentUri = Uri.fromFile(f);
        //mAttachedImage.setImageURI(null);
       // mAttachedImage.setImageURI(contentUri);

        File imgFile = new  File(message);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

              Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            Log.e("Screen width ", " "+width);
            Log.e("Screen height ", " " + height);
            Log.e("img width ", " "+myBitmap.getWidth());
            Log.e("img height ", " "+myBitmap.getHeight());
            float scaleHt =(float) width/myBitmap.getWidth();
            Log.e("Scaled percent ", " "+scaleHt);
            Bitmap scaled = Bitmap.createScaledBitmap(myBitmap,     width, (int) (myBitmap.getWidth()*scaleHt), true);
            mAttachedImage.setImageBitmap(myBitmap);

        }


    }

    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        if (mToolbar != null) {

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mToolbar.setNavigationIcon(R.drawable.backicon);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

  
 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_button:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
  

}
