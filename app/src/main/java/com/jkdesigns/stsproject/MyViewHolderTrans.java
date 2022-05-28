package com.jkdesigns.stsproject;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyViewHolderTrans extends RecyclerView.ViewHolder {

    TextView trans, date, money, custname, payment, time, discount;
    ImageView delbtn;
    LinearLayout gone;
    CardView cv;

    public MyViewHolderTrans(View itemView) {
        super(itemView);

        cv = (CardView) itemView.findViewById(R.id.cv);

        trans = (TextView) itemView.findViewById(R.id.id);
        date = (TextView) itemView.findViewById(R.id.date);
        time = (TextView) itemView.findViewById(R.id.time);
        money = (TextView) itemView.findViewById(R.id.money);
        custname = (TextView) itemView.findViewById(R.id.custname);
        payment = (TextView) itemView.findViewById(R.id.payment);
        discount = (TextView) itemView.findViewById(R.id.distext);

        delbtn = (ImageView) itemView.findViewById(R.id.deletebutton);
    }
}
