package com.example.x_change.utility;

import java.util.ArrayList;

public class SwappingItem {
    public String name, itemId, sellerId, description, lookingFor, value;
    public ArrayList<String> images = new ArrayList<>();
    public String mainImage;

    public SwappingItem(String name, String itemId, String sellerId, String description, String lookingFor, String value, ArrayList<String> images, String mainImage) {
        this.name = name;
        this.itemId = itemId;
        this.sellerId = sellerId;
        this.description = description;
        this.lookingFor = lookingFor;
        this.value = value;
        this.images = images;
        this.mainImage = mainImage;
    }

    public SwappingItem(String n) { // for testing purposes
        this.name = n;
    }

    public SwappingItem() {}
}
