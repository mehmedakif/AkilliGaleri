package com.uear.akilligaleri.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.uear.akilligaleri.DBManager;
import com.uear.akilligaleri.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PersonPhotos extends AppCompatActivity {

    private static boolean isGalleryInitalized = false;
    private final File imagesDir = new File(String.valueOf(Environment.
            getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
    private final File[] files = imagesDir.listFiles();


    final PersonPhotos.GalleryAdapter galleryAdapter = new GalleryAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_photos);
        GridView gridview = findViewById(R.id.personGridView);
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            String akif = extras.getString("PersonID");
            Toast toast = Toast.makeText(this,akif,Toast.LENGTH_LONG);
            toast.show();
        }


        galleryAdapter.setData(getIndividuals());
        gridview.setAdapter(galleryAdapter);
        isGalleryInitalized = true;

    }
    /*TODO bu fonksiyon kisiyi parametre olarak alacak.*/
    public List<String> getIndividuals()
    {
        List<String> filesList = new ArrayList<>();
        /*TODO fetchPerson'u dinamik yaparak istenen kisiyi getirtecegiz.*/
        Cursor cursor = DBManager.fetchPerson("1");
        cursor.moveToFirst();
        String path;
        if(cursor.moveToFirst()){

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndex("resimAdres"));
                filesList.add(path);
                Toast toast = Toast.makeText(this,path,Toast.LENGTH_LONG);
                toast.show();
            }
            cursor.close();
        }
        return filesList;
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
