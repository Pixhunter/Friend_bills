package com.pixhunter.friendsbills.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pixhunter.friendsbills.R;

import java.util.Collections;

public class AdapterResult extends ArrayAdapter {
    private final LayoutInflater inflater;
    private final int layout;
    private final String friendList;
    private final String cashList;
    private final String billdList;
    private final String delta;

    public AdapterResult(Context context, int resource, String products, String billdList, String cashList, String delta) {
        super(context, resource, Collections.singletonList(products));
        this.friendList = products;
        this.billdList = billdList;
        this.cashList = cashList;
        this.delta = delta;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.friendView.setText(friendList);
        viewHolder.billView.setText(billdList);
        viewHolder.cashView.setText(cashList);
        viewHolder.deltaView.setText(delta);

        return convertView;
    }
    private static class ViewHolder {

        final TextView friendView;
        final TextView billView;
        final TextView cashView;
        final TextView deltaView;
        ViewHolder(View view){

            friendView = view.findViewById(R.id.listViewNames);
            billView = view.findViewById(R.id.listViewBills);
            cashView = view.findViewById(R.id.listViewCash);
            deltaView = view.findViewById(R.id.listViewDelta);
        }
    }

}
