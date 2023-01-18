package com.pixhunter.friendsbills.entities;

import static com.pixhunter.friendsbills.Variables.SQL_QUERY_CASHES;
import static com.pixhunter.friendsbills.Variables.SQL_QUERY_FRIEND_BILLS;
import static com.pixhunter.friendsbills.Variables.SQL_QUERY_FRIEND_CASHES;
import static com.pixhunter.friendsbills.Variables.SQL_QUERY_NAMES;
import static com.pixhunter.friendsbills.Variables.SQL_QUERY_SELECT_NAMES;
import static com.pixhunter.friendsbills.Variables.db;
import static com.pixhunter.friendsbills.Variables.friendNames;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.pixhunter.friendsbills.MainActivity;
import com.pixhunter.friendsbills.R;
import com.pixhunter.friendsbills.adapter.AdapterBill;
import com.pixhunter.friendsbills.adapter.AdapterCash;

import java.util.ArrayList;

/**
 * Fragment for dedicated friend, consist bill and cash fragments
 */
public class FriendFragment extends Fragment {

    private final String chet;
    private final Friend friend;
    public static String newNames = "";
    TextView textViewFriendName;
    Button updateButtonBill;
    Button updateButtonCash;
    TextView allBillTax;
    TextView allCashTax;
    ListView listBill;
    ListView listCash;
    View backView;
    Button buttonEditName;

