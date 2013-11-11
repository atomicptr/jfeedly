package de.kasoki.jfeedly.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Tags implements Iterable<Tag> {
    private ArrayList<Tag> tags;

    private Tags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public Iterator<Tag> iterator() {
        return tags.iterator();
    }

    public int getNumberOfTags() {
        return tags.size();
    }

    public Tag get(int index) {
        return tags.get(index);
    }

    public Tag getById(String id) {
        for(Tag t : this.tags) {
            if(t.getId().equals(id)) {
                return t;
            }
        }

        return null;
    }

    public static Tags fromJSONArray(JSONArray array) {
        ArrayList<Tag> tags = new ArrayList<Tag>();

        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            tags.add(Tag.fromJSONObject(object));
        }

        return new Tags(tags);
    }

}
