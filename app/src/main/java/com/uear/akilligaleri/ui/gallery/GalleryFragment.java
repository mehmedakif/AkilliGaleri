package com.uear.akilligaleri.ui.gallery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import com.uear.akilligaleri.DBManager;
import com.uear.akilligaleri.DialogHelper;
import com.uear.akilligaleri.R;
import com.uear.akilligaleri.ui.SingleUploadBroadcastReceiver;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class GalleryFragment extends Fragment implements SingleUploadBroadcastReceiver.Delegate {

    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();
    private static boolean isGalleryInitalized = false;
    private final File imagesDir = new File(String.valueOf(Environment.
            getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
    private final File[] files = imagesDir.listFiles();
    private List<String> filesList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        GalleryViewModel galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        isGalleryInitalized = false;
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!isGalleryInitalized)
        {
            GridView gridview = Objects.requireNonNull(getView()).findViewById(R.id.galleryGridView);
            final GalleryAdapter galleryAdapter = new GalleryAdapter();
            assert files != null;
            for (File file : files)
            {
                final String path = file.getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg"))
                {
                    filesList.add(path);
                    Log.i("SQLITE PATH ANA EKRAN",path);
                }
            }
            //Fotograflara tiklayinca olacaklar.
            gridview.setOnItemClickListener((parent, view, position, id) ->
            {
                //galleryAdapterin dokunulan pozisyonundaki obje sendItem adiyla aliniyor.
                // Bu obje fotografin yolunu tutuyor.
                Object sendItem = galleryAdapter.getItem(position);
                // Alinan obje yani fotografin yolu stringe ceviriliyor.
                String picPath = sendItem.toString();
                // bu yol kullanilarak fotograf bitmap seklinde tutuluyor.
                Cursor cursorImgId = DBManager.fetchImgIdToUpload(picPath);
                cursorImgId.moveToFirst();
                int imgID = cursorImgId.getInt(0);
                uploadImage(picPath,imgID);
                cursorImgId.close();
                Bitmap myBitmap = BitmapFactory.decodeFile(picPath);
                ImageView fullImageView = new ImageView(getContext());
                fullImageView.setImageBitmap(myBitmap);
                


            });

            //Fotograflara uzun tiklayinca olacaklar.
            gridview.setOnItemLongClickListener((parent, view, position, id) ->
            {

                    Object sendItem = galleryAdapter.getItem(position);
                    String picPath = sendItem.toString();
                    AlertDialog.Builder builder = DialogHelper.alertBuilder(getActivity());
                    builder.setTitle("Akıllı Galeri");
                    builder.setMessage("Bu fotoğraf ile ne yapmak istiyorsunuz?");

                    builder.setNegativeButton(
                        "Sil",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(picPath);
                                DBManager.deleteImg(picPath);
                                boolean deleted = file.delete();
                                if(deleted){
                                    Toast toastIptal = Toast.makeText(getActivity(), "Fotograf başarıyla silindi.", Toast.LENGTH_LONG);
                                    toastIptal.show();

                                }
                                dialog.dismiss();
                            }
                        }
                    );

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
                        "Paylas",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO Paylasima ozelligi yapilabilir.
                                Toast toastPaylas = Toast.makeText(getContext(), "Henuz paylasim yok.", Toast.LENGTH_LONG);
                                toastPaylas.show();
                                dialog.dismiss();
                            }
                        }
                    );

                    builder.show();
                    return true;
            });

            galleryAdapter.setData(filesList);
            gridview.setAdapter(galleryAdapter);
            isGalleryInitalized = true;
        }
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
        uploadReceiver.register(Objects.requireNonNull(getActivity()));

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
            //noinspection ResultOfMethodCallIgnored
            directory.mkdir();
                    } else
        try {
            JSONObject responseObject = new JSONObject(new String(serverResponseBody));

            String processedImgPath= responseObject.getString("processedImg");
            String processedImgId= responseObject.getString("_id");
            JSONObject facesObject = responseObject.getJSONObject("faces");
            int responseLength = facesObject.length();
            JSONObject[] facesArray = new JSONObject[responseLength];
            for (int i =0;i<responseLength;i++)
            {
                facesArray[i] = facesObject.getJSONObject(String.valueOf(i));
                /*
                * JSON icerisinden dogruluk ve tahmin sonucu alinir.
                * Eger dogruluk orani 0.6'dan kucuk ise etiket -1 yani esigin altinda kalmis,
                * tanimlanamamis kisi olarkak etiketlernir.
                */
                double accu = facesArray[i].getDouble("csIdPer");
                String person = facesArray[i].getString("classId");
                if( Math.abs(1.0-accu) <= 0.40) {
                    person = "-1";
                }

                DBManager.insertFace(
                        facesArray[i].getString("_id"),
                        facesArray[i].getString("X"),
                        facesArray[i].getString("Y"),
                        person,
                        facesArray[i].getDouble("csIdPer"),
                        processedImgId
                );

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelled() {

    }

    private void sendtoProcess(){
    Cursor cursor = DBManager.fetchNonProcessed();
        cursor.moveToFirst();

        if(cursor.moveToFirst()){

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex("resimAdres"));
                DBManager.updateProccesStatus(path);

                //uploadImage(path,0);
            }
            cursor.close();
        }
    }

    //Fotografin upload edilmesi.
    private void uploadImage(String path, int position)
    {
        try
        {
            String uploadId = UUID.randomUUID().toString();
            uploadReceiver.setDelegate(this);
            uploadReceiver.setUploadID(uploadId);
            /*Upload URL is a static variable. May change*/
            String UPLOAD_URL = "http://212.253.48.98:3000/upload";
            String id = String.valueOf(position);
            new MultipartUploadRequest(Objects.requireNonNull(getActivity()), uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "uploadImage") //Adding file
                    .addParameter("imgId", id)//Adding text parameter to the request
                    .addParameter("userId", "45689")//Adding text parameter to the request
                    .setMaxRetries(2)
                    .startUpload();
        }
        catch (Exception ignored)
        { }
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
            int ID_COUNTER = 10000;
            return position+ID_COUNTER;
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

    @SuppressLint("StaticFieldLeak")
    class AsyncTaskRunner extends AsyncTask<String, String, String>
    {
    //1-Tum resimler veri tabanina kayit ediliyor.
        protected String doInBackground(String... times)
        {
            int ID_COUNTER = 10000;
            String resp = null;
            String[] itemsArray = new String[filesList.size()];
            itemsArray = filesList.toArray(itemsArray);
            for (int i = 0; i<filesList.size(); i++)
            {
                DBManager.insertImg(ID_COUNTER, itemsArray[i], 0, 0);
                ID_COUNTER++;
            }
            return null;
        }
        //AsyncTask tamamlandiktan sonra calisacak.
        @Override
        protected void onPostExecute(String result)
        {
            //Veritabanina yazildiktan sonra process edilmeye gonderilecek.
            sendtoProcess();
        }
        @Override
        protected void onPreExecute()
        {
        }
        @Override
        protected void onProgressUpdate(String... text)
        {
        }
    }
}