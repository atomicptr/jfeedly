package de.kasoki.jfeedly.helper;

import de.kasoki.jfeedly.JFeedly;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HTTPConnections {

    private JFeedly jfeedlyHandler;

    public HTTPConnections(JFeedly feedly) {
        this.jfeedlyHandler = feedly;
    }

    public String sendPostRequestToFeedly(String apiUrl, String urlParameters) {
        return this.sendPostRequestToFeedly(apiUrl, urlParameters, false);
    }

    public String sendPostRequestToFeedly(String apiUrl, String urlParameters, boolean isAuthenticated) {
        try {
            String url = this.jfeedlyHandler.getBaseUrl() + apiUrl;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "jfeedly");
            con.setRequestProperty("Content-Type", "application/json");

            if(isAuthenticated) {
                con.setRequestProperty("Authorization", "OAuth " + this.jfeedlyHandler.getConnection().getAccessToken());
            }

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            if(jfeedlyHandler.getVerbose()) {
                System.out.println("\nPOST to: " + url);
                System.out.println("parameters : " + urlParameters);
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

    public String sendGetRequestToFeedly(String apiUrl) {
        try {
            String url = this.jfeedlyHandler.getBaseUrl() + apiUrl;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", "jfeedly");
            con.setRequestProperty("Authorization", "OAuth " + this.jfeedlyHandler.getConnection().getAccessToken());
            con.setRequestProperty("Content-Type", "application/json");

            int responseCode = con.getResponseCode();

            if(jfeedlyHandler.getVerbose()) {
                System.out.println("\nFeeldy GET: " + url);
                System.out.println("Response Code : " + responseCode);
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if(jfeedlyHandler.getVerbose()) {
                //print result
                System.out.println(response.toString());
            }

            return response.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String sendDeleteRequestToFeedly(String apiUrl) {
        try {
            String url = this.jfeedlyHandler.getBaseUrl() + apiUrl;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("DELETE");

            //add request header
            con.setRequestProperty("User-Agent", "jfeedly");
            con.setRequestProperty("Authorization", "OAuth " + this.jfeedlyHandler.getConnection().getAccessToken());
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);

            int responseCode = con.getResponseCode();

            if(jfeedlyHandler.getVerbose()) {
                System.out.println("\nFeeldy DELETE: " + url);
                System.out.println("Response Code : " + responseCode);
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if(jfeedlyHandler.getVerbose()) {
                //print result
                System.out.println(response.toString());
            }

            return response.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
