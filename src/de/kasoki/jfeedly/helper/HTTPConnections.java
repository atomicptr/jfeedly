package de.kasoki.jfeedly.helper;

import de.kasoki.jfeedly.JFeedly;

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

    public String sendGetRequestToFeedly(String apiUrl) {
        return this.sendRequest(apiUrl, "", true, RequestType.GET);
    }

    public String sendDeleteRequestToFeedly(String apiUrl) {
        return this.sendRequest(apiUrl, "", true, RequestType.DELETE);
    }

    private String sendRequest(String apiUrl, String parameters, boolean isAuthenticated, RequestType type) {
        try {
            String url = this.jfeedlyHandler.getBaseUrl() + apiUrl;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestProperty("User-Agent", "jfeedly");
            con.setRequestProperty("Content-Type", "application/json");

            if(isAuthenticated) {
                con.setRequestProperty("Authorization", "OAuth " + this.jfeedlyHandler.getConnection().getAccessToken());
            }

            // Send request header
            if(type == RequestType.POST) {
                con.setRequestMethod("POST");

                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(parameters);
                wr.flush();
                wr.close();
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
                System.out.println("parameters : " + parameters);
                System.out.println("\nResponse Code : " + responseCode);
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            String serverResponse = response.toString();

            if(jfeedlyHandler.getVerbose()) {
                //print response
                System.out.println(serverResponse);
            }

            return serverResponse;
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
