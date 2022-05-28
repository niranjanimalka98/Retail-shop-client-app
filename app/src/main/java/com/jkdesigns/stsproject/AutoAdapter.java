package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AutoAdapter extends ArrayAdapter<Model> {

    Context context;
    int resource, textViewResourceId;
    ArrayList<Model> items, tempItems, suggestions;
    String itemen = "", item = "", p8 = "", unit = "", expdate = "", batch = "", key = "", oqty, p1 = "", p6 = "", p7 = "", p11 = "", p4 = "", p18 = "", p19 = "", p20 = "", p21 = "", itemp3, str;

    public AutoAdapter(Context context, int resource, int textViewResourceId, ArrayList<Model> items) {

        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        this.tempItems = (ArrayList<Model>) items.clone();
        this.suggestions = new ArrayList<Model>();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autotext_list, parent, false);
        }
        final Model model = items.get(position);
        if (model != null) {

            String current = Locale.getDefault().getDisplayLanguage();
            if (current.toLowerCase().contains("en")) {
                itemp3 = model.getP03();
            }else{
                itemp3 = model.getP03t();
            }
            TextView item = (TextView) view.findViewById(R.id.item_name);

            if (item != null)

                item.setText( "(" + model.getP01() + ") "+ itemp3  + '\n' + "Qty: " + model.getP10() + " || Rs: " + model.getP08());
        }
        return view;
    }

    @Override
    public Filter getFilter() {

        return nameFilter;
    }

    Filter nameFilter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String current = Locale.getDefault().getDisplayLanguage();
            if (current.toLowerCase().contains("en")) {
                str = ((Model) resultValue).getP03() + " (" +((Model) resultValue).getP01() + ")";
                item = ((Model) resultValue).getP03();
                itemen = ((Model) resultValue).getP03t();
            }else{
                str = ((Model) resultValue).getP03t() + " (" +((Model) resultValue).getP01() + ")";
                item = ((Model) resultValue).getP03();
                itemen = ((Model) resultValue).getP03t();
            }

            p8 = ((Model) resultValue).getP08();
            expdate = ((Model) resultValue).getP05();
            batch = ((Model) resultValue).getP02();
            key = ((Model) resultValue).getP01();
            oqty = ((Model) resultValue).getP10();
            unit = ((Model) resultValue).getP09();
            p6 = ((Model) resultValue).getP06();
            p7 = ((Model) resultValue).getP07();
            p11 = ((Model) resultValue).getP11();
            p4 = ((Model) resultValue).getP04();
            p18 = ((Model) resultValue).getP18();
            p19 = ((Model) resultValue).getP19();
            p20 = ((Model) resultValue).getP20();
            p21 = ((Model) resultValue).getP21();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Model model : tempItems) {
                    String current = Locale.getDefault().getDisplayLanguage();
                    if (current.toLowerCase().contains("en")) {
                        if (model.getP03().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(model);
                        }
                    }else{
                        if (model.getP03t().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(model);
                        }
                    }

                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Model> filterList = (ArrayList<Model>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Model model : filterList) {
                    add(model);
                    notifyDataSetChanged();
                }
            }
        }
    };

    public String[] det() {
        String current = Locale.getDefault().getDisplayLanguage();
        if (current.toLowerCase().contains("en")) {
            String[] str = { item, itemen, p8, batch, expdate, key, oqty, unit, p6, p7, p11, p4, p18, p19, p20, p21 };
            return str;
        }else{
            String[] str = { itemen, item, p8, batch, expdate, key, oqty, unit, p6, p7, p11, p4, p18, p19, p20, p21 };
            return str;
        }

    }
}
