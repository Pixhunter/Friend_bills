package com.pixhunter.friendsbills;

import static com.pixhunter.friendsbills.Variables.DB_NAME;
import static com.pixhunter.friendsbills.Variables.SQL_QUERY_NAME_BILLS;
import static com.pixhunter.friendsbills.Variables.db;
import static com.pixhunter.friendsbills.Variables.friendNames;
import static com.pixhunter.friendsbills.Variables.progressChangedValue;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.pixhunter.friendsbills.adapter.AdapterResult;
import com.pixhunter.friendsbills.entities.Friend;
import com.pixhunter.friendsbills.entities.FriendFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 *  Schema of dependencies of application activities
 *  where main activity is Main Activity
 *
 *            Main Activity
 *         ________|_______
 *        |               |
 *      Pack        Friend Fragment
 *                        |
 *                     ___|____
 *                    |       |
 *                  Bill    Cash
 */
public class MainActivity extends AppCompatActivity {
    public static List<String> friendNamesAction = new ArrayList<>();
    public static ArrayList<Friend> productsFried = new ArrayList<>();

    int chet = 0;

    FrameLayout simpleFrameLayout;

    SeekBar seekBar;
    TextView seekBarValue;
    Button newFriend;
    Button deleteFriend;
    ImageButton imageButton;

    public static TextView allFriends;
    public static TextView allFriends2;
    public static TextView allFriends3;
    public static TextView allFriends4;
    public static ListView productList;
    TabLayout tabLayout;

    Stack<Integer> animalsFree = new Stack<>();
    Stack<Integer> animalsSetup = new Stack<>();
    static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("MainActivity on create");

