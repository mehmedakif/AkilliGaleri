package com.uear.akilligaleri;
import com.loopj.android.http.*;
import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.uear.akilligaleri.ui.SingleUploadBroadcastReceiver;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.navigation.ui.AppBarConfiguration;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import net.gotev.uploadservice.MultipartUploadRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements SingleUploadBroadcastReceiver.Delegate
{
    String UPLOAD_URL = "http://81.214.177.75:3000/upload";
    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();
    @Override
    public Context getApplicationContext()
    {
        return super.getApplicationContext();
    }

    public static boolean isGalleryInitalized = false;
    private static final int REQUEST_PERMISSION = 1234;

    private static final String[] PERMISSION =
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET

            };

    //private AppBarConfiguration mAppBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /*public String toStringImage(Bitmap bmp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }*/

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

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object selectedItem = galleryAdapter.getItemId(position);


                            //galleryAdapterin dokunulan pozisyonundaki obje sendItem adiyla aliniyor.
                            // Bu obje fotografin yolunu tutuyor.
                            Object sendItem = galleryAdapter.getItem(position);

                            // Alinan obje yani fotografin yolu stringe ceviriliyor.
                            String picPath = sendItem.toString();

                            // bu yol kullanilarak fotograf bitmap seklinde tutuluyor.
                            Bitmap picBitmap = BitmapFactory.decodeFile(picPath);
                            //TODO Bu bitmap fotografin boyutlarini kucult
                            uploadImage(picPath);

                            // bitmap bicimdeki fotograf base64e donusturmek icin cagiriliyor.
                            //String base64 = convert(picBitmap);
                            //int width=picBitmap.getWidth();
                            //int height=picBitmap.getHeight();
                            //String preparedJson= JsonUtil.toJSon(base64,width,height);
                            //preparedJson = preparedJson.replaceAll("/","");
                            //preparedJson = preparedJson.replaceAll("//","");

/*Base64 to Bitmap donusumu icin bu yorumu kaldir.
                            Bitmap recover = ImageUtil.convert(base64);
                            ImageView mImg;
                            mImg = (ImageView) findViewById(R.id.imageView);
                            mImg.setImageBitmap(recover);
*/
                            /*try {

                                new UploadUtil().execute("http://81.214.177.75:3000/upload", preparedJson);
                                Toast toast = Toast.makeText(getApplicationContext(), "nice", Toast.LENGTH_LONG);
                                toast.show();
                            } catch (Exception  e) {
                                e.printStackTrace();
                            }*/

                            //uploadToServer(preparedJson);

                        }
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


            Toast toast = makeText(this, "basarili oldu +", Toast.LENGTH_LONG);
            toast.show();//Starting the upload


        } catch (Exception e) {
            Toast toast = makeText(this, "Basarisiz", Toast.LENGTH_LONG);
            toast.show();
        }

    }
    @Override
    public void onProgress(int progress) {
        //your implementation
    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {
        //your implementation
        Toast toast = makeText(this,"dudutduttdut",Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onError(Exception exception) {
        //your implementation
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        //your implementation
        String value = new String(serverResponseBody);
        Document doc = Jsoup.parse(value);
        //String title = doc.title();
        //Elements links = doc.getElementsByAttribute("img src");
        Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
        //String strImageURL = doc.val();
        //String lll = strImageURL.toString();
        //downloadImage(strImageURL);

        for (Element el : images) {
            //for each element get the srs url
            String src =el.attr("src");
            Toast toast = makeText(this,src,Toast.LENGTH_LONG);
            toast.show();
            downloadImageFromPath(src);
        }



        //toast2.show();
        //Drawable d = Drawable.createFromStream(new ByteArrayInputStream(serverResponseBody), null);
        //byte[] blob=c.getBlob("yourcolumnname");
       // Bitmap bmp=BitmapFactory.decodeByteArray(serverResponseBody,0,serverResponseBody.length);
        //ImageView image=findViewById(R.id.imageView);
        //image.setImageBitmap(bmp);
    }
    public void downloadImageFromPath(String path){
        InputStream in =null;
        Bitmap bmp=null;
        ImageView iv = (ImageView)findViewById(R.id.imageView);
        int responseCode = -1;
        try{

            URL url = new URL("http://81.214.177.75:3000/"+path);//"http://192.xx.xx.xx/mypath/img1.jpg
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
                iv.setImageBitmap(bmp);
            }

        }
        catch(Exception ex){
            Log.e("Exception",ex.toString());
        }
    }


    @Override
    public void onCancelled() {
        //your implementation
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
            Glide.with(MainActivity.this).load(data.get(position)).centerCrop().into(imageview);
            return imageview;
        }
    }
}

