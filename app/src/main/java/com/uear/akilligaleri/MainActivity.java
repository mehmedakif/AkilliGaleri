package com.uear.akilligaleri;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.uear.akilligaleri.ui.gallery.GalleryFragment;
import com.uear.akilligaleri.ui.home.HomeFragment;
import com.uear.akilligaleri.ui.send.SendFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    private DBManager dbManager;
    private DrawerLayout mDrawer;
    Fragment fragment_home = new HomeFragment();
    Fragment fragment_gallery = new GalleryFragment();
    Fragment fragment_send = new SendFragment();
    Fragment active = fragment_gallery;
    final FragmentManager fm = getSupportFragmentManager();



    @Override
    public Context getApplicationContext()
    {
        return super.getApplicationContext();
    }

    private static final int REQUEST_PERMISSION = 1234;
    private static final int PERMISSION_COUNT = 2 ;
    private static final String[] PERMISSION =
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET
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

        fm.beginTransaction().add(R.id.flContent, fragment_home, "3").hide(fragment_home).commit();
        fm.beginTransaction().add(R.id.flContent, fragment_gallery, "2").hide(fragment_gallery).commit();

        fm.beginTransaction().hide(active).show(fragment_gallery).commit();
        active = fragment_gallery;

        dbManager = new DBManager(this);
        dbManager.open();



        menuButton.setOnClickListener(v -> mDrawer.openDrawer(Gravity.LEFT));

    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }
    public void selectDrawerItem(MenuItem menuItem) {

        switch(menuItem.getItemId())
        {
            case R.id.nav_home:
                fm.beginTransaction().hide(active).show(fragment_home).commit();
                active = fragment_home;
                break;

            case R.id.nav_tools:
                fm.beginTransaction().hide(active).show(fragment_send).commit();
                active = fragment_send;
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

