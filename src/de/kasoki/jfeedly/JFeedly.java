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

    private JFeedly(String basename, String clientId, String apiSecretKey) {
        this.basename = basename;
        this.clientId = clientId;
        this.apiSecretKey = apiSecretKey;

        this.httpHelper = new HTTPConnections(this);
    }

    public void authenticate() {
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
        return this.getBaseUrl() + "/v3/auth/auth?response_type=code&client_id=" + this.clientId + "&redirect_uri=http://localhost&scope=https://cloud.feedly.com/subscriptions";
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

    public static JFeedly createSandboxHandler(String apiSecretKey) {
        return new JFeedly("sandbox", "sandbox", apiSecretKey);
    }

    public static JFeedly createHandler(String clientId, String apiSecretKey) {
        return new JFeedly("cloud", clientId, apiSecretKey);
    }
}
