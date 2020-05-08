package com.uear.akilligaleri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.uear.akilligaleri.ui.SingleUploadBroadcastReceiver;
import com.uear.akilligaleri.ui.gallery.GalleryFragment;
import com.uear.akilligaleri.ui.home.HomeFragment;
import com.uear.akilligaleri.ui.people.PeopleFragment;
import com.uear.akilligaleri.ui.send.SendFragment;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    private DBManager dbManager;
    private DrawerLayout mDrawer;
    Fragment fragment_home = new HomeFragment();
    Fragment fragment_gallery = new GalleryFragment();
    Fragment fragment_send = new SendFragment();
    Fragment fragment_people  = new PeopleFragment();
    Fragment active = fragment_gallery;
    ImageView imgTakenPic;
    private static final int CAM_REQUEST=1313;
    final FragmentManager fm = getSupportFragmentManager();
    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();



    @Override
    public Context getApplicationContext()
    {
        return super.getApplicationContext();
    }

    private static final int REQUEST_PERMISSION = 1234;
    private static final int PERMISSION_COUNT = 3 ;
    private static final String[] PERMISSION =
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.CAMERA
            };

    private boolean arePermissionsDenied()
    {
        for(int i = 0 ;i<PERMISSION_COUNT;i++)
        {
            if(checkSelfPermission(PERMISSION[i]) != PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }
        }
        return false;
    }

    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if( requestCode == REQUEST_PERMISSION && grantResults.length>0 )
        {
            if( arePermissionsDenied() )
            {
                ( (ActivityManager) Objects.requireNonNull( this.getSystemService(ACTIVITY_SERVICE) ) ).
                        clearApplicationUserData();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions (PERMISSION, REQUEST_PERMISSION);

        NavigationView nvDrawer = findViewById(R.id.nav_view);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar1 = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar1);
        // Find our drawer view
        mDrawer = findViewById(R.id.drawer_layout);
        DrawerLayout mDrawer = findViewById(R.id.drawer_layout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        Button menuButton = findViewById(R.id.menu_button);
        Button cameraButton = findViewById(R.id.camera_button);

        fm.beginTransaction().add(R.id.flContent, fragment_home, "3").hide(fragment_home).commit();
        fm.beginTransaction().add(R.id.flContent, fragment_gallery, "2").hide(fragment_gallery).commit();

        fm.beginTransaction().hide(active).show(fragment_gallery).commit();
        active = fragment_gallery;

        dbManager = new DBManager(this);
        dbManager.open();

        menuButton.setOnClickListener(v -> mDrawer.openDrawer(Gravity.LEFT));
        cameraButton.setOnClickListener(new btnTakePhotoClicker());
    }

    public void uploadImage(String path, int position)
    {
        try
        {
            String uploadId = UUID.randomUUID().toString();
            uploadReceiver.setDelegate((SingleUploadBroadcastReceiver.Delegate) this);
            uploadReceiver.setUploadID(uploadId);
            /*Upload URL is a static variable. May change*/
            String UPLOAD_URL = "http://212.253.48.98:3000/upload";
            String id = String.valueOf(position);
            new MultipartUploadRequest(getApplicationContext(), uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "uploadImage") //Adding file
                    .addParameter("imgId", id)//Adding text parameter to the request
                    .addParameter("userId", "45689")//Adding text parameter to the request
                    .setMaxRetries(2)
                    .startUpload();
        }
        catch (Exception ignored)
        { }
    }

    class btnTakePhotoClicker implements  Button.OnClickListener
    {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,CAM_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAM_REQUEST){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            Date date = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmm");
            String strDate = dateFormat.format(date);
            File file = new File(path, "/" + strDate + ".png");

            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadImage(file.toString(),10001);
        }
    }

    private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    public void selectDrawerItem(MenuItem menuItem)
    {

        switch(menuItem.getItemId())
        {
            case R.id.nav_home:
                fm.beginTransaction().hide(active).show(fragment_home).commit();
                active = fragment_home;
                break;

            case R.id.nav_send:
                fm.beginTransaction().hide(active).show(fragment_send).commit();
                active = fragment_send;
                break;

            case R.id.nav_people:
                fm.beginTransaction().hide(active).show(fragment_people).commit();
                active = fragment_people;
                break;

                default:
                    fm.beginTransaction().hide(active).show(fragment_gallery).commit();
                    active = fragment_gallery;
        }
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }

}

