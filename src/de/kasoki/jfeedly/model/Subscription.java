// Copyright 2013 Christopher "Kasoki" Kaster <http://kasoki.de>
//
// This project is hosted at Github <https://github.com/Kasoki/jfeedly>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// <http://www.apache.org/licenses/LICENSE-2.0>
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package de.kasoki.jfeedly.model;

import de.kasoki.jfeedly.JFeedly;
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

    public void setTitle(String title) {
        this.title = title;
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

    public void addCategory(Category category) {
        this.categoryIds.add(category.getCategoryId());
    }

    public void removeCategory(Category category) {
        this.categoryIds.remove(category.getCategoryId());
    }

    public void update(JFeedly handler) {
        Categories userCategories = handler.getCategories();

        ArrayList<Category> categories = new ArrayList<Category>();

        for(String categoryId : this.categoryIds) {
            categories.add(userCategories.getById(categoryId));
        }

        handler.updateSubscription(this, categories);
    }

    public void delete(JFeedly handler) {
        handler.deleteSubscription(this);
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
