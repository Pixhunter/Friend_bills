package com.pixhunter.friendsbills.adapter;

import static com.pixhunter.friendsbills.Variables.db;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.pixhunter.friendsbills.entities.Bill;
import com.pixhunter.friendsbills.entities.Friend;
import com.pixhunter.friendsbills.MainActivity;
import com.pixhunter.friendsbills.R;

import java.util.ArrayList;

public class AdapterBill extends ArrayAdapter<Bill> {
    private final LayoutInflater inflater;
    private final int layout;
    private final ArrayList<Bill> productList;
    TextView textBill;
    Friend friend;

    public AdapterBill(Context context, int resource, ArrayList<Bill> products, TextView textBill, Friend friend) {
        super(context, resource, products);
        this.productList = products;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);

        this.textBill = textBill;
        this.friend = friend;

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
        final Bill product = productList.get(position);

        viewHolder.nameView.setText(product.getName());
        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float removeBill = Float.parseFloat(product.getName());
                var name = friend.getName();
                //db.execSQL("DELETE FROM ALL_BILLS_TABLE WHERE ALL_NAMES = \'" + name + "\'"
                //         +" and ALL_BILLS = " + removeBill + ";");


                db.execSQL("DELETE FROM ALL_BILLS_TABLE WHERE rowid = (SELECT rowid FROM ALL_BILLS_TABLE WHERE " +
                        " ALL_NAMES = \'"  + name + "\'"
                       + " and ALL_BILLS = " + removeBill + " );");

                Cursor cursor1 = db.rawQuery("SELECT * FROM ALL_BILLS_TABLE;", null);
                System.out.println("removeBill BILLS ---------------------------- ");
                if (cursor1.moveToFirst()) {
                    do {
                        String name0 = cursor1.getString(0);
                        var bbb = cursor1.getString(1);
                        System.out.println("----> " + name0 + " " + bbb);

                    } while (cursor1.moveToNext());
                }

                friend.updateBill(friend.getBill() - removeBill);
                textBill.setText(friend.getTextBill());
                remove(product);
                notifyDataSetChanged();
                friend.updateBillList(productList);
                MainActivity.updateTableResults();
            }

        });
        return convertView;
    }
    private static class ViewHolder {
        final Button  removeButton;
        final TextView nameView;
        ViewHolder(View view){
            removeButton = view.findViewById(R.id.removeButton);
            nameView = view.findViewById(R.id.nameView);
        }
    }

}
