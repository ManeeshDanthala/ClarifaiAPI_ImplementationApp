package com.clarifai.demo.app;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sunny on 1/12/2018.
 */

public class GenAdapter extends ArrayAdapter<HashMap<String,String>> {
    Context t;
    public GenAdapter(Activity context, ArrayList<HashMap<String,String>> GenarrayList){
        super(context,0,GenarrayList);
        t=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View l = convertView;

        if (l == null) {
            l = LayoutInflater.from(getContext()).inflate(
                    R.layout.commom_layout,parent, false);
        }

        HashMap<String,String> genHash = getItem(position);
        String name = genHash.get("name");
        String prob = genHash.get("prob");

        TextView a = (TextView) l.findViewById(R.id.com_head);
        TextView b = (TextView) l.findViewById(R.id.com_prob);

        a.setText(name);
        b.setText(prob);
        return l;
    }
}
