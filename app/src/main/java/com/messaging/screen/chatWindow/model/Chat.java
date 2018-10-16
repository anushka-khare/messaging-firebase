package com.messaging.screen.chatWindow.model;

import android.support.annotation.NonNull;

import com.messaging.screen.common.AppConstants;

import java.util.HashMap;
import java.util.Map;

public class Chat implements Comparable {

    private String myMessage;
    private String status;
    private Long atTime;
    private String timeInFormat;

    public Chat(String myMessage, String status, String timeInFormat) {
        this.myMessage = myMessage;
        this.status = status;
        this.timeInFormat = timeInFormat;
    }

    public Chat(String myMessage, String status, long atTime) {
        this.myMessage = myMessage;
        this.status=status;
        this.atTime = atTime;

    }

    public String getMyMessage() {
        return myMessage;
    }


    public Long getAtTime() {
        return atTime;
    }

    public String getStatus() {
        return status;
    }

    public Map<String,Object> getChatMap()
    {
        HashMap<String,Object> map=new HashMap<>();
        map.put(AppConstants.MESSAGE,myMessage);
        map.put(AppConstants.STATUS,status);
        map.put(AppConstants.AT_TIME,atTime);
        return map;
    }

    public String getTimeInFormat() {
        return timeInFormat;
    }

    @Override
    public int compareTo(@NonNull Object o) {

        return (int) (this.atTime-((Chat)o).atTime);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimeInFormat(String timeInFormat) {
        this.timeInFormat = timeInFormat;
    }
}
