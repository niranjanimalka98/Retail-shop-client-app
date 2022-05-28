package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<ListModel> {

    String c1;
    double d4;
    Context context;
    ArrayList<ListModel> listModels = new ArrayList<>();
    int res;
    DecimalFormat dc;

    public ListAdapter(Context context, int resource, ArrayList<ListModel> listModels) {
        super(context, resource, listModels);
        this.res = resource;
        this.listModels = listModels;
        this.context = context;
    }

    private class ViewHolder {

        TextView med, pr, amt;
        Button del, qty;
    }

    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder = null;
        final ListModel model = getItem(position);
        dc = new DecimalFormat(".00");

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(res, null);
            holder = new ViewHolder();
            holder.med = (TextView) convertView.findViewById(R.id.med);
            holder.pr = (TextView) convertView.findViewById(R.id.pr);
            holder.qty = (Button) convertView.findViewById(R.id.qty);
            holder.amt = (TextView) convertView.findViewById(R.id.amt);
            holder.del = (Button) convertView.findViewById(R.id.btndel);
            convertView.setTag(holder);

        } else
            holder = (ViewHolder) convertView.getTag();

        Double up = Double.parseDouble(model.getPr()) / Double.parseDouble(model.getUnit());
        holder.pr.setText(up + "");
        holder.qty.setText(model.getQty());
        holder.amt.setText(model.getAmount());
        final String a = holder.amt.getText().toString();
        final String p = holder.pr.getText().toString();
        final String p21 = model.getP21();//Discount amount
        final String p20 = model.getP20();//condition in inventory p21

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Trans.totamount != 0) {
                    Trans.totamount = Trans.totamount - Double.valueOf(a);
                    Trans.totamount = Double.parseDouble(dc.format(Trans.totamount));
                    Trans.totamt.setText(String.valueOf(Trans.totamount));
                    Trans.damount = (Trans.damount - Double.valueOf(a)) + Double.valueOf(p21);
                    Trans.due.setText(String.valueOf(Trans.damount));
                    Trans.d3 = Trans.d3 - Double.valueOf(p21);
                    Trans.discount.setText(String.valueOf(Trans.d3));
                    String rep = Trans.SList.getText().toString().replace(model.getKey()+"-","");
                    Trans.SList.setText(rep);
                }else if(Returns.totamount != 0) {
                    Returns.totamount = Returns.totamount - Double.valueOf(a);
                    Returns.totamount = Double.parseDouble(dc.format(Returns.totamount));
                    Returns.totamt.setText(String.valueOf(Returns.totamount));
                    Returns.damount = (Returns.damount - Double.valueOf(a)) + Double.valueOf(p21);
                    Returns.due.setText(String.valueOf(Returns.damount));
                    Returns.d3 = Returns.d3 - Double.valueOf(p21);
                    Returns.discount.setText(String.valueOf(Returns.d3));
                    String rep = Returns.SList.getText().toString().replace(model.getKey()+"-","");
                    Returns.SList.setText(rep);
                    /*Returns.totamount = Returns.totamount - Double.valueOf(a);
                    Returns.totamount = Double.parseDouble(dc.format(Returns.totamount));
                    Returns.totamt.setText(String.valueOf(Returns.totamount));
                    Returns.due.setText(String.valueOf(Returns.totamount));
                    String rep = Returns.SList.getText().toString().replace(model.getKey()+"-","");
                    Returns.SList.setText(rep);*/
                }
                //Trans.gen.setText("GENERATE BILL");
                remove(getItem(position));
                notifyDataSetChanged();
                ListView lv = (ListView) parent.findViewById(R.id.list);
                Utility.setListViewHeightBasedOnChildren(lv);
            }
        });

        final ViewHolder finalHolder = holder;
        final ViewHolder finalHolder1 = holder;

        holder.qty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.input_dia, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);

                alertDialogBuilder.setCancelable(false).setPositiveButton(R.string.trans_set, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String qt = editText.getText().toString().trim();

                        if (!qt.equals("") && !qt.equals("0")) {
                            if (Integer.parseInt(qt) <= Integer.parseInt(model.getOrgqty())) {
                                Double u = Double.valueOf(model.getUnit());
                                double am = 0;
                                double dm = 0;
                                if(p20 != null) {
                                    String[] condition = p20.split("\\|");
                                    c1 = condition[0];
                                    int c2 = Integer.parseInt(condition[1]);
                                    double c3 = Double.parseDouble(condition[2]);

                                    if(c1.equals(">=")) {
                                        if(Integer.parseInt(qt) >= c2) {

                                            d4 = Integer.parseInt(qt) * c3;
                                            am = Double.valueOf(qt) * Double.valueOf(p);
                                            dm = Double.valueOf(qt) * Double.valueOf(p) - d4;
                                        }else {
                                            d4 = 0.00;
                                            am = Double.valueOf(qt) * Double.valueOf(p);
                                            dm = Double.valueOf(qt) * Double.valueOf(p);
                                        }
                                    }else{
                                        d4 = 0.00;
                                        am = Double.valueOf(qt) * Double.valueOf(p);
                                        dm = Double.valueOf(qt) * Double.valueOf(p);
                                    }

                                }else{
                                    d4 = 0.00;
                                    am = Double.valueOf(qt) * Double.valueOf(p);
                                    dm = Double.valueOf(qt) * Double.valueOf(p);
                                }

                                am = Double.parseDouble(dc.format(am));
                                dm = Double.parseDouble(dc.format(dm));
                                if(Trans.totamount != 0) {
                                    Trans.totamount = (Trans.totamount - Double.valueOf(a)) + am;
                                    Trans.totamount = Double.parseDouble(dc.format(Trans.totamount));
                                    model.setQty(qt);
                                    model.setAmount(String.valueOf(am));
                                    Trans.totamt.setText(String.valueOf(Trans.totamount));
                                    Trans.damount = (Trans.damount - Double.valueOf(a)) + dm;
                                    Trans.damount = Double.parseDouble(dc.format(Trans.damount));
                                    Trans.due.setText(String.valueOf(Trans.damount));
                                    Trans.d3 = (Trans.d3 - Double.valueOf(p21)) + d4;
                                    Trans.discount.setText(String.valueOf(Trans.d3));
                                } else if(Returns.totamount != 0) {
                                    Returns.totamount = (Returns.totamount - Double.valueOf(a)) + am;
                                    Returns.totamount = Double.parseDouble(dc.format(Returns.totamount));
                                    model.setQty(qt);
                                    model.setAmount(String.valueOf(am));
                                    Returns.totamt.setText(String.valueOf(Returns.totamount));
                                    Returns.due.setText(String.valueOf(Returns.totamount));
                                }
                                //Trans.gen.setText("GENERATE BILL");
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            else {
                                Toast.makeText(context, R.string.trans_qtynotinstock, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, R.string.trans_validqty, Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton(R.string.trans_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                alert.show();
            }
        });

        if(Trans.totamount != 0) {
            if (!p21.equals("0.00") || !p21.isEmpty()) {
                holder.med.setText(model.getMed() + "(Dis." + Double.parseDouble(p21) + ")");
            } else if (p20 != null) {
                if (!p20.contains("Stop")) {
                    holder.med.setText(model.getMed() + "(Dis." + d4 + ")");
                } else {
                    holder.med.setText(model.getMed());
                }
            } else {
                holder.med.setText(model.getMed());
            }
        }else if(Returns.totamount != 0) {
            if (!p21.equals("0.00") || !p21.isEmpty()) {
                holder.med.setText(model.getMed() + "(Dis." + Double.parseDouble(p21) + ")");
            } else if (p20 != null) {
                if (!p20.contains("Stop")) {
                    holder.med.setText(model.getMed() + "(Dis." + d4 + ")");
                } else {
                    holder.med.setText(model.getMed());
                }
            } else {
                holder.med.setText(model.getMed());
            }
        }

        return convertView;
    }

    public double roundOff(double d) {
        d = Math.round(d * 100.00);
        d = d / 100.00;
        return d;
    }
}
