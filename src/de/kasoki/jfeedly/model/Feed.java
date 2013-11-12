package de.kasoki.jfeedly.model;

import de.kasoki.jfeedly.JFeedly;
import org.json.JSONObject;

public class Feed {

    private String id;
    private String title;
    private String description;
    private String website;
    private double velocity;
    private String language;
    private int subscribers;

    private Feed(String id, String title, String description, String website, double velocity,
                 String language, int subscribers) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.website = website;
        this.velocity = velocity;
        this.language = language;
        this.subscribers = subscribers;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public double getVelocity() {
        return velocity;
    }

    public String getLanguage() {
        return language;
    }

    public int getNumberOfSubscribers() {
        return subscribers;
    }

    public void markAsRead(JFeedly handler) {
        handler.markAsRead(this);
    }

    public Entries getEntries(JFeedly handler) {
        return handler.getEntriesFor(this);
    }

    public Entry getNewestEntry(JFeedly handler) {
        Entries entries = this.getEntries(handler);

        entries.sortByDateNewestFirst();

        if(entries.getNumberOfEntries() > 0) {
            return entries.get(0);
        } else {
            return null;
        }
    }

    public Entry getOldestEntry(JFeedly handler) {
        Entries entries = this.getEntries(handler);

        entries.sortByDateOldestFirst();

        if(entries.getNumberOfEntries() > 0) {
            return entries.get(0);
        } else {
            return null;
        }
    }

    public static Feed fromJSONObject(JSONObject object) {
        String id = object.getString("id");
        String title = object.getString("title");

        String website = object.getString("website");
        double velocity = object.getDouble("velocity");
        String language = object.getString("language");
        String description = object.has("description") ? object.getString("description") : "";
        int subscribers = object.getInt("subscribers");

        return new Feed(id, title, description, website, velocity, language, subscribers);
    }
}