        /** Read the database from resource directory
         *  Create a Database if not exist
         *
         *  TABLES - no foreign keys
         *  ALL_NAMES_TABLE     (ALL_NAMES | ALL_NEW_NAMES )
         *  ALL_BILLS_TABLE     (ALL_NAMES | ALL_BILLS     )
         *  ALL_CASHES_TABLE    (ALL_NAMES | ALL_CASHES    | ALL_COUNTS)
         *
         *
         */
        try {
            db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "ALL_BILLS_TABLE"
                    + " (ALL_NAMES VARCHAR(15), ALL_BILLS float);");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "ALL_CASHES_TABLE"
                    + " (ALL_NAMES VARCHAR(15), ALL_CASHES float, ALL_COUNTS int);");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "ALL_NAMES_TABLE"
                    + " (ALL_NAMES VARCHAR(15), ALL_NEW_NAMES VARCHAR(15));");


            // Get all existing names from bills table
            Cursor namesFromBills = db.rawQuery("SELECT DISTINCT ALL_NAMES FROM ALL_BILLS_TABLE;", null);
            namesFromBills.moveToFirst();

            if (namesFromBills.moveToFirst()) {
                do {
                    animalsSetup.add(friendNames.indexOf(namesFromBills.getString(0)));
                } while (namesFromBills.moveToNext());
            }

            Cursor cursor2 = db.rawQuery("SELECT DISTINCT ALL_NAMES FROM ALL_CASHES_TABLE;", null);

            if (cursor2.moveToFirst()) {
                do {
                    if (!animalsSetup.contains(friendNames.indexOf(cursor2.getString(0)))) {
                        animalsSetup.add(friendNames.indexOf(cursor2.getString(0)));
                    }
                } while (cursor2.moveToNext());
            }

            Cursor cursor3 = db.rawQuery("SELECT ALL_NAMES FROM ALL_NAMES_TABLE;", null);

            if (cursor3.moveToFirst()) {
                do {
                    if (!animalsSetup.contains(friendNames.indexOf(cursor3.getString(0)))) {
                        animalsSetup.add(friendNames.indexOf(cursor3.getString(0)));
                    }
                } while (cursor3.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        }


        Cursor cursor = db.rawQuery(SQL_QUERY_NAME_BILLS, null);
        System.out.println("SQL initialization BILLS ---------------------------- ");
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                System.out.println("----> " + name);
                if (!friendNamesAction.contains(name)) {
                    System.out.println("friendNamesAction have no name - " + name);
                    friendNamesAction.add(name);
                    if (friendNames.contains(name)) {
                        System.out.println("friendNames have name - " + name);
                        int index = friendNames.indexOf(name);
                    }
                }

            } while (cursor.moveToNext());
        }

        for (int i = friendNames.size() - 1; i >= 0; i--) {
            if ((!animalsFree.contains(i)) && (!animalsSetup.contains(i))) {
                animalsFree.add(i);
            }
        }

        context = this;
        allFriends = (TextView) findViewById(R.id.friendsNameView);
        allFriends2 = (TextView) findViewById(R.id.billsView);
        allFriends3 = (TextView) findViewById(R.id.cashNeedView);
        allFriends4 = (TextView) findViewById(R.id.changeView);

        simpleFrameLayout = (FrameLayout) findViewById(R.id.simpleFrameLayout);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        productList = findViewById(R.id.listViewNames);

        AdapterResult adapterAllResult = new AdapterResult(context,
                R.layout.list_friend, " ", "", "", "");

        productList.setAdapter(adapterAllResult);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                System.out.println("--> " + tab.getText() + " on tab select");


                int pos = tab.getPosition();

                System.out.println("-----> list friend size: " + productsFried.size() + " total count: " + chet);
                if ((productsFried.size() < chet) || (productsFried.size() == 0) || (productsFried.size() == pos)) {
                    System.out.println("Add new friend");
                    productsFried.add(new Friend(pos));
                }

                productsFried.get(pos).setName(tab.getText().toString());
                System.out.println("-----> " + productsFried.size() + "pos:" + pos + " total count: " + chet);

                Fragment fragment = new FriendFragment(productsFried.get(pos), tab.getText().toString(), tab);

                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.white);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.simpleFrameLayout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();

                updateTableResults();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.black);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.black);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBarValue = (TextView) findViewById(R.id.textSeek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarValue.setText(progressChangedValue + " %");
                updateTableResults();

            }
        });

        imageButton = (ImageButton) findViewById(R.id.infoButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               InformationDialogFragment dialog = new InformationDialogFragment();
                                               dialog.show(getSupportFragmentManager(), "custom");
                                           }
                                       }
        );
        newFriend = (Button) findViewById(R.id.buttonNewFriend);
        newFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!animalsFree.isEmpty()) {
                    TabLayout.Tab thirdTab = tabLayout.newTab();
                    String animal = null;
                    int ccc = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Collections.shuffle(animalsFree);

                        if (animalsFree.isEmpty()) {
                            return;
                        }
                        System.out.println("--Animals---> " + animalsFree.toString());
                        ccc = animalsFree.pop();
                        animal = friendNames.get(ccc);

                    }
                    thirdTab.setText(animal);
                    thirdTab.setIcon(FriendFragment.getindexOfFriendImage(ccc));

                    //set tabs text color
                    tabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#FFFFFF"));
                    tabLayout.addTab(thirdTab, true);

                    updateTableResults();
                    chet++;
                }
            }
        });
        deleteFriend = (Button) findViewById(R.id.buttonDeleteFriend);
        deleteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currenttabNum = tabLayout.getSelectedTabPosition();
                if (tabLayout.getSelectedTabPosition() != -1) {
                    System.out.println("DELETE ---->   " + tabLayout.getSelectedTabPosition());


                    TabLayout.Tab tab = tabLayout.getTabAt(currenttabNum);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        animalsFree.add(friendNames.indexOf(tab.getText()));
                        System.out.println("add to free animals ---->   " + friendNames.indexOf(tab.getText())
                                + " " + friendNames.get(friendNames.indexOf(tab.getText())));
                    }
                    tab.getText();

                    db.execSQL("DELETE FROM ALL_BILLS_TABLE WHERE " +
                            " ALL_NAMES = \'" + tab.getText() + "\' ;");
                    db.execSQL("DELETE FROM ALL_CASHES_TABLE WHERE " +
                            " ALL_NAMES = \'" + tab.getText() + "\' ;");

                    productsFried.remove(tabLayout.getSelectedTabPosition());
                    tabLayout.removeTabAt(tabLayout.getSelectedTabPosition());

                    updateTableResults();

                    Cursor cursor1 = db.rawQuery("SELECT * FROM ALL_BILLS_TABLE;", null);
                    System.out.println("removeBill BILLS ---------------------------- ");
                    if (cursor1.moveToFirst()) {
                        do {
                            String name0 = cursor1.getString(0);
                            var bbb = cursor1.getString(1);
                            System.out.println("----> " + name0 + " " + bbb);

                        } while (cursor1.moveToNext());
                    }
                    Cursor cursor2 = db.rawQuery("SELECT * FROM ALL_CASHES_TABLE;", null);
                    System.out.println("removeBill  CASHES ---------------------------- ");
                    if (cursor2.moveToFirst()) {
                        do {
                            String name0 = cursor2.getString(0);
                            var bbb = cursor2.getString(1);
                            var bb3b = cursor2.getString(2);
                            System.out.println("----> " + name0 + " " + bbb + " " + bb3b);

                        } while (cursor2.moveToNext());
                    }


                }

            }
        });

        initFriends(animalsSetup, animalsSetup.size());


    }

    public static void updateTableResults() {
        CountBillAndCash count = new CountBillAndCash(productsFried);

        String names = count.giveFriendsNames();
        String bills = count.giveFriendBills();
        String cash = count.giveFriendsCash();

        AdapterResult adapterAllResult = new AdapterResult(context,
                R.layout.list_friend, names, bills, cash, count.resultDelta);

        productList.setAdapter(adapterAllResult);

        allFriends2.setText(count.resultFriendBills);
        allFriends4.setText(count.resultDeltaRes);
    }

    public void initFriends(Stack<Integer> animalsFree, int i) {
        System.out.println("initialization of " + i + " anilals");
        for (int j = 0; j < i; j++) {
            if (!animalsFree.isEmpty()) {
                TabLayout.Tab thirdTab = tabLayout.newTab();
                String animal = null;
                int ccc = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    Collections.shuffle(animalsFree);

                    if (animalsFree.isEmpty()) {
                        return;
                    }
                    System.out.println("!!!--Animals---> " + animalsFree.toString());
                    ccc = animalsFree.pop();
                    animal = friendNames.get(ccc);
                }
                thirdTab.setText(animal);
                thirdTab.setIcon(FriendFragment.getindexOfFriendImage(ccc));

                //set tabs text color
                tabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#FFFFFF"));
                tabLayout.addTab(thirdTab, true);
                updateTableResults();
                chet++;
            }
        }
    }
}