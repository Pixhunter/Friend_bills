package com.pixhunter.friendsbills;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Class with all main variables
 */
public class Variables {

    public static List<String> friendNames = List.of(
            "Monkey", "Frog", "Yuriy", "Cat", "Fox", "Elephant", "Racoon", "Lion", "Wolf", "Walrus",
            "Pig", "Goat", "Giraffe", "Deer", "Hyena", "Panda", "Polyana", "Octopus", "Crab", "Shark");

    public static final String DB_NAME = "Cash_and_Bills";

    public static int progressChangedValue = 100;

    public static final String SQL_QUERY_NAME_BILLS = "SELECT ALL_NAMES FROM ALL_BILLS_TABLE;";
    public static final String SQL_QUERY_FRIEND_CASHES = "SELECT * FROM ALL_CASHES_TABLE WHERE ALL_NAMES = ";
    public static final String SQL_QUERY_FRIEND_BILLS = "SELECT * FROM ALL_BILLS_TABLE WHERE ALL_NAMES = ";

    public static final String SQL_QUERY_CASHES = "SELECT * FROM ALL_CASHES_TABLE;";
    public static final String SQL_QUERY_SELECT_NAMES = "SELECT ALL_NEW_NAMES FROM ALL_NAMES_TABLE WHERE ALL_NAMES = ";
    public static final String SQL_QUERY_NAMES = "SELECT * FROM ALL_NAMES_TABLE WHERE ALL_NAMES = ";

    public static SQLiteDatabase db;
}
