package com.application.cortesluis.housekeeper.data;

/**
 * Created by luis_cortes on 7/1/17.
 */

public class Task {
    private String authorId;
    private String msg;
    private long timeStamp;

    public Task() {

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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
