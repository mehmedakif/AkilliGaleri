package com.uear.akilligaleri;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.uear.akilligaleri.ui.SingleUploadBroadcastReceiver;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static android.widget.Toast.makeText;

public class GalleryLayout extends AppCompatActivity implements SingleUploadBroadcastReceiver.Delegate {
    String UPLOAD_URL = "http://81.214.177.75:3000/upload";
    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();
    private NavigationView nvDrawer;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;
    @Override
    public Context getApplicationContext()
    {
        return super.getApplicationContext();
    }

    public static boolean isGalleryInitalized = false;
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);
        Toast bakalim = Toast.makeText(this,"Hmmm",Toast.LENGTH_LONG);
        bakalim.show();
    }
    public void onResume()
    {
        super.onResume();

        if (arePermissionsDenied())
        {
            requestPermissions (PERMISSION, REQUEST_PERMISSION);
            return;
        }

        if (!isGalleryInitalized)
        {
            final GridView gridview = findViewById(R.id.gridView);
            final GalleryAdapter galleryAdapter = new GalleryAdapter();
            final File imagesDir = new File(String.valueOf(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
            final File[] files = imagesDir.listFiles();
            //final int filesCount = files.length;
            final List<String> filesList = new ArrayList<>();

            for (File file : files)
            {
                final String path = file.getAbsolutePath();

                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg"))
                {
                    filesList.add(path);
                }
            }

            gridview.setOnItemClickListener((parent, view, position, id) -> {
                //galleryAdapterin dokunulan pozisyonundaki obje sendItem adiyla aliniyor.
                // Bu obje fotografin yolunu tutuyor.
                Object sendItem = galleryAdapter.getItem(position);

                // Alinan obje yani fotografin yolu stringe ceviriliyor.
                String picPath = sendItem.toString();

                // bu yol kullanilarak fotograf bitmap seklinde tutuluyor.
                Bitmap picBitmap = BitmapFactory.decodeFile(picPath);
                uploadImage(picPath);
            });

            galleryAdapter.setData(filesList);
            gridview.setAdapter(galleryAdapter);
            isGalleryInitalized = true;
        }
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }

    @Override
    public void onProgress(int progress) {
        //your implementation
    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {
        //your implementation
        Toast toast = makeText(this,"POST DEVAM EDIYOR",Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onError(Exception exception) {
        //your implementation
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        Toast toastComplete = makeText(this,"POST TAMAMLANDI",Toast.LENGTH_LONG);
        StrictMode.setThreadPolicy(policy);
        //Serverdan gelen response (resmin adi) value stringine yaziliyor.
        // response icerigi suna benzerdir ; value = img-15642132.jpg
        String value = new String(serverResponseBody);
        //Resim gelen value degeri parametre gecilerek
        Bitmap bmp = downloadImageFromPath(value);
        File directory = new File(Environment.getExternalStorageDirectory() + java.io.File.separator +"Pictures/detectedFaces");

        if (!directory.exists())
        {
            directory.mkdir();
            Toast.makeText(this, "DETECED KLASORU OLUSTURULDU", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "KLASOR ZATEN VAR", Toast.LENGTH_SHORT).show();

        bitmapToFile(bmp);
    }
    @Override
    public void onCancelled() {
        //your implementation
    }
    //Servere fotografi byte[] array olarak yollayan fonksiyon.
    private void uploadImage(String path) {

        String name = "image";
        try {
            String uploadId = UUID.randomUUID().toString();
            uploadReceiver.setDelegate(this);
            uploadReceiver.setUploadID(uploadId);
            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "uploadImage") //Adding file
                    .addParameter("name", name) //Adding text parameter to the request
                    .setMaxRetries(2)
                    .startUpload();

            Toast toast = makeText(this, "POST BASLATILDI", Toast.LENGTH_LONG);
            toast.show();//Starting the upload

        } catch (Exception e) {
            Toast toast = makeText(this, "UPLOAD EDILEMEDI", Toast.LENGTH_LONG);
            toast.show();
        }
    }
    /*Serverdan gelen sonuc fotografinin adi path olarak aliniyor.
    path ile cagirilan img Bitmap olarak donduruluyor.*/
    public Bitmap downloadImageFromPath(String path){
        InputStream in =null;
        Bitmap bmp=null;
        int responseCode = -1;
        try{
            URL url = new URL("http://81.214.177.75:3000/"+path);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoInput(true);
            con.connect();
            responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                //download
                in = con.getInputStream();
                bmp = BitmapFactory.decodeStream(in);
                in.close();
                //iv.setImageBitmap(bmp);
            }

        }
        catch(Exception ex){
            Log.e("Exception",ex.toString());
        }
        return bmp;
    }
    //Bitmap olarak aldigi parametereyi yerel bir klasore kayit ediyor.
    public void bitmapToFile(Bitmap bmp) {

        try {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath(); // "storage/emulated/0" yolunu getirir.
            root = root+ "/Pictures/detectedFaces"; // "storage/emulated/0/Pictures" haline gtirilir.
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fileName = "Image-"+n+".png";
            File f = new File(root + File.separator + fileName);

            if (f.exists()) {
                Toast toast = makeText(this,"RESIM ZATEN VAR",Toast.LENGTH_LONG);
                toast.show();
                //f.delete();
//                FileOutputStream out = new FileOutputStream(f);
//                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
//                out.flush();
//                out.close();
                String imgDownloaded = root+"/"+fileName;
                Intent intent = new Intent(this, ImgActivity.class);
                intent.putExtra("path", imgDownloaded);
                startActivity(intent);
            }
            else
            {
                FileOutputStream out = new FileOutputStream(f);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                Toast toast = makeText(this,"RESIM DOSYAYA YAZILDI",Toast.LENGTH_LONG);
                toast.show();

                String imgDownloaded = root+"/"+fileName;
                Intent intent = new Intent(this, ImgActivity.class);
                intent.putExtra("path", imgDownloaded);
                startActivity(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Gallery adapter
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
            return data.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
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
            Glide.with(GalleryLayout.this).load(data.get(position)).centerCrop().into(imageview);
            return imageview;
        }
    }
}
