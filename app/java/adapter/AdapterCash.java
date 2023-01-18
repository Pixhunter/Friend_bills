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

import com.pixhunter.friendsbills.entities.Cash;
import com.pixhunter.friendsbills.entities.Friend;
import com.pixhunter.friendsbills.MainActivity;
import com.pixhunter.friendsbills.R;

import java.util.ArrayList;

public class AdapterCash extends ArrayAdapter<Cash> {
    private final LayoutInflater inflater;
    private final int layout;
    private final ArrayList<Cash> productList;
    TextView textCash;
    Friend friend;

    public AdapterCash(Context context, int resource, ArrayList<Cash> products, TextView textCash, Friend friend) {
        super(context, resource, products);
        this.productList = products;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.textCash = textCash;
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
        final Cash product = productList.get(position);

        viewHolder.nameView.setText(product.getName());
        viewHolder.countView.setText(String.valueOf(product.getCount()));
        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float removeBill = Float.parseFloat(product.getName());
                var name = friend.getName();
                int count = product.getCount();

                if (product.getCount() != 0) {
                    friend.updateCash(friend.getCash() - removeBill);

                }

                if (product.getCount() <= 1) {
                    remove(product);
                    notifyDataSetChanged();
                    db.execSQL("DELETE FROM ALL_CASHES_TABLE WHERE ALL_NAMES = \'"  + name + "\'"
                            + " and ALL_CASHES = " + removeBill + " and ALL_COUNTS = " + count + ";");

                    Cursor cursor1 = db.rawQuery("SELECT * FROM ALL_BILLS_CASHES;", null);
                    System.out.println("remove CASH ---------------------------- ");
                    if (cursor1.moveToFirst()) {
                        do {
                            String name0 = cursor1.getString(0);
                            var bbb = cursor1.getString(1);
                            var bbb2 = cursor1.getString(2);
                            System.out.println("----> " + name0 + " " + bbb + " " + bbb2);

                        } while (cursor1.moveToNext());
                    }

                } else {
                    product.decCount();
                    count = product.getCount();
                    viewHolder.countView.setText(String.valueOf(count));
                    notifyDataSetChanged();

                    db.execSQL("UPDATE ALL_CASHES_TABLE SET ALL_COUNTS = " + count + " WHERE ALL_NAMES = \'"  + name + "\'"
                            + " and ALL_CASHES = " + removeBill + " ;");

                    Cursor cursor1 = db.rawQuery("SELECT * FROM ALL_CASHES_TABLE;", null);
                    System.out.println("remove CASH ---------------------------- ");
                    if (cursor1.moveToFirst()) {
                        do {
                            String name0 = cursor1.getString(0);
                            var bbb = cursor1.getString(1);
                            var bbb2 = cursor1.getString(2);
                            System.out.println("----> " + name0 + " " + bbb + " " + bbb2);

                        } while (cursor1.moveToNext());
                    }

                }
                friend.updateCashList(productList);
                textCash.setText(friend.getTextCash());
                MainActivity.updateTableResults();
            }
        });

        viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend.updateCash(friend.getCash() + Float.parseFloat(product.getName()));
                product.incCount();

                viewHolder.countView.setText(String.valueOf(product.getCount()));
                notifyDataSetChanged();

                if (product.getCount() > 1) {
                    db.execSQL("UPDATE ALL_CASHES_TABLE SET ALL_COUNTS = " + product.getCount() + " WHERE ALL_NAMES = \'" + friend.getName() + "\'"
                            + " and ALL_CASHES = " + product.getName() + " ;");
                } else {
                    db.execSQL("INSERT INTO ALL_CASHES_TABLE (ALL_NAMES, ALL_CASHES, ALL_COUNTS)"
                            + " VALUES (\'" + friend.getName() + "\', " + product.getName() + " , " + 1 + ");");
                }
                Cursor cursor1 = db.rawQuery("SELECT * FROM ALL_CASHES_TABLE;", null);
                System.out.println("add CASH ---------------------------- " + "ALL_COUNTS = " + product.getCount() + " " +  product.getName());
                if (cursor1.moveToFirst()) {
                    do {
                        String name0 = cursor1.getString(0);
                        var bbb = cursor1.getString(1);
                        var bbb2 = cursor1.getString(2);
                        System.out.println("----> " + name0 + " " + bbb + " " + bbb2);

                    } while (cursor1.moveToNext());
                }

                friend.updateCashList(productList);
                textCash.setText(friend.getTextCash());
                MainActivity.updateTableResults();
            }
        });

        return convertView;
    }
    private static class ViewHolder {
        final Button removeButton;
        final Button addButton;
        final TextView nameView;
        final TextView countView;
        ViewHolder(View view){
            removeButton = view.findViewById(R.id.removeButton);
            addButton = view.findViewById(R.id.addButton);
            nameView = view.findViewById(R.id.nameView);
            countView = view.findViewById(R.id.countView);

        }
    }

}
