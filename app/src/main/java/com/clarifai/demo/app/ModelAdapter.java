package com.clarifai.demo.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sunny on 1/12/2018.
 */

public class ModelAdapter extends ArrayAdapter<Modelclass> {

    private String Name;
    private String Description;
    private int link;
    private Context t;
    public ModelAdapter(Activity context, ArrayList<Modelclass> arrayList){
        super(context, 0, arrayList);
        t=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View l = convertView;

        if (l == null) {
            l = LayoutInflater.from(getContext()).inflate(
                    R.layout.model_design,parent, false);
        }
        final Modelclass modelclass = getItem(position);
        Name = modelclass.getName();
        link = modelclass.getLink();
        Description = modelclass.getDescription();

        ImageView ModelImg = (ImageView) l.findViewById(R.id.ModelImg);
        TextView title = (TextView) l.findViewById(R.id.ModelTitle);
        TextView Desc = (TextView) l.findViewById(R.id.ModelDesc);

        ModelImg.setImageResource(link);
        title.setText(Name);
        Desc.setText(Description);

        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(t,ImgUpload.class);
                i.putExtra("model",modelclass);
                t.startActivity(i);
            }
        });

        return l;
    }
}
