package com.cf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cf.R;
import com.cf.pojo.BmpList;

import java.util.List;

public class BmpAdapter extends ArrayAdapter<BmpList> {
    private int resourceID;
    public BmpAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<BmpList> objects) {
        super(context, textViewResourceId, objects);
        resourceID = textViewResourceId;
    }
    // resourceID = textViewResourceId;
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BmpList bmpList = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceID, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView =view.findViewById(R.id.bmpimageview);
            viewHolder.textView = view.findViewById(R.id.bmptextview);
            view.setTag(viewHolder);  //将viewHolder储存起来，下次直接在缓存里面拿就行
        } else {
            view = convertView;   //启用缓存，增加效率
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageView.setImageBitmap(bmpList.getBitmap());
        viewHolder.textView.setText(bmpList.getBmpText());
        return view;
    }

     static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
