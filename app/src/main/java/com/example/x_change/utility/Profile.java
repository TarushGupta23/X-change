package com.example.x_change.utility;

import java.util.ArrayList;

public class Profile {
    public String profileId, profileBanner;
    public String location, contact, profilePic;
    public int successfulSwaps = 0;
    public ArrayList<String> reviewIds, sellingItemIds, bookmarkIds, chatsIds;

    public Profile(String profileId, String location, String contact, String profilePic) {
        this.profileId = profileId;
        this.location = location;
        this.contact = contact;
        this.profilePic = profilePic;
        reviewIds = new ArrayList<>();
        sellingItemIds = new ArrayList<>();
        bookmarkIds = new ArrayList<>();
        chatsIds = new ArrayList<>();
    }
}
