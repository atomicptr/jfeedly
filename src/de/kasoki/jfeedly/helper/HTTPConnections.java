package de.kasoki.jfeedly.helper;

import de.kasoki.jfeedly.JFeedly;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPConnections {

    private JFeedly jfeedlyHandler;

    private static enum RequestType {
        POST,
        GET,
        DELETE
    };

    public HTTPConnections(JFeedly feedly) {
        this.jfeedlyHandler = feedly;
    }

    public String sendPostRequestToFeedly(String apiUrl, String urlParameters) {
        return this.sendPostRequestToFeedly(apiUrl, urlParameters, false);
    }

    public String sendPostRequestToFeedly(String apiUrl, String urlParameters, boolean isAuthenticated) {
        return this.sendRequest(apiUrl, urlParameters, isAuthenticated, RequestType.POST);
    }

    public String sendPostRequestToFeedly(String apiUrl, String urlParameters, boolean isAuthenticated,
                                          String contentType) {
        return this.sendRequest(apiUrl, urlParameters, isAuthenticated, RequestType.POST, contentType);
    }

    public String sendGetRequestToFeedly(String apiUrl) {
        return this.sendRequest(apiUrl, "", true, RequestType.GET);
    }

    public String sendDeleteRequestToFeedly(String apiUrl) {
        return this.sendRequest(apiUrl, "", true, RequestType.DELETE);
    }

    private String sendRequest(String apiUrl, String parameters, boolean isAuthenticated, RequestType type) {
        return this.sendRequest(apiUrl, parameters, isAuthenticated, type, "application/json");
    }

    private String sendRequest(String apiUrl, String parameters, boolean isAuthenticated, RequestType type,
                               String contentType) {
        try {
            String url = this.jfeedlyHandler.getBaseUrl() + apiUrl;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestProperty("User-Agent", "jfeedly");
            con.setRequestProperty("Content-Type", contentType);

            if(isAuthenticated) {
                con.setRequestProperty("Authorization", "OAuth " + this.jfeedlyHandler.getConnection().getAccessToken());
            }

            // Send request header
            if(type == RequestType.POST) {
                con.setRequestMethod("POST");

                con.setDoOutput(true);

                DataOutputStream writer = new DataOutputStream(con.getOutputStream());

                writer.write(parameters.getBytes());
                writer.flush();

                writer.close();
            } else if(type == RequestType.GET) {
                con.setRequestMethod("GET");
            } else if(type == RequestType.DELETE) {
                con.setRequestMethod("DELETE");
            } else {
                System.err.println("jfeedly: Unkown RequestType " + type);
            }

            int responseCode = con.getResponseCode();

            if(jfeedlyHandler.getVerbose()) {
                System.out.println("\n" + type + " to: " + url);
                System.out.println("content : " + parameters);
                System.out.println("\nResponse Code : " + responseCode);
            }

            BufferedReader in = null;

            // if error occurred
            if(responseCode >= 400) {
                in = new BufferedReader(
                        new InputStreamReader(con.getErrorStream()));
            } else {
                in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            String serverResponse = response.toString();

            if(jfeedlyHandler.getVerbose()) {
                //print response
                String printableResponse = format(serverResponse);

                if(responseCode >= 400) {
                    System.err.println(printableResponse);
                } else {
                    System.out.println(printableResponse);
                }
            }

            return serverResponse;
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private String format(String string) {
        if(isValidJSONObject(string)) {
            return new JSONObject(string).toString(4);
        } else if(isValidJSONArray(string)) {
            return new JSONArray(string).toString(4);
        } else {
            return string;
        }
    }

    private boolean isValidJSONObject(String jsonString) {
        // try if it is an JSONObject
        try {
            new JSONObject(jsonString);
            return true;
        } catch(JSONException ex) {
            // do nothing
        }

        return false;
    }

    private boolean isValidJSONArray(String jsonString) {
        // try if it is an JSONArray
        try {
            new JSONArray(jsonString);
            return true;
        } catch(JSONException ex) {
            // do nothing
        }

        return false;
    }
}
