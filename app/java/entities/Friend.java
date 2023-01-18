package com.pixhunter.friendsbills.entities;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Entity for friend
 */
public class Friend {
    private float allBillTax;
    private float allCashTax;
    int number;
    String name = "";

    ArrayList<Bill> billList = new ArrayList<>();
    ArrayList<Cash> cashList = new ArrayList<>();

    //default friend params
    public Friend(int number) {
        this.allBillTax = 0;
        this.allCashTax = 0;
        this.number = number;
    }

    public float getBill() {
        return allBillTax;
    }
    public float getCash() {return allCashTax;}

    public ArrayList<Bill> getBillList() {return billList;}
    public ArrayList<Cash> getCashList() {return cashList;}

    public void updateBillList(ArrayList<Bill> billList) {this.billList = billList;}
    public void updateCashList(ArrayList<Cash> cashList) {this.cashList = cashList;}

    public void updateBill(float bill) {this.allBillTax = bill;}
    public void updateCash(float cash) {this.allCashTax = cash;}

    DecimalFormat df = new DecimalFormat("######.##");

    public String getTextBill(){
        String billPrefix = "TOTAL Bill: ";
        return billPrefix + df.format(allBillTax);
    }
    public String getTextCash(){
        String cashPrefix = "TOTAL Cash: ";
        return cashPrefix + df.format(allCashTax);
    }

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}
}
