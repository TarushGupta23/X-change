package com.example.x_change.utility;

import java.util.ArrayList;

public class Profile {
    public String profileId, profileBanner, profileName;
    public String location, contact;
    public int successfulSwaps = 0;
    public ArrayList<String> reviewIds, sellingItemIds, bookmarkIds, chatsIds;

    public Profile(String profileId, String location, String contact, String name) {
        this.profileId = profileId;
        this.location = location;
        this.contact = contact;
        this.profileName = name;
        reviewIds = new ArrayList<>();
        sellingItemIds = new ArrayList<>();
        bookmarkIds = new ArrayList<>();
        chatsIds = new ArrayList<>();
    }

    public Profile() {}
}
