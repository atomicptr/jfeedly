package de.kasoki.jfeedly.model;

import de.kasoki.jfeedly.JFeedly;
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

    public int getNumberOfUnreadArticles(JFeedly handler) {
        return handler.getCountOfUnreadArticles(this);
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

    public static Category fromJSONObject(JSONObject object) {
        return new Category(object.getString("id"), object.getString("label"));
    }

    public static Category getGlobalAllCategory(Profile userProfile) {
        return new Category("user/" + userProfile.getId() + "/category/global.all", "All");
    }

}
