package com.midas.mobile3.midas_mobile.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Drink {
    public String name;
    public String ImageUrl;
    public String cost;
    public String kcal;

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("ImageUrl", ImageUrl);
        result.put("cost", cost);
        result.put("kcal", kcal);
        return result;
    }
}
