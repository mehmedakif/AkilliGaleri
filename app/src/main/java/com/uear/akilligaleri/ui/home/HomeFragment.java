package com.uear.akilligaleri.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.uear.akilligaleri.DBManager;
import com.uear.akilligaleri.R;

public class HomeFragment extends Fragment implements View.OnClickListener {
    public static boolean isGalleryInitalized = false;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        Toast toast = Toast.makeText(getActivity(),"HomeFragment",Toast.LENGTH_LONG);
        toast.show();


        //Button button1 = root.findViewById(R.id.button3);

        LinearLayout linearLayout = root.findViewById(R.id.linlay);
        for(int i = 0; i < 5; i++)
        {
            try
            {
                Button newButton = new Button(getActivity());
                newButton.setId(R.id.button+i);
                // Since API Level 17, you can also use View.generateViewId()
                newButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
                newButton.setOnClickListener(this);
                newButton.setText(Integer.toString(i)+" NUMARALI KISI");
                linearLayout.addView(newButton);
            }
            catch (Exception e)
            {
                // Unknown button id !
                // We skip it
            }
        }

        return root;
    }

    public int getCount()
    {
        int count=0;
        Cursor cursor = DBManager.fetchCountPerson();
        cursor.moveToFirst();


        if(cursor.moveToFirst()){

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                count = cursor.getInt(0);
                Toast toast = Toast.makeText(getActivity(),count,Toast.LENGTH_LONG);
                toast.show();
            }
            cursor.close();
        }
        return count;
    }

    @Override
    public void onClick(View v)
    {
        Intent i = new Intent(getContext(), PersonPhotos.class);
        switch (v.getId()) {

            case R.id.button1:
                i.putExtra("PersonID", "1");
                startActivity(i);
                break;

            case R.id.button2:
                i.putExtra("PersonID", "2");
                startActivity(i);
                break;
            case R.id.button3:
                i.putExtra("PersonID", "3");
                startActivity(i);
                break;
            case R.id.button4:
                i.putExtra("PersonID", "4");
                startActivity(i);
                break;
            case R.id.button5:
                i.putExtra("PersonID", "5");
                startActivity(i);
                break;
            case R.id.button6:
                i.putExtra("PersonID", "6");
                startActivity(i);
                break;

            default:
                i.putExtra("PersonID", "111000000000000000");
                startActivity(i);
                break;
        }
    }


}
