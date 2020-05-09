package com.uear.akilligaleri.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.uear.akilligaleri.DBManager;
import com.uear.akilligaleri.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PersonPhotos extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_photos);


        Bundle extras = getIntent().getExtras();

        String requestedPersonClassID = Objects.requireNonNull(extras).getString("PersonID");
        Log.i("FACEID", Objects.requireNonNull(requestedPersonClassID));

            GridView gridview = findViewById(R.id.personGridView);
            final GalleryAdapter individualsGalleryAdapter = new GalleryAdapter();
            List<File> listOfFiles = new ArrayList<>();
            List<String> listOfPaths = new ArrayList<>();

            Cursor cursor = DBManager.fetchPerson(requestedPersonClassID);
            cursor.moveToFirst();
            do {
                String path = cursor.getString(cursor.getColumnIndex("resimAdres"));
                File file = new File(path);
                listOfFiles.add(file);
            }while(cursor.moveToNext());
            cursor.close();


            for (File file : listOfFiles)
            {
                final String path = file.getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg"))
                {
                    listOfPaths.add(path);
                }
            }
            individualsGalleryAdapter.setData(listOfPaths);
            gridview.setAdapter(individualsGalleryAdapter);

        }


    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
       finish();
    }

    final class GalleryAdapter extends BaseAdapter
    {
        List<String> data = new ArrayList<>();

        void setData(List<String> data) {
            if (this.data.size() > 0) {
                this.data.clear();
            }
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }
        @Override
        public Object getItem(int position) {

            return data.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView imageview;
            if (convertView == null) {
                imageview = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            } else imageview = (ImageView) convertView;
            Glide.with(PersonPhotos.this).load(data.get(position)).centerCrop().into(imageview);
            return imageview;
        }
    }
}
