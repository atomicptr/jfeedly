package de.kasoki.jfeedly.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Categories implements Iterable<Category> {
    private ArrayList<Category> categories;

    private Categories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public Iterator<Category> iterator() {
        return categories.iterator();
    }

    public int getNumberOfCategories() {
        return categories.size();
    }

    public static Categories fromJSONArray(JSONArray array) {
        ArrayList<Category> categories = new ArrayList<Category>();

        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            categories.add(Category.fromJSONObject(object));
        }

        return new Categories(categories);
    }

}
