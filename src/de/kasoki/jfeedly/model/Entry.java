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

/**
 * Representation model of one Entry from the "/v3/entries" api call. This class is sometimes refered to as article.
 * @author Christopher Kaster
 */
public class Entry implements Comparable<Entry> {

    private String id;
    private String fingerprint;
    private String originId;
    private String author;
    private Date crawled;
    private String title;
    private String summaryContent;
    private String summaryDirection;
    private Date published;
    private String visualUrl;
    private int visualWidth;
    private int visualHeight;
    private String visualContentType;
    private boolean unread;
    private ArrayList<String> categoryIds;

    private Entry(String id, String fingerprint, String originId, String author, Date crawled,
                  String title, String summaryContent, String summaryDirection, Date published,
                  String visualUrl, int visualWidth, int visualHeight, String visualContentType, boolean unread,
                  ArrayList<String> categoryIds) {

        this.id = id;
        this.fingerprint = fingerprint;
        this.originId = originId;
        this.author = author;
        this.crawled = crawled;
        this.title = title;
        this.summaryContent = summaryContent;
        this.summaryDirection = summaryDirection;
        this.published = published;
        this.visualUrl = visualUrl;
        this.visualWidth = visualWidth;
        this.visualHeight = visualHeight;
        this.visualContentType = visualContentType;
        this.unread = unread;
        this.categoryIds = categoryIds;
    }

    /** Returns the ID of this article */
    public String getId() {
        return this.id;
    }

    /** Returns the fingerprint of this article */
    public String getFingerprint() {
        return fingerprint;
    }

    /** Returns the origin ID of this article */
    public String getOriginId() {
        return originId;
    }

    /** Returns the author of this article */
    public String getAuthor() {
        return author;
    }

    /** Returns the date this article was crawled */
    public Date getCrawledDate() {
        return crawled;
    }

    /** Returns the title of this article */
    public String getTitle() {
        return title;
    }

    /**
     * Check if this article has a summary
     * @return true/false whenever this article has or has not a summary
     */
    public boolean hasSummary() {
        return summaryContent != null;
    }

    /** Get the content of the summary */
    public String getSummaryContent() {
        return summaryContent;
    }

    /** Returns the summary direction */
    public String getSummaryDirection() {
        return summaryDirection;
    }

    /** Returns the date this article was published */
    public Date getPublishedDate() {
        return published;
    }

    /** Check if this article has a "visual" (a preview image) */
    public boolean hasVisual() {
        return visualUrl != null;
    }

    /** Get the url of the preview image */
    public String getVisualUrl() {
        return visualUrl;
    }

    /** Returns width of the preview image (-1 when not specified) */
    public int getVisualWidth() {
        return visualWidth;
    }

    /** Returns height of the preview image (-1 when not specified) */
    public int getVisualHeight() {
        return visualHeight;
    }

    /** Returns the content type of the preview image */
    public String getVisualContentType() {
        return visualContentType;
    }

    /** Is this article unread? */
    public boolean isUnread() {
        return unread;
    }

    /** Returns the IDs of the categories which affect this article */
    public ArrayList<String> getCategoryIds() {
        return categoryIds;
    }

    /** Mark this article as read */
    public void markAsRead(JFeedly handler) {
        handler.markAsRead(this);
    }

    /** Create a new Entry from the given JSON object */
    public static Entry fromJSONObject(JSONObject object) {
        String id = object.getString("id");
        String fingerprint = object.getString("fingerprint");
        String originId = object.getJSONObject("origin").getString("streamId");
        String author = object.has("author") ? object.getString("author") : "None";
        Date crawled = new Date(object.getLong("crawled"));
        String title = object.getString("title");

        String summaryContent = null;
        String summaryDirection = null;

        if(object.has("summary")) {
            summaryContent = object.getJSONObject("summary").getString("content");
            summaryDirection = object.getJSONObject("summary").getString("direction");
        }

        Date published = new Date(object.getLong("published"));

        String visualUrl = null;
        int visualWidth = -1;
        int visualHeight = -1;
        String visualContentType = null;

        if(object.has("visual")) {
            visualUrl = object.getJSONObject("visual").getString("url");
            visualWidth = object.has("width") ? object.getJSONObject("visual").getInt("width") : -1;
            visualHeight = object.has("height") ? object.getJSONObject("visual").getInt("height") : -1;
            visualContentType = object.has("contentType") ?
                    object.getJSONObject("visual").getString("contentType") : null;
        }


        boolean unread = object.getBoolean("unread");

        ArrayList<String> categoryIds = new ArrayList<String>();

        JSONArray categories = object.getJSONArray("categories");

        for(int i = 0; i < categories.length(); i++) {
            JSONObject category = categories.getJSONObject(i);

            categoryIds.add(category.getString("id"));
        }

        return new Entry(id, fingerprint, originId, author, crawled, title,
                summaryContent, summaryDirection, published, visualUrl, visualWidth,
                visualHeight, visualContentType, unread, categoryIds);
    }

    @Override
    /** compare this article to another (by published date) */
    public int compareTo(Entry otherEntry) {
        return this.getPublishedDate().compareTo(otherEntry.getPublishedDate());
    }
}
