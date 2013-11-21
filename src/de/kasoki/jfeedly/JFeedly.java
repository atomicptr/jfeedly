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
package de.kasoki.jfeedly;

import de.kasoki.jfeedly.components.BrowserFrame;
import de.kasoki.jfeedly.components.OnAuthenticatedListener;
import de.kasoki.jfeedly.helper.HTTPConnections;
import de.kasoki.jfeedly.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * API handler for the Feedly API.
 * @author Christopher Kaster
 */
public class JFeedly {

    private String appName = "jfeedly";
    private boolean verbose = false;

    private FeedlyConnection connection;

    private String basename;
    private String clientId;
    private String apiSecretKey;

    private OnAuthenticatedListener listener = null;
    private HTTPConnections httpHelper;

    private static final int MAJOR_VERSION = 0;
    private static final int MINOR_VERSION = 0;
    private static final int PATCH_VERSION = 19;
    private String configPath = ".";

    protected JFeedly(String basename, String clientId, String apiSecretKey) {
        this.basename = basename;
        this.clientId = clientId;
        this.apiSecretKey = apiSecretKey;

        this.httpHelper = new HTTPConnections(this);
    }

    /** authenticate with the Feedly server */
    public void authenticate() {
        if(this.getVerbose()) {
            System.out.println("jfeedly v" + JFeedly.getVersion() + ": try to authenticate...");
        }

        System.out.println("trying to restore connection from file: " + configPath + "/connection.properties");

        if(!FeedlyConnection.oldConnectionExists(configPath + "/connection.properties")) {
            String authUrl = this.getAuthenticationUrl();
            BrowserFrame frame = new BrowserFrame(appName + " Authenticate", authUrl);

            frame.setOnAuthenticatedListener(new BrowserFrame.OnBrowserAuthenticatedListener() {
                @Override
                public void onSignedIn(String code) {
                    JFeedly.this.requestNewTokens(code);
                }
            });

            frame.setVisible(true);
        } else {
            connection = FeedlyConnection.restoreConnection(configPath + "/connection.properties");

            if(connection.isExpired()) {
                System.out.println("Tokens are expired. \nRequest new tokens...");

                this.refreshTokens();
            } else {
                this.onAuthenticated();
            }
        }
    }

    private void requestNewTokens(String code) {
        String apiUrl = "/v3/auth/token/";

        String urlParameters = "code=" + code + "&client_id=" + this.clientId + "&client_secret=" + this.apiSecretKey +
                "&redirect_uri=http://localhost&grant_type=authorization_code";

        String response = httpHelper.sendPostRequestToFeedly(apiUrl, urlParameters, false, "");

        JSONObject object = new JSONObject(response);

        this.connection = FeedlyConnection.newConnection(object, configPath + "/connection.properties");
        this.onAuthenticated();
    }

    private void refreshTokens() {
        String apiUrl = "/v3/auth/token/";

        String refreshToken = this.connection.getRefreshToken();

        String urlParameters = "refresh_token=" + refreshToken + "&client_id=" + this.clientId + "&client_secret=" + this.apiSecretKey + "&grant_type=refresh_token";

        String response = httpHelper.sendPostRequestToFeedly(apiUrl, urlParameters, false, "");

        JSONObject object = new JSONObject(response);

        this.connection.refresh(object);
        this.onAuthenticated();
    }

    private void onAuthenticated() {
        if(listener != null) {
            listener.onAuthenticated(JFeedly.this);
        }
    }

    private String getAuthenticationUrl() {
        return this.getBaseUrl() + "/v3/auth/auth?response_type=code&client_id=" + this.clientId +
                "&redirect_uri=http://localhost&scope=https://cloud.feedly.com/subscriptions";
    }

    /** Returns the base url of feedly this app is using (sandbox or cloud) */
    public String getBaseUrl() {
        return "http://" + this.basename + ".feedly.com";
    }

    /** Returns the connection model */
    public FeedlyConnection getConnection() {
        return this.connection;
    }

    /** Set a listener which will be called when the authentication is done */
    public void setOnAuthenticatedListener(OnAuthenticatedListener listener) {
        this.listener = listener;
    }

    /** Set the name of this app (will for instance be displayed in the BrowserFrame window) */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /** Make jfeedly verbose. This will print a lot of information for example every connection to the server */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /** Is jfeedly set to verbose? */
    public boolean getVerbose() {
        return this.verbose;
    }

