package de.kasoki.jfeedly.model;

import org.json.JSONObject;

public class Tag {

    private String id;
    private String label;

    private Tag(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public static Tag fromJSONObject(JSONObject object) {
        String id = object.getString("id");

        String label = null;

        if(object.has("label")) {
            label = object.getString("label");
        }

        return new Tag(id, label);
    }
}
