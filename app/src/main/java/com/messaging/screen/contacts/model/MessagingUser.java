package com.messaging.screen.contacts.model;

import com.messaging.screen.common.AppConstants;

import java.util.HashMap;
import java.util.Map;

public class MessagingUser {


    private String phoneNumber;
    private String name;

    public MessagingUser(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MessagingUser(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public Map<String,Object> getMessagingUserMap()
    {
        Map<String, Object> user = new HashMap<>();
        user.put(AppConstants.MESSAGING_USER_PHONE_NUMBER,phoneNumber);

        return user;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }
}
