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
import org.json.JSONObject;

/**
 * Representation model of a category from "/v3/categories"
 */
public class Category {

    private String categoryId;
    private String label;

    private Category(String categoryId, String label) {
        this.categoryId = categoryId;
        this.label = label;
    }

    /** Returns the ID of this category */
    public String getCategoryId() {
        return categoryId;
    }

    /** Returns the label of this category */
    public String getLabel() {
        return label;
    }

    /** Returns the number of unread articles */
    public int getNumberOfUnreadArticles(JFeedly handler) {
        return handler.getCountOfUnreadArticles(this);
    }

    /** Mark this category as read */
    public void markAsRead(JFeedly handler) {
        handler.markAsRead(this);
    }

    /** Returns all unread articles of this category */
    public Entries getEntries(JFeedly handler) {
        return handler.getEntriesFor(this);
    }

    /** Returns the newest (date) article */
    public Entry getNewestEntry(JFeedly handler) {
        Entries entries = this.getEntries(handler);

        entries.sortByDateNewestFirst();

        if(entries.getNumberOfEntries() > 0) {
            return entries.get(0);
        } else {
            return null;
        }
    }

    /** Returns the oldest (date) article */
    public Entry getOldestEntry(JFeedly handler) {
        Entries entries = this.getEntries(handler);

        entries.sortByDateOldestFirst();

        if(entries.getNumberOfEntries() > 0) {
            return entries.get(0);
        } else {
            return null;
        }
    }

    /** Create a new Category from a given JSON object */
    public static Category fromJSONObject(JSONObject object) {
        return new Category(object.getString("id"), object.getString("label"));
    }

    /** Get the global.all (which contains all articles) category */
    public static Category getGlobalAllCategory(Profile userProfile) {
        return new Category("user/" + userProfile.getId() + "/category/global.all", "All");
    }

}
