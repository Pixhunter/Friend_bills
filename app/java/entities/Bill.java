package com.pixhunter.friendsbills.entities;

/**
 * Entity for bill
 */
public class Bill {
    private String name;
    private int count;
    Friend friend;

    //default bill params
    public Bill(String name, Friend friend){
        this.name = name;
        this.friend = friend;
        try {
            this.count = Integer.parseInt(name);
            friend.updateBill(friend.getBill() + count);
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException - can't parse integer for bill count");
            this.count = 0;
        }
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
}