package com.example.androidsms.models;

import java.util.Date;

public class SMSInfo {
    private String sender;
    private String personName;
    private String content;
    private long date;
    private boolean isRead;
    // 0-ALL 1-收件箱 2-已发送 3-草稿 4-发件箱  5-发送失败  6-待发送
    private int type;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