    /** Is the user a Pro user? (Can he use pro features like in stream search?) */
    public boolean isPro() {
        if(this.connection != null) {
            return this.connection.getPlan().equals("pro");
        } else {
            System.err.println("JFeedly: Connection required to do this...\n\nCall jfeedlyInstance.authenticate();");
        }

        return false;
    }

    /** Set the path where all files (connection file + cache) will be stored */
    public void setConfigPath(String path) {
        this.configPath = path;
    }

    /** Returns a user profile */
    public Profile getProfile() {
        if(this.connection != null) {
            String response = httpHelper.sendGetRequestToFeedly("/v3/profile/");

            JSONObject object = new JSONObject(response);

            return Profile.fromJSONObject(object);
        } else {
            System.err.println("JFeedly: Connection required to do this...\n\nCall jfeedlyInstance.authenticate();");
        }

        return null;
    }

    /** Returns all categories */
    public Categories getCategories() {
        if(this.connection != null) {
            String response = httpHelper.sendGetRequestToFeedly("/v3/categories/");

            JSONArray array = new JSONArray(response);

            return Categories.fromJSONArray(array);
        } else {
            System.err.println("JFeedly: Connection required to do this...\n\nCall jfeedlyInstance.authenticate();");
        }

        return null;
    }

    /** Returns all subscriptions */
    public Subscriptions getSubscriptions() {
        if(this.connection != null) {
            String response = httpHelper.sendGetRequestToFeedly("/v3/subscriptions/");

            JSONArray array = new JSONArray(response);

            return Subscriptions.fromJSONArray(array);
        } else {
            System.err.println("JFeedly: Connection required to do this...\n\nCall jfeedlyInstance.authenticate();");
        }

        return null;
    }

    /** Return all tags */
    public Tags getTags() {
        if(this.connection != null) {
            String response = httpHelper.sendGetRequestToFeedly("/v3/tags/");

            JSONArray array = new JSONArray(response);

            return Tags.fromJSONArray(array);
        } else {
            System.err.println("JFeedly: Connection required to do this...\n\nCall jfeedlyInstance.authenticate();");
        }

        return null;
    }

    /**
     * Subscribe to a feed
     * @param feedUrl The URL of the feed
     * @param title The title of this feed
     * @param categories A list of categories, if this is empty the feed will be in the "Uncategorized" category.
     */
    public void subscribe(String feedUrl, String title, List<Category> categories) {
        if(this.connection != null) {
            JSONObject object = new JSONObject();

            object.put("id", "feed/" + feedUrl);
            object.put("title", title);

            JSONArray categoriesArray = new JSONArray();

            for(Category c : categories) {
                HashMap<String, String> category = new HashMap<String, String>();

                category.put("id", c.getCategoryId());
                category.put("label", c.getLabel());

                categoriesArray.put(category);
            }

            object.put("categories", categoriesArray);

            String input = object.toString();

            httpHelper.sendPostRequestToFeedly("/v3/subscriptions/", input, true);
        } else {
            System.err.println("JFeedly: Connection required to do this...\n\nCall jfeedlyInstance.authenticate();");
        }
    }

    /** Save changes on a subscription to the feedly server */
    public void updateSubscription(Subscription subscription, ArrayList<Category> categories) {
        if(this.connection != null) {
            JSONObject object = new JSONObject();

            object.put("id", subscription.getId());
            object.put("title", subscription.getTitle());

            JSONArray categoriesArray = new JSONArray();

            for(Category c : categories) {
                HashMap<String, String> category = new HashMap<String, String>();

                category.put("id", c.getCategoryId());
                category.put("label", c.getLabel());

                categoriesArray.put(category);
            }

            object.put("categories", categoriesArray);

            String input = object.toString();

            httpHelper.sendPostRequestToFeedly("/v3/subscriptions/", input, true);
        } else {
            System.err.println("JFeedly: Connection required to do this...\n\nCall jfeedlyInstance.authenticate();");
        }
    }

    /** Remove a given subscription */
    public void deleteSubscription(Subscription subscription) {
        String feedId = subscription.getId();

        httpHelper.sendDeleteRequestToFeedly("/v3/subscriptions/" + feedId);

        System.err.println("jfeedly: deleting subscriptions seems not to work at the moment :(");
    }

    /**
     * Search feeds by a specified search query. This will not search in streams.
     * @param query search query (e.g. "android", "java", etc.)
     * @return A list of feeds which are somehow affected by your search query.
     */
    public ArrayList<Feed> searchFeeds(String query) {
        return this.searchFeeds(query, 20);
    }

