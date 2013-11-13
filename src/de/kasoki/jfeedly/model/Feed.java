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
 * Representation model of the "/v3/feeds" api call, this class is nearly identical with the "Subscription" class
 * @author Christopher Kaster
 */
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

    /** Returns the id of this feed */
    public String getId() {
        return id;
    }

    /** Returns the title of this feed */
    public String getTitle() {
        return title;
    }

    /** Returns the description of this feed */
    public String getDescription() {
        return description;
    }

    /** Returns the website of this feed */
    public String getWebsite() {
        return website;
    }

    /** Returns the velocity of this feed */
    public double getVelocity() {
        return velocity;
    }

    /** Returns the language of this feed */
    public String getLanguage() {
        return language;
    }

    /** Returns the number of subscribers of this feed */
    public int getNumberOfSubscribers() {
        return subscribers;
    }

    /** mark all articles in this feed as read */
    public void markAsRead(JFeedly handler) {
        handler.markAsRead(this);
    }

    /** Return the articles associated to this feed */
    public Entries getEntries(JFeedly handler) {
        return handler.getEntriesFor(this);
    }

    /** Get the newest article in this feed */
    public Entry getNewestEntry(JFeedly handler) {
        Entries entries = this.getEntries(handler);

        entries.sortByDateNewestFirst();

        if(entries.getNumberOfEntries() > 0) {
            return entries.get(0);
        } else {
            return null;
        }
    }

    /**Get the oldest article in this feed */
    public Entry getOldestEntry(JFeedly handler) {
        Entries entries = this.getEntries(handler);

        entries.sortByDateOldestFirst();

        if(entries.getNumberOfEntries() > 0) {
            return entries.get(0);
        } else {
            return null;
        }
    }

    /** Create a new Feed from a given JSON object */
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
