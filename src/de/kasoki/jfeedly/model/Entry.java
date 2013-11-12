package de.kasoki.jfeedly.model;

import de.kasoki.jfeedly.JFeedly;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class Entry {

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

    public String getId() {
        return this.id;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getOriginId() {
        return originId;
    }

    public String getAuthor() {
        return author;
    }

    public Date getCrawledDate() {
        return crawled;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasSummary() {
        return summaryContent != null;
    }

    public String getSummaryContent() {
        return summaryContent;
    }

    public String getSummaryDirection() {
        return summaryDirection;
    }

    public Date getPublishedDate() {
        return published;
    }

    public boolean hasVisual() {
        return visualUrl != null;
    }

    public String getVisualUrl() {
        return visualUrl;
    }

    public int getVisualWidth() {
        return visualWidth;
    }

    public int getVisualHeight() {
        return visualHeight;
    }

    public String getVisualContentType() {
        return visualContentType;
    }

    public boolean isUnread() {
        return unread;
    }

    public ArrayList<String> getCategoryIds() {
        return categoryIds;
    }

    public void markAsRead(JFeedly handler) {
        handler.markAsRead(this);
    }

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
}
