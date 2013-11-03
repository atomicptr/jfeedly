package de.kasoki.jfeedly.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class Subscription {
    private String id;
    private String title;
    private double velocity;
    private Date updated;
    private String website;
    private ArrayList<String> categoryIds;

    private Subscription(String id, String title, double velocity, Date updated, String website, ArrayList<String> categoryIds) {
        this.id = id;
        this.title = title;
        this.velocity = velocity;
        this.updated = updated;
        this.website = website;
        this.categoryIds = categoryIds;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getVelocity() {
        return velocity;
    }

    public Date getUpdated() {
        return updated;
    }

    public String getWebsite() {
        return website;
    }

    public ArrayList<String> getCategoryIds() {
        return this.categoryIds;
    }

    public static Subscription fromJSONObject(JSONObject object) {
        Date updatedDate = new Date(object.getLong("updated"));

        ArrayList<String> categoryIds = new ArrayList<String>();

        JSONArray array = object.getJSONArray("categories");

        for(int i = 0; i < array.length(); i++) {
            JSONObject category = array.getJSONObject(i);

            categoryIds.add(category.getString("id"));
        }

        return new Subscription(object.getString("id"), object.getString("title"), object.getDouble("velocity"),
                updatedDate, object.getString("website"), categoryIds);
    }
}
