package com.pixhunter.friendsbills;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * Information alert dialog window - with help text
 */
public class InformationDialogFragment extends DialogFragment {
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("INSTRUCTION")
                .setIcon(R.drawable.ic_action_monkey)
                .setMessage(
                        "This app helps you and your friends to collect all cashes and bills not to count any more.\n\n" +
                        "You can add or delete any items - just press the buttons with + or -\n\n" +
                        "MORE ABOUT SECTIONS:\n\n" +
                        "● FRIENDS: add or delete friends, but the maximum number of friends about 20\n" +
                        "● COMMENT: add special comment for friend if you need, for example - real friend name\n" +
                        "● BILLS: add all items that friend get in bill, this section collect history of them\n" +
                        "● CASHES: add all cashes that friend has, this section helps to chose optimal cashes\n" +
                        "● TAX RATE: set taxes of bill, for default it is 100%, you can change it up to 200% or les\n\n" +
                        "● TOTAL: this is a table of total counts by all friends with all information about each friend - bills, " +
                        "what banknotes he needs to give and how much money from change he needs back\n"
                        )
                .setPositiveButton("close", null)
                .create();
    }
}
