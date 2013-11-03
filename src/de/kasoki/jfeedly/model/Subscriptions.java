package de.kasoki.jfeedly.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Subscriptions implements Iterable<Subscription> {
    private ArrayList<Subscription> subscriptions;

    private Subscriptions(ArrayList<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Iterator<Subscription> iterator() {
        return subscriptions.iterator();
    }

    public int getNumberOfSubscriptions() {
        return subscriptions.size();
    }

    public Subscription get(int index) {
        return subscriptions.get(index);
    }

    public Subscription getById(String id) {
        for(Subscription s : this.subscriptions) {
            if(s.getId().equals(id)) {
                return s;
            }
        }

        return null;
    }

    public static Subscriptions fromJSONArray(JSONArray array) {
        ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();

        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            subscriptions.add(Subscription.fromJSONObject(object));
        }

        return new Subscriptions(subscriptions);
    }

}
