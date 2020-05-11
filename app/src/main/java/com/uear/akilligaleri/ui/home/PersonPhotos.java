package com.uear.akilligaleri.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.uear.akilligaleri.DBManager;
import com.uear.akilligaleri.DialogHelper;
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
        String requestedPersonClassID = null;
        if (extras != null) {
            requestedPersonClassID = extras.getString("PersonID");
        }
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

        gridview.setOnItemClickListener((parent, view, position, id) ->
        {
            Object sendItem = individualsGalleryAdapter.getItem(position);
            String picPath = sendItem.toString();
            createAlert(picPath);

        });

        }

    public void createAlert(String picPath){
        AlertDialog.Builder builder = DialogHelper.alertBuilder(PersonPhotos.this);

        final EditText input = new EditText(PersonPhotos.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);


        ArrayList<String> spinnerArray = new ArrayList<String>();

        Cursor countCursor = DBManager.fetchDistinctFaces();
        countCursor.moveToFirst();
        do {
            spinnerArray.add(countCursor.getString(0));
        }while (countCursor.moveToNext());



        Spinner classSpinner = new Spinner(PersonPhotos.this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(PersonPhotos.this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        classSpinner.setAdapter(spinnerArrayAdapter);
        classSpinner.setPadding(55,0,55,0);
        builder.setView(classSpinner);



        builder.setTitle("Akıllı Galeri");
        builder.setMessage("Yanlış etiketleme olduğunu mu düşünüyorsunuz? Öyle ise bu kişiyi yeniden tanımlayın :");

        builder.setNeutralButton(
                "Iptal",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
        builder.setPositiveButton(
                "Değiştir",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DBManager.updateClass(classSpinner.getItemAtPosition(classSpinner.getSelectedItemPosition()).toString(),picPath);
                        Toast toastPaylas = Toast.makeText(PersonPhotos.this, "Değiştirme işlemi yapıldı.", Toast.LENGTH_LONG);
                        toastPaylas.show();
                        dialog.dismiss();
                    }
                }
        );
        builder.setNegativeButton(
                "Etiketi Kaldır",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast toastPaylas = Toast.makeText(PersonPhotos.this, "Etiket kaldırıldı", Toast.LENGTH_LONG);
                        toastPaylas.show();
                        dialog.dismiss();
                    }
                }
        );

        builder.show();
    }


    @Override
    public void onResume()
    {
        super.onResume();

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
