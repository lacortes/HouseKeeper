package com.application.cortesluis.housekeeper.data;

import android.text.format.DateFormat;

import java.util.Date;
import java.util.UUID;

/**
 * Created by luis_cortes on 7/2/17.
 */

public class Announcement {
    private String authorId;
    private String msg;
    private String authorName;
    private String date;
    private long timeStamp;

    public Announcement() {

    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
