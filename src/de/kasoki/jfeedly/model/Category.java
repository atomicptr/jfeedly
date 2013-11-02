package de.kasoki.jfeedly.model;

import org.json.JSONObject;

public class Category {

    private String categoryId;
    private String label;

    private Category(String categoryId, String label) {
        this.categoryId = categoryId;
        this.label = label;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getLabel() {
        return label;
    }

    public static Category fromJSONObject(JSONObject object) {
        return new Category(object.getString("id"), object.getString("label"));
    }

}
