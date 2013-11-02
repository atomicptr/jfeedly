package de.kasoki.jfeedly.model;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class FeedlyConnection {
    private String accessToken;
    private String refreshToken;
    private String plan;
    private String tokenType;
    private String id;
    private Date expireDate;

    private FeedlyConnection(HashMap<String, String> map) {
        this.accessToken = map.get("access_token");
        this.refreshToken = map.get("refresh_token");
        this.plan = map.get("plan");
        this.tokenType = map.get("token_type");
        this.id = map.get("id");

        this.expireDate = new Date(Long.parseLong(map.get("expire_date")));
    }

    public boolean isExpired() {
        Date currentDate = new Date();

        return currentDate.after(this.expireDate);
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void refresh(JSONObject object) {
        this.accessToken = object.getString("access_token");
        this.id = object.getString("id");
        this.tokenType = object.getString("token_type");
        this.plan = object.getString("plan");
        this.expireDate = FeedlyConnection.getExpireDate(object.getLong("expires_in"));

        this.save();
    }

    public void save() {
        Properties prop = new Properties();

        prop.setProperty("access_token", this.accessToken);
        prop.setProperty("refresh_token", this.refreshToken);
        prop.setProperty("plan", this.plan);
        prop.setProperty("token_type", this.tokenType);
        prop.setProperty("id", this.id);
        prop.setProperty("expire_date", Long.toString(this.expireDate.getTime()));

        try {
            prop.store(new FileOutputStream("connection.properties"), null);
        } catch (IOException e) {
            System.err.println("This should only appear if you have no rights to write in this folder!");
            e.printStackTrace();
        }
    }

    public static FeedlyConnection restoreConnection() {
        Properties prop = new Properties();

        try {
            //load connection properties file
            prop.load(new FileInputStream("connection.properties"));

            HashMap<String, String> map = createConnectionHashMap(prop.getProperty("access_token"),
                    prop.getProperty("refresh_token"), prop.getProperty("plan"), prop.getProperty("token_type"),
                    prop.getProperty("id"), prop.getProperty("expire_date"));

            return new FeedlyConnection(map);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static FeedlyConnection newConnection(JSONObject object) {
        Date expireDate = FeedlyConnection.getExpireDate(object.getLong("expires_in"));

        HashMap<String, String> map = createConnectionHashMap(object.getString("access_token"),
                object.getString("refresh_token"), object.getString("plan"), object.getString("token_type"),
                object.getString("id"), Long.toString(expireDate.getTime()));

        FeedlyConnection connection = new FeedlyConnection(map);

        connection.save();

        return connection;
    }

    private static Date getExpireDate(long expiresIn) {
        Date currentDate = new Date();

        Date expireDate = new Date(currentDate.getTime() + (expiresIn * 1000));

        return expireDate;
    }

    private static HashMap<String, String> createConnectionHashMap(String accessToken, String refreshToken,
                                                                   String plan, String tokenType,
                                                                   String id, String expireDate) {

        HashMap<String, String> map = new HashMap<String, String>();

        map.put("access_token", accessToken);
        map.put("refresh_token", refreshToken);
        map.put("plan", plan);
        map.put("token_type", tokenType);
        map.put("id", id);
        map.put("expire_date", expireDate);

        return map;
    }

    public static boolean oldConnectionExists() {
        File connectionFile = new File("connection.properties");

        return connectionFile.exists();
    }
}
