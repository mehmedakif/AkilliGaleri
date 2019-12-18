package com.uear.akilligaleri.ui.gallery;

import android.Manifest;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.uear.akilligaleri.ImgActivity;
import com.uear.akilligaleri.R;
import com.uear.akilligaleri.ui.SingleUploadBroadcastReceiver;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONException;
import org.json.JSONObject;

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

public class GalleryFragment extends Fragment implements SingleUploadBroadcastReceiver.Delegate {

    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();

    private static boolean isGalleryInitalized = false;
    private static final String[] PERMISSION =
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET
            };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        GalleryViewModel galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        return root;

    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!isGalleryInitalized)
        {
            GridView gridview = Objects.requireNonNull(getView()).findViewById(R.id.galleryGridView);
            final GalleryAdapter galleryAdapter = new GalleryAdapter();
            final File imagesDir = new File(String.valueOf(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
            final File[] files = imagesDir.listFiles();
            final List<String> filesList = new ArrayList<>();

            assert files != null;
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
                //Bitmap picBitmap = BitmapFactory.decodeFile(picPath);
                uploadImage(picPath);
            });

            galleryAdapter.setData(filesList);
            gridview.setAdapter(galleryAdapter);
            isGalleryInitalized = true;
        }
        uploadReceiver.register(Objects.requireNonNull(getActivity()));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {

    }

    @Override
    public void onError(Exception exception) {

    }

    @Override
    public void onCompleted ( int serverResponseCode, byte[] serverResponseBody)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        /*
        * Serverdan gelen response (resmin adi) value stringine yaziliyor.
        * response icerigi suna benzerdir ; value = img-15642132.jpg
        * String facesImg = serverResponseBody.getString(outputImg);
        */
        File directory = new File(Environment.getExternalStorageDirectory() + java.io.File.separator + "Pictures/detectedFaces");
        if (!directory.exists()) {
            directory.mkdir();
            Toast.makeText(getActivity(), "DETECED KLASORU OLUSTURULDU", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity(), "KLASOR ZATEN VAR", Toast.LENGTH_SHORT).show();


        try {
            JSONObject testV = new JSONObject(new String(serverResponseBody));

            int objectLength = testV.length();
            String[] imgArray = new String[objectLength];
            for(int i = 0; i<objectLength-1 ;i++)
            {
                String iterator = String.valueOf(i+1);
                imgArray[i] = testV.getString("face"+iterator);
                Bitmap bmp = downloadImageFromPath(imgArray[i]);
                String[] bitmapFaces = new String[objectLength];
                bitmapFaces[i] = bmp.toString();
                bitmapToFile(bmp,false);
            }
            imgArray[objectLength-1]= testV.getString("outputImg");
            Bitmap bmp = downloadImageFromPath(imgArray[objectLength-1]);
            bitmapToFile(bmp,true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelled() {

    }
/* author AkifAy
*
* Uploading image to server.
* gets path of image as string.
*
* */
    private void uploadImage(String path) {

        String name = "image";
        try {
            String uploadId = UUID.randomUUID().toString();
            uploadReceiver.setDelegate(this);
            uploadReceiver.setUploadID(uploadId);
            /*Upload URL is a static variable. May change*/
            String UPLOAD_URL = "http://81.214.177.75:3000/upload";
            new MultipartUploadRequest(Objects.requireNonNull(getActivity()), uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "uploadImage") //Adding file
                    .addParameter("name", name) //Adding text parameter to the request
                    .setMaxRetries(2)
                    .startUpload();

        } catch (Exception ignored) {

        }


    }
/* author AkifAy
*
* Makes Http request for image.
* finds and request image with its path
* path comes from server as response of upload request
*
* */
    private Bitmap downloadImageFromPath(String path){
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
            }

        }
        catch(Exception ex){
            Log.e("Exception",ex.toString());
        }
        return bmp;
    }

/* author AkifAy
*
*  Saving downloaded bitmap object to the device's storage.
*
*  "control" is a boolean flag for detect last item of JSONObject
*  if it is "True" this image is the output image and will be shown
*  into the dialog.
*
* */
    private void bitmapToFile(Bitmap bmp,boolean control) {

        try {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath(); // "storage/emulated/0" yolunu getirir.
            root = root+ "/Pictures/detectedFaces"; // "storage/emulated/0/Pictures" haline getirilir.
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fileName = "Image-"+n+".png";
            File f = new File(root + File.separator + fileName);
     //If there is a photo with same name
            if (f.exists()) {
                String imgDownloaded = root+"/"+fileName;
                Intent intent = new Intent(this.getContext(), ImgActivity.class);
                intent.putExtra("path", imgDownloaded);
                startActivity(intent);
            }
            else
            {
                FileOutputStream out = new FileOutputStream(f);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();

                String imgDownloaded = root+"/"+fileName;
                if(control) {
                    Intent intent = new Intent(this.getContext(), ImgActivity.class);
                    intent.putExtra("path", imgDownloaded);
                    startActivity(intent);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView imageview;
            if (convertView == null) {
                imageview = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            } else imageview = (ImageView) convertView;
            Glide.with(GalleryFragment.this).load(data.get(position)).centerCrop().into(imageview);
            return imageview;
        }
    }
}