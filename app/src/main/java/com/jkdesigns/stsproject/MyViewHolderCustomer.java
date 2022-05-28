package com.jkdesigns.stsproject;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyViewHolderCustomer extends RecyclerView.ViewHolder {

    TextView item, date, money, b3, phone;
    ImageView editBtn, delbtn, locbutton;
    Button update, log;
    LinearLayout gone;
    CardView cv;

    public MyViewHolderCustomer(View itemView) {
        super(itemView);

        cv = (CardView) itemView.findViewById(R.id.cv);
        gone = (LinearLayout) itemView.findViewById(R.id.gone_layout);

        item = (TextView) itemView.findViewById(R.id.name);
        date = (TextView) itemView.findViewById(R.id.date);
        money = (TextView) itemView.findViewById(R.id.money);
        b3 = (TextView) itemView.findViewById(R.id.b3);
        phone = (TextView) itemView.findViewById(R.id.phone);

        editBtn = (ImageView) itemView.findViewById(R.id.editbutton);
        delbtn = (ImageView) itemView.findViewById(R.id.deletebutton);
        update = (Button) itemView.findViewById(R.id.updatebtn);
        log = (Button) itemView.findViewById(R.id.logbtn);
        locbutton = (ImageView) itemView.findViewById(R.id.locbutton);
    }
}