    /**
     * Search feeds by a specified search query. This will not search in streams.
     * @param query search query (e.g. "android", "java", etc.)
     * @param numberOfFeeds Specifiy a maximum number of feeds which will be returned (Default: 20).
     * @return A list of feeds which are somehow affected by your search query.
     */
    public ArrayList<Feed> searchFeeds(String query, int numberOfFeeds) {
        String response = httpHelper.sendGetRequestToFeedly("/v3/search/feeds/?q=" + query + "&n=" + numberOfFeeds);

        JSONObject searchResult = new JSONObject(response);

        JSONArray results = searchResult.getJSONArray("results");

        ArrayList<Feed> feeds = new ArrayList<Feed>();

        for(int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);

            feeds.add(this.getFeedById(result.getString("feedId")));
        }

        return feeds;
    }

    /**
     * Search in streams (PRO only)
     * @param feedId The feed/subscription where you want to find something
     * @param query The search query
     * @return A JSON string (couldn't test this yet because i'm not a Pro user)
     */
    public String searchInFeeds(String feedId, String query) {
        if(isPro()) {
            String response = httpHelper.sendGetRequestToFeedly("/v3/streams/contents?streamId=" + feedId +
                    "&q=" + query);

            return response;
        } else {
            return "{\"error\": \"Pro required\"}";
        }
    }

    /** Returns ALL entries (max. 10'000) */
    public Entries getEntries() {
        return this.getEntries(10000);
    }

    /**
     *  Returns ALL entries with a specified maximum
     * @param number Maximum of articles
     */
    public Entries getEntries(int number) {
        Profile profile = this.getProfile();

        return this.getEntriesFor(Category.getGlobalAllCategory(profile).getCategoryId(), true, true, number);
    }

    /** Returns all articles for a specified Category (max. 10'000) */
    public Entries getEntriesFor(Category category) {
        return this.getEntriesFor(category.getCategoryId(), true, true, 10000);
    }

    /** Returns all articles for a specified Category (max. 10'000)
     * @param unreadOnly Show only the unread entries?
     */
    public Entries getEntriesFor(Category category, boolean unreadOnly) {
        return this.getEntriesFor(category.getCategoryId(), unreadOnly, true, 10000);
    }

    /** Returns all articles for a specified Feed (max. 10'000) */
    public Entries getEntriesFor(Feed feed) {
        return this.getEntriesFor(feed.getId(), true, true, 10000);
    }

    /** Returns all articles for a specified Feed (max. 10'000)
     * @param unreadOnly Show only the unread entries?
     */
    public Entries getEntriesFor(Feed feed, boolean unreadOnly) {
        return this.getEntriesFor(feed.getId(), unreadOnly, true, 10000);
    }

    /** Returns all articles for a specified Subscription (max. 10'000) */
    public Entries getEntriesFor(Subscription subscription) {
        return this.getEntriesFor(subscription.getId(), true, true, 10000);
    }

    /** Returns all articles for a specified Subscription (max. 10'000)
     * @param unreadOnly Show only the unread entries?
     */
    public Entries getEntriesFor(Subscription subscription, boolean unreadOnly) {
        return this.getEntriesFor(subscription.getId(), unreadOnly, true, 10000);
    }

    /**
     * Returns articles
     * @param id All articles grouped by one ID. May be a subscription, feed, tag or category id
     * @param unreadOnly List only the unread entries
     * @param showNewest Newest first?
     * @param number Maximum number of entries
     * @return A bunch of articles
     */
    public Entries getEntriesFor(String id, boolean unreadOnly, boolean showNewest, int number) {
        String entryIdResponse = httpHelper.sendGetRequestToFeedly("/v3/streams/ids?streamId=" + id +
            "&unreadOnly=" + unreadOnly + "&count=" + number + "&ranked=" + (showNewest ? "newest" : "oldest"));

        String response = httpHelper.sendPostRequestToFeedly("/v3/entries/.mget", entryIdResponse, true);

        return Entries.fromJSONArray(new JSONArray(response));
    }

    /** Get a Feed specified by an ID */
    public Feed getFeedById(String feedId) {
        String response = httpHelper.sendPostRequestToFeedly("/v3/feeds/.mget", "[ \"" + feedId + "\" ]", true);

        JSONArray objects = new JSONArray(response);

        JSONObject object = objects.getJSONObject(0);

        return Feed.fromJSONObject(object);
    }

    /** Returns the number of unread articles for a category */
    public int getCountOfUnreadArticles(Category category) {
        return this.getCountOfUnreadArticles(category.getCategoryId());
    }

    /** Returns the number of unread articles for a subscription */
    public int getCountOfUnreadArticles(Subscription subscription) {
        return this.getCountOfUnreadArticles(subscription.getId());
    }

    /** Returns the number of unread articles for an ID (may be a feed, subscription, category or tag */
    protected int getCountOfUnreadArticles(String id) {
        String response = httpHelper.sendGetRequestToFeedly("/v3/markers/counts");

        JSONObject object = new JSONObject(response);

        JSONArray unreadcounts = object.getJSONArray("unreadcounts");

        int unreadCount = -1;

        for(int i = 0; i < unreadcounts.length(); i++) {
            JSONObject unread = unreadcounts.getJSONObject(i);

            String unreadId = unread.getString("id");

            if(id.equals(unreadId)) {
                unreadCount = unread.getInt("count");

                break;
            }
        }

        if(unreadCount == -1) {
            System.err.println("Unkown id: " + id);
        }

        return unreadCount;
    }

    /** Mark everything as read */
    public void markEverythingAsRead() {
        this.markAsRead(Category.getGlobalAllCategory(getProfile()));
    }

    /** Mark an article as read */
    public void markAsRead(Entry entry) {
        this.markAsRead(entry.getId(), "entries", null);
    }

    /** Mark a subscription as read */
    public void markAsRead(Subscription subscription) {
        this.markAsRead(subscription.getId(), "feeds", subscription.getNewestEntry(this));
    }

    /** Mark a feed as read */
    public void markAsRead(Feed feed) {
        this.markAsRead(feed.getId(), "feeds", feed.getNewestEntry(this));
    }

    /** Mark a category as read */
    public void markAsRead(Category category) {
        this.markAsRead(category.getCategoryId(), "categories", category.getNewestEntry(this));
    }

    private void markAsRead(String id, String type, Entry newestEntry) {
        JSONObject object = new JSONObject();

        object.put("action", "markAsRead");

        object.put("type", type);

        JSONArray ids = new JSONArray();
        ids.put(id);

        String typeIdIdentificator = null;

        if(type.equals("entries")) {
            typeIdIdentificator = "entryIds";
        } else if(type.equals("feeds")) {
            typeIdIdentificator = "feedIds";
        } else if(type.equals("categories")) {
            typeIdIdentificator = "categoryIds";
        } else {
            System.err.println("jfeedly: Unknown type: " + type + " don't know what to do with this.");
        }

        object.put(typeIdIdentificator, ids);

        if(!type.equals("entries") && newestEntry != null) {
            object.put("lastReadEntryId", newestEntry.getId());
        }

        httpHelper.sendPostRequestToFeedly("/v3/markers", object.toString(), true);
    }

    /**
     * Export the users subscriptions as OPML
     * @return A String which contains a XML/OPML files content.
     */
    public String exportOPML() {
        return httpHelper.sendGetRequestToFeedly("/v3/opml");
    }

    /** Add subscriptions to the users account based on this OPML string */
    public void importOPML(String opmlString) {
        httpHelper.sendPostRequestToFeedly("/v3/markers", opmlString, true, "application/xml");
    }

    /**
     * Create a handler for sandbox usage.
     * @param apiSecretKey Your secret api key. If you have none please contact Feedly.
     * @return A jfeedly api handler
     */
    public static JFeedly createSandboxHandler(String apiSecretKey) {
        return new JFeedly("sandbox", "sandbox", apiSecretKey);
    }

    /**
     * Create a handler for production usage
     * @param clientId Your client id.
     * @param apiSecretKey Your secret api key.
     * @return A jfeedly api handler
     */
    public static JFeedly createHandler(String clientId, String apiSecretKey) {
        return new JFeedly("cloud", clientId, apiSecretKey);
    }

    /**
     * Create a cached handler for sandbox usage.
     * @param apiSecretKey Your secret api key. If you have none please contact Feedly.
     * @return A jfeedly api handler
     */
    public static JFeedlyCached createCachcedSandboxHandler(String apiSecretKey) {
        return JFeedlyCached.createCachedSandboxHandler(apiSecretKey);
    }

    /**
     * Create a cached handler for production usage
     * @param clientId Your client id.
     * @param apiSecretKey Your secret api key.
     * @return A jfeedly api handler
     */
    public static JFeedlyCached createCachedHandler(String clientId, String apiSecretKey) {
        return JFeedlyCached.createCachedHandler(clientId, apiSecretKey);
    }

    /** Returns the version of jfeedly */
    public static String getVersion() {
        return MAJOR_VERSION + "." + MINOR_VERSION + "." + PATCH_VERSION;
    }
}
