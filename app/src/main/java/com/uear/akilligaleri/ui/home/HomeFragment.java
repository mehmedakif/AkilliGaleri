package com.uear.akilligaleri.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.uear.akilligaleri.DBManager;
import com.uear.akilligaleri.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {
    public static boolean isGalleryInitalized = false;
    private List<Integer> buttonIds = new ArrayList<>();
    private List<String> classIds = new ArrayList<>();

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

        LinearLayout linearLayout = root.findViewById(R.id.linlay);
        getNumberOfDistinctFaces();

        for(int i = 0; i < getCount(); i++)
        {
            try
            {
                Button newButton = new Button(getActivity());
                newButton.setId(R.id.button+i);
                buttonIds.add(newButton.getId());
                Log.i("BUTTON IDS",Integer.toString(newButton.getId()));
                // Since API Level 17, you can also use View.generateViewId()
                newButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
                newButton.setOnClickListener(this);
                newButton.setText(i +" NUMARALI KISI");
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

    private void getNumberOfDistinctFaces()
    {
        Cursor cursor =DBManager.fetchDistinctFaces();
        cursor.moveToFirst();
        try {
            while (cursor.moveToNext())
            {
                classIds.add(cursor.getString(0));

            }
        } finally
        {
            cursor.close();
        }
    }

    private int getCount()
    {
        int count=0;
        Cursor cursor = DBManager.fetchCountPerson();

        cursor.moveToFirst();
        if(cursor.moveToFirst()){

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count-1;
    }

    @Override
    public void onClick(View v)
    {
        Intent i = new Intent(getContext(), PersonPhotos.class);
        i.putExtra("PersonID", classIds.get(buttonIds.indexOf(v.getId())));
        startActivity(i);

        }



}
