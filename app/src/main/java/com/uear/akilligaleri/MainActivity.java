package com.uear.akilligaleri;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.ViewGroup;
import android.widget.Adapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity
{
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static boolean isGalleryInitalized = false;
    private static final int REQUEST_PERMISSION = 1234;

    private static final String[] PERMISSION =
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE

            };

    private AppBarConfiguration mAppBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



    private static final int PERMISSION_COUNT = 2 ;

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

    public void onRequestPermissionsResult(final int requestCode,final String[] permissions,final int[] grantResults)
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
    public void onResume()
    {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied())
        {
            requestPermissions (PERMISSION, REQUEST_PERMISSION);
            return;
        }

        if (!isGalleryInitalized)
        {
            final GridView gridview = findViewById(R.id.gridView);
            final GalleryAdapter galleryAdapter = new GalleryAdapter();
            final File imagesDir = new File(String.valueOf(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
            final File[] files = imagesDir.listFiles();
            final int filesCount = files.length;
            final List<String> filesList = new ArrayList<>();

            for (File file : files)
            {
                final String path = file.getAbsolutePath();

                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg"))
                {
                    filesList.add(path);
                }
            }

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   Object selectedItem = parent.getItemAtPosition(position);
                   Toast toast = Toast.makeText(getApplicationContext(),selectedItem.toString(),Toast.LENGTH_LONG);
                   toast.show();


                }
            });

            galleryAdapter.setData(filesList);
            gridview.setAdapter(galleryAdapter);
            isGalleryInitalized = true;


        }

    }




    public final class GalleryAdapter extends BaseAdapter
    {
        List<String> data = new ArrayList<>();
        void setData(List<String> data)
        {
            if ( this.data.size() > 0 )
            {
                this.data.clear();
            }
            this.data.addAll(data);
            notifyDataSetChanged();
        }
        @Override
        public int getCount()
        {
            return data.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final ImageView imageview;
            if ( convertView == null )
            {
                imageview = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
            }
            else imageview = (ImageView) convertView;
            Glide.with(MainActivity.this).load(data.get(position)).centerCrop().into(imageview);
            return imageview;
        }
    }
}
