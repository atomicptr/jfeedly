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

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

/**
 * Representation of a connection to Feedly
 * @author Christopher Kaster
 */
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

    /** Is the access token expired? */
    public boolean isExpired() {
        Date currentDate = new Date();

        return currentDate.after(this.expireDate);
    }

    /** Returns the refresh token */
    public String getRefreshToken() {
        return this.refreshToken;
    }

    /** Returns the access token */
    public String getAccessToken() {
        return this.accessToken;
    }

    /** Returns the plan (standard or pro) */
    public String getPlan() {
        return this.plan;
    }

    /** refresh the tokens by a given JSON object (which was an answer from the server) */
    public void refresh(JSONObject object) {
        this.accessToken = object.getString("access_token");
        this.id = object.getString("id");
        this.tokenType = object.getString("token_type");
        this.plan = object.getString("plan");
        this.expireDate = FeedlyConnection.getExpireDate(object.getLong("expires_in"));

        this.save();
    }

    /** Save the connection details to a file */
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

    /** Restores an existing connection */
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

    /** Initiate a new connection */
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

    /** Was there once a connection? */
    public static boolean oldConnectionExists() {
        File connectionFile = new File("connection.properties");

        return connectionFile.exists();
    }
}
