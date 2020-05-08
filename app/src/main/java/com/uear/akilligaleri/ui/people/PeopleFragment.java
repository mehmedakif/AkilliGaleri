package com.uear.akilligaleri.ui.people;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.uear.akilligaleri.R;

import java.util.Objects;

public class PeopleFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState)
    {
        PeopleViewModel peopleViewModel = ViewModelProviders.of(this).get(PeopleViewModel.class);
        View root = inflater.inflate(R.layout.fragment_people, container, false);



        GridView gridview = Objects.requireNonNull(getView()).findViewById(R.id.galleryGridView);
//final PeopleFragment.GalleryAdapter galleryAdapter = new GalleryAdapter();


        return root;
    }
//
//    public int getCount()
//    {
//        int count=0;
//        Cursor cursor = DBManager.fetchCountPerson();
//        cursor.moveToFirst();
//
//        if(cursor.moveToFirst()){
//
//            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
//                count = cursor.getInt(0);
//            }
//            cursor.close();
//        }
//        return count;
//    }
//
//    final class GalleryAdapter extends BaseAdapter
//    {
//        List<String> data = new ArrayList<>();
//
//        void setData(List<String> data) {
//            if (this.data.size() > 0) {
//                this.data.clear();
//            }
//            this.data.addAll(data);
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public int getCount() {
//            return data.size();
//        }
//        @Override
//        public Object getItem(int position) {
//
//            return data.get(position);
//        }
//        @Override
//        public long getItemId(int position) {
//            return position;}
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            final ImageView imageview;
//            if (convertView == null) {
//                imageview = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
//            } else imageview = (ImageView) convertView;
//            Glide.with(PeopleFragment.this).load(data.get(position)).centerCrop().into(imageview);
//            return imageview;
//        }
//    }
}