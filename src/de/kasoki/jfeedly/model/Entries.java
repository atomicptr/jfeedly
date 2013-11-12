package de.kasoki.jfeedly.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Entries implements Iterable<Entry> {
    private ArrayList<Entry> entries;

    private Entries(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    public int getNumberOfEntries() {
        return entries.size();
    }

    public Entry get(int index) {
        return entries.get(index);
    }

    public Entry getById(String id) {
        for(Entry t : this.entries) {
            if(t.getId().equals(id)) {
                return t;
            }
        }

        return null;
    }

    public void sortByDateOldestFirst() {
        Collections.sort(entries);
    }

    public void sortByDateNewestFirst() {
        Collections.sort(entries, Collections.reverseOrder());
    }

    public static Entries fromJSONArray(JSONArray array) {
        ArrayList<Entry> entries = new ArrayList<Entry>();

        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            entries.add(Entry.fromJSONObject(object));
        }

        return new Entries(entries);
    }

}
