package com.pixhunter.friendsbills.entities;

public class Pack implements Comparable<Pack>{
    public float delta;
    public String pack;

    // default pack params
    public Pack() {
        this.pack = " ";
        this.delta = 1;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    public float getDelta() {
        return delta;
    }

    public String getPack() {
        return pack;
    }

    public void updatePack(String man) {
        pack = man;
    }

    @Override
    public int compareTo(Pack pack) {
        return this.pack.compareTo(pack.pack);
    }
}