    // default params for each fried fragment
    public FriendFragment(Friend friend, String chet, TabLayout.Tab tap) {
        super(R.layout.one_list_check);
        this.chet = chet;
        this.friend = friend;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateButtonBill = view.findViewById(R.id.buttonPlusBill);
        updateButtonCash = view.findViewById(R.id.buttonPlusCash);
        allBillTax = view.findViewById(R.id.allBillTax);
        allCashTax = view.findViewById(R.id.allCashTax);
        textViewFriendName = view.findViewById(R.id.textViewComment);
        listBill = view.findViewById(R.id.productListBill);
        listCash = view.findViewById(R.id.productListCash);
        backView = view.findViewById(R.id.pocket);
        buttonEditName = view.findViewById(R.id.buttonEditName);


        AdapterBill adapterBill = new AdapterBill(view.getContext(), R.layout.list_item,
                friend.getBillList(), allBillTax, friend);
        AdapterCash adapterCash = new AdapterCash(view.getContext(), R.layout.list_item_cash,
                friend.getCashList(), allCashTax, friend);

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(view.getContext(), getindexOfFriendImage(friendNames.indexOf(this.chet)));
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        int tabIconColor = ContextCompat.getColor(view.getContext(), R.color.tab_background_selected_transparent);
        DrawableCompat.setTint(wrappedDrawable, tabIconColor);

        backView.setBackground(wrappedDrawable);

        System.out.println("Friend name " + chet);
        TextView setName = view.findViewById(R.id.textViewFriend);
        setName.setText(chet);
        Cursor cursor1 = db.rawQuery(SQL_QUERY_SELECT_NAMES + "\'" + friend.getName() + "\';", null);
        System.out.println("SELECT new friend name: " + friend.getName() + " " + newNames );
        if (cursor1.moveToFirst()) {
            String newNameFriend = cursor1.getString(0);
            System.out.println("SELECT new friend name: " + friend.getName() + " " + newNames + " " + newNameFriend);
            textViewFriendName.setText(newNameFriend);
        }

        // show dialog window to change comment fpr friend
        buttonEditName.setOnClickListener(this::showAlertDialogButtonClicked);

        Cursor cursor = db.rawQuery(SQL_QUERY_FRIEND_CASHES + "\'" + friend.getName() + "\';", null);

        // for new friend setup default cashes
        if ((friend.getCashList().isEmpty()) && (cursor.getCount() == 0)) {
            friend.getCashList().add(new Cash("50", 0, new Friend(0)));
            friend.getCashList().add(new Cash("100", 0, new Friend(0)));
            friend.getCashList().add(new Cash("500", 0, new Friend(0)));
            friend.getCashList().add(new Cash("1000", 0, new Friend(0)));
            friend.updateCashList(friend.getCashList());
        }

        listBill.setAdapter(adapterBill);
        allBillTax.setText(friend.getTextBill());
        EditText newBill = view.findViewById(R.id.editBill);
        updateButtonBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    float newBillValue = Float.parseFloat(newBill.getText().toString());

                    var name = friend.getName();
                    db.execSQL("INSERT INTO ALL_BILLS_TABLE (ALL_NAMES, ALL_BILLS)"
                            + " VALUES (\'" + name + "\', " + newBillValue + ");");

                    Cursor cursor1 = db.rawQuery("SELECT * FROM ALL_BILLS_TABLE;", null);
                    System.out.println("ADD ALL BILLS");
                    if (cursor1.moveToFirst()) {
                        do {
                            String name0 = cursor1.getString(0);
                            var bbb = cursor1.getString(1);
                            System.out.println("----> " + name0 + " " + bbb);

                        } while (cursor1.moveToNext());
                    }

                    friend.getBillList().add(new Bill(String.valueOf(newBillValue), friend));
                    friend.updateBill(friend.getBill() + newBillValue);
                    allBillTax.setText(friend.getTextBill());

                    adapterBill.notifyDataSetChanged();
                    friend.updateBillList(friend.getBillList());
                    MainActivity.updateTableResults();
                } catch (NumberFormatException e) {
                    System.out.println("can't parse bill -> NumberFormatException: " + e);
                }
            }
        });

        listCash.setAdapter(adapterCash);
        allCashTax.setText(friend.getTextCash());
        EditText newCash = view.findViewById(R.id.editCash);
        updateButtonCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    float newCashValue = Float.parseFloat(newCash.getText().toString());
                    int countOP = 0;

                    ArrayList<Cash> oldCashList = friend.getCashList();
                    int duplex = -1;
                    for (int i = 0; i < oldCashList.size(); i++) {
                        System.out.println("---> " + oldCashList.get(i).getName()
                                + " " + newCashValue);
                        float oldVal = Float.parseFloat(oldCashList.get(i).getName());
                        if (oldVal == newCashValue) {
                            duplex = i;
                            break;
                        }
                    }
                    var name = friend.getName();
                    if (duplex == -1) {
                        oldCashList.add(new Cash(String.valueOf(newCashValue), 1, friend));
                        db.execSQL("INSERT INTO ALL_CASHES_TABLE (ALL_NAMES, ALL_CASHES, ALL_COUNTS)"
                                + " VALUES (\'" + name + "\', " + newCashValue + " , " + 1 + ");");
                    } else {
                        oldCashList.get(duplex).incCount();
                        countOP = oldCashList.get(duplex).getCount();
                        db.execSQL("UPDATE ALL_CASHES_TABLE SET ALL_COUNTS = " + countOP + " WHERE ALL_NAMES = \'"  + name + "\'"
                                + " and ALL_CASHES = " + newCashValue + " ;");
                    }

                    Cursor cursor1 = db.rawQuery(SQL_QUERY_CASHES, null);
                    System.out.println("add ALL BILLS CASHES");
                    if (cursor1.moveToFirst()) {
                        do {
                            String name0 = cursor1.getString(0);
                            var bbb = cursor1.getString(1);
                            var bbb2 = cursor1.getString(2);
                            System.out.println("----> " + name0 + " " + bbb + " " + bbb2);

                        } while (cursor1.moveToNext());
                    }
                    friend.updateCash(friend.getCash() + newCashValue);
                    allCashTax.setText(friend.getTextCash());

                    adapterCash.notifyDataSetChanged();
                    friend.updateCashList(oldCashList);
                    MainActivity.updateTableResults();
                } catch (NumberFormatException e) {
                    System.out.println("can't parse cash -> NumberFormatException: " + e);
                }
            }
        });

        setDefaultBills(allBillTax, adapterBill);
        setDefaultCashes(allCashTax, adapterCash);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.one_list_check, container, false);
    }

    public void setDefaultBills(TextView allBillTax, AdapterBill adapterBill) {
        try {
            String sql = SQL_QUERY_FRIEND_BILLS + "\'" + friend.getName() + "\';";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.getCount() > friend.getBillList().size()) {
                if (cursor.moveToFirst()) {
                    do {
                        float newBillValue = Float.parseFloat(cursor.getString(1));
                        friend.getBillList().add(new Bill(String.valueOf(newBillValue), friend));
                        friend.updateBill(friend.getBill() + newBillValue);
                        allBillTax.setText(friend.getTextBill());

                        adapterBill.notifyDataSetChanged();
                        friend.updateBillList(friend.getBillList());
                        MainActivity.updateTableResults();

                    } while (cursor.moveToNext());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("can't parse bill -> NumberFormatException: " + e);
        }
    }
    public void setDefaultCashes(TextView allCashTax, AdapterCash adapterCash) {
        try {
            Cursor cursor = db.rawQuery(SQL_QUERY_FRIEND_CASHES + "\'" + friend.getName() + "\';", null);
            System.out.println("set result CASHES");
            ArrayList<Cash> oldCashList = friend.getCashList();
            if (cursor.getCount() > friend.getCashList().size()) {
                float newCashValue;
                if (cursor.moveToFirst()) {
                    do {
                        newCashValue = Float.parseFloat(cursor.getString(1));
                        oldCashList.add(new Cash(String.valueOf(newCashValue), Integer.parseInt(cursor.getString(2)), friend));

                    } while (cursor.moveToNext());
                    friend.updateCash(friend.getCash() + newCashValue);
                    allCashTax.setText(friend.getTextCash());

                    adapterCash.notifyDataSetChanged();
                    friend.updateCashList(oldCashList);
                    MainActivity.updateTableResults();
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("can't parse bill -> NumberFormatException: " + e);
        }
    }
    public void showAlertDialogButtonClicked(View view)
    {
        // Create an alert builder
        AlertDialog.Builder builder
                = new AlertDialog.Builder(view.getContext(), R.style.AlarmStyle);
        builder.setTitle("Add new comment")
                .setMessage("\nAdd a comment to uniquely identify\nwho the animal is")
                .setIcon(R.drawable.ic_action_monkey);

        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(R.layout.custom_dialog,
                        null);
        builder.setView(customLayout);

        // add a button
        builder.setPositiveButton(
                        "SAVE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // send data from the
                                // AlertDialog to the Activity
                                EditText editText = customLayout .findViewById( R.id.imputComment);

                                newNames = editText.getText().toString();
                                textViewFriendName.setText(newNames);
                                System.out.println("new friend name " + newNames);

                                Cursor cursor1 = db.rawQuery(SQL_QUERY_NAMES + "\'" + friend.getName() + "\';", null);
                                if (!cursor1.moveToFirst()) {
                                    db.execSQL("INSERT INTO ALL_NAMES_TABLE (ALL_NAMES, ALL_NEW_NAMES)"
                                            + " VALUES (\'" + friend.getName() + "\', \'" + newNames + "\');");
                                    System.out.println("INSERT new friend name " + friend.getName() + " " + newNames);
                                } else {
                                    db.execSQL("UPDATE ALL_NAMES_TABLE SET ALL_NEW_NAMES = \'" + newNames + "\' WHERE ALL_NAMES = \'" + friend.getName() + "\';");
                                    System.out.println("UPDATE new friend name " + friend.getName() + " " + newNames);
                                }
                            }
                        });
        builder.setNegativeButton("CANCEL", null);

        // create and show
        // the alert dialog
        AlertDialog dialog= builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.tab_background_selected));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.tab_background_selected));
    }

    public static int getindexOfFriendImage(int index) {
        switch (index) {
            case 0: return R.drawable.ic_action_monkey;
            case 1: return R.drawable.ic_action_frog;
            case 2: return R.drawable.ic_action_yu;
            case 3: return R.drawable.ic_action_cat;
            case 4: return R.drawable.ic_action_fox;
            case 5: return R.drawable.ic_action_elefant;
            case 6: return R.drawable.ic_action_racoon;
            case 7: return R.drawable.ic_action_lion;
            case 8: return R.drawable.ic_action_wolf;
            case 9: return R.drawable.ic_action_walrus;
            case 10: return R.drawable.ic_action_pig;
            case 11: return R.drawable.ic_action_goat;
            case 12: return R.drawable.ic_action_giraffe;
            case 13: return R.drawable.ic_action_deer;
            case 14: return R.drawable.ic_action_guena;
            case 15: return R.drawable.ic_action_panda;
            case 16: return R.drawable.ic_action_mushroom;
            case 17: return R.drawable.ic_action_octopus;
            case 18: return R.drawable.ic_action_crab;
            case 19: return R.drawable.ic_action_shark;
            default: return R.drawable.ic_action_monkey;
        }
    }
}