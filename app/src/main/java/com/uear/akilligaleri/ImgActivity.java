package com.uear.akilligaleri;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ImgActivity extends AppCompatActivity {
    EditText description_edit;
    EditText step_edit;
    Button create_button;
    public Context getApplicationContext()
    {
        return super.getApplicationContext();
    }
    public String value;
    public int step_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);

        DisplayMetrics display_metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display_metrics);
        int width = display_metrics.widthPixels;
        int height = display_metrics.heightPixels;

        this.getWindow().setLayout((int)(width*.7), (int)(height*.6));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -170;
        getWindow().setAttributes(params);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        byte[] byteArray = getIntent().getByteArrayExtra("path");
        assert byteArray != null;
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView image = findViewById(R.id.resultImageView);
        image.setImageBitmap(bitmap);


    }
    @Override
    public void onResume() {
        super.onResume();



    }
}
