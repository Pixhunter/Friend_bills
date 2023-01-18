package com.pixhunter.friendsbills.entities;

/**
 * Entity for cash
 */
public class Cash {

    private String name;
    private Float cash;
    private  int count;
    Friend friend;

    //default cash params
    public Cash(String name, int count, Friend friend){
        this.name = name;
        this.count = count;
        this.friend = friend;
        try {
            this.cash=Float.parseFloat(name);
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException - can't parse integer for cash count");
            this.cash = (float) 0;
        }
    }

    public Float getCash() {
        return cash;
    }
    public int getCount() {return count;}
    public void incCount() {count++;}
    public void decCount() {count--;}
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
}