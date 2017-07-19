package com.application.cortesluis.housekeeper.data;

/**
 * Created by luis_cortes on 7/18/17.
 */

public class GroupInfo {
    private int group;
    private int days;
    private FireDate date;

    public GroupInfo() {

    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public FireDate getDate() {
        return date;
    }

    public void setDate(FireDate date) {
        this.date = date;
    }
}
