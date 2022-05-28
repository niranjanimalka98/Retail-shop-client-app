package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.jkdesigns.stsproject.Trans.act;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    ArrayList<String> personNames;
    ArrayList<String> personImages;
    Context context;
    Bitmap myBitmap;

    public CustomAdapter(Context context, ArrayList<String> personNames, ArrayList<String> personImages) {
        this.context = context;
        this.personNames = personNames;
        this.personImages = personImages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // set the data in items
        holder.name.setText(personNames.get(position));
        //if(personImages.get(position) != null) {
            myBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/products/" + personImages.get(position));
        /*}else{
            myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cancel);
        }*/
            //myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cancel);
        //myImage.setImageBitmap(myBitmap);
        holder.image.setImageBitmap(myBitmap);
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Trans.actLayout == null || Trans.actLayout != null) {
                    assert Trans.actLayout != null;
                    Trans.actLayout.setVisibility(View.VISIBLE);
                    Trans.act.setInputType(InputType.TYPE_NULL); // disable soft input
                    Trans.act.setFocusable(true);
                    Trans.act.setPressed(true);
                    Trans.act.setActivated(true);
                    Trans.act.setSelection(act.getText().length());
                    Trans.act.setText(personNames.get(position));
                    Trans.act.requestFocus();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return personNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView name;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.image);

        }
    }
}
