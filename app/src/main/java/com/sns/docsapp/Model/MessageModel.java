package com.sns.docsapp.Model;

public class MessageModel {
    private String message;
    private boolean isSelf;
    private long timeUTC;
    private boolean isDate;

    public boolean isDate() {
        return isDate;
    }

    public void setDate(boolean date) {
        isDate = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public long getTimeUTC() {
        return timeUTC;
    }

    public void setTimeUTC(long timeUTC) {
        this.timeUTC = timeUTC;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "message='" + message + '\'' +
                ", isSelf=" + isSelf +
                ", timeUTC=" + timeUTC +
                ", isDate=" + isDate +
                '}';
    }
}
