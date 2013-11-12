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

public class JFeedly {

    private String appName = "JFeedly";
    private boolean verbose = false;

    private FeedlyConnection connection;

    private String basename;
    private String clientId;
    private String apiSecretKey;

    private OnAuthenticatedListener listener = null;
    private HTTPConnections httpHelper;

    private static final int MAJOR_VERSION = 0;
    private static final int MINOR_VERSION = 0;
    private static final int PATCH_VERSION = 9;

    private JFeedly(String basename, String clientId, String apiSecretKey) {
        this.basename = basename;
        this.clientId = clientId;
        this.apiSecretKey = apiSecretKey;

        this.httpHelper = new HTTPConnections(this);
    }

    public void authenticate() {
        if(this.getVerbose()) {
            System.out.println("jfeedly v" + JFeedly.getVersion() + ": try to authenticate...");
        }

        if(!FeedlyConnection.oldConnectionExists()) {
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
            connection = FeedlyConnection.restoreConnection();

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

        String response = httpHelper.sendPostRequestToFeedly(apiUrl, urlParameters);

        JSONObject object = new JSONObject(response);

        this.connection = FeedlyConnection.newConnection(object);
        this.onAuthenticated();
    }

    private void refreshTokens() {
        String apiUrl = "/v3/auth/token/";

        String refreshToken = this.connection.getRefreshToken();

        String urlParameters = "refresh_token=" + refreshToken + "&client_id=" + this.clientId + "&client_secret=" + this.apiSecretKey + "&grant_type=refresh_token";

        String response = httpHelper.sendPostRequestToFeedly(apiUrl, urlParameters);

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

    public String getBaseUrl() {
        return "http://" + this.basename + ".feedly.com";
    }

    public FeedlyConnection getConnection() {
        return this.connection;
    }

    public void setOnAuthenticatedListener(OnAuthenticatedListener listener) {
        this.listener = listener;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean getVerbose() {
        return this.verbose;
    }

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

    public void deleteSubscription(Subscription subscription) {
        String feedId = subscription.getId();

        httpHelper.sendDeleteRequestToFeedly("/v3/subscriptions/" + feedId);

        System.err.println("jfeedly: deleting subscriptions seems not to work at the moment :(");
    }

    public ArrayList<Feed> searchFeeds(String query) {
        return this.searchFeeds(query, 20);
    }

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

    public Entries getEntries() {
        return this.getEntries(10000);
    }

    public Entries getEntries(int number) {
        Profile profile = this.getProfile();

        return this.getEntriesFor(Category.getGlobalAllCategory(profile).getCategoryId(), true, true, number);
    }

    public Entries getEntriesFor(Category category) {
        return this.getEntriesFor(category.getCategoryId(), true, true, 10000);
    }

    public Entries getEntriesFor(Category category, boolean unreadOnly) {
        return this.getEntriesFor(category.getCategoryId(), unreadOnly, true, 10000);
    }

    public Entries getEntriesFor(String categoryId, boolean unreadOnly, boolean showNewest, int number) {
        String entryIdResponse = httpHelper.sendGetRequestToFeedly("/v3/streams/ids?streamId=" + categoryId +
            "&unreadOnly=" + unreadOnly + "&count=" + number + "&ranked=" + (showNewest ? "newest" : "oldest"));

        String response = httpHelper.sendPostRequestToFeedly("/v3/entries/.mget", entryIdResponse, true);

        return Entries.fromJSONArray(new JSONArray(response));
    }

    public Feed getFeedById(String feedId) {
        String response = httpHelper.sendPostRequestToFeedly("/v3/feeds/.mget", "[ \"" + feedId + "\" ]", true);

        JSONArray objects = new JSONArray(response);

        JSONObject object = objects.getJSONObject(0);

        return Feed.fromJSONObject(object);
    }

    public int getCountOfUnreadArticles(Category category) {
        return this.getCountOfUnreadArticles(category.getCategoryId());
    }

    public int getCountOfUnreadArticles(Subscription subscription) {
        return this.getCountOfUnreadArticles(subscription.getId());
    }

    private int getCountOfUnreadArticles(String id) {
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

    public void markAsRead(Entry entry) {
        this.markAsRead(entry.getId(), "entries");
    }

    public void markAsRead(Subscription subscription) {
        this.markAsRead(subscription.getId(), "feeds");
    }

    public void markAsRead(Feed feed) {
        this.markAsRead(feed.getId(), "feeds");
    }

    public void markAsRead(Category category) {
        this.markAsRead(category.getCategoryId(), "categories");
    }

    private void markAsRead(String id, String type) {
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

        /*if(!type.equals("entries")) {
            object.put("asOf", "");
        }*/

        httpHelper.sendPostRequestToFeedly("/v3/markers", object.toString(), true);
    }

    public static JFeedly createSandboxHandler(String apiSecretKey) {
        return new JFeedly("sandbox", "sandbox", apiSecretKey);
    }

    public static JFeedly createHandler(String clientId, String apiSecretKey) {
        return new JFeedly("cloud", clientId, apiSecretKey);
    }

    public static String getVersion() {
        return MAJOR_VERSION + "." + MINOR_VERSION + "." + PATCH_VERSION;
    }
}
