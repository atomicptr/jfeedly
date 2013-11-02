package de.kasoki.jfeedly;

import de.kasoki.jfeedly.components.BrowserFrame;
import de.kasoki.jfeedly.components.OnAuthenticatedListener;
import de.kasoki.jfeedly.model.FeedlyConnection;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JFeedly {

    private String appName = "JFeedly";

    private FeedlyConnection connection;

    private String basename;
    private String clientId;
    private String apiSecretKey;

    private OnAuthenticatedListener listener = null;

    private JFeedly(String basename, String clientId, String apiSecretKey) {
        this.basename = basename;
        this.clientId = clientId;
        this.apiSecretKey = apiSecretKey;
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
            }
        }

        if(listener != null) {
            listener.onAuthenticated();
        }
    }

    private void requestNewTokens(String code) {
        String apiUrl = "/v3/auth/token/";

        String urlParameters = "code=" + code + "&client_id=" + this.clientId + "&client_secret=" + this.apiSecretKey +
                "&redirect_uri=http://localhost&grant_type=authorization_code";

        String response = sendPostRequestToFeedly(apiUrl, urlParameters);

        JSONObject object = new JSONObject(response);

        this.connection = FeedlyConnection.newConnection(object);
    }

    private void refreshTokens() {
        String apiUrl = "/v3/auth/token/";

        String refreshToken = this.connection.getRefreshToken();

        String urlParameters = "refresh_token=" + refreshToken + "&client_id=" + this.clientId + "&client_secret=" + this.apiSecretKey + "&grant_type=refresh_token";

        String response = sendPostRequestToFeedly(apiUrl, urlParameters);

        JSONObject object = new JSONObject(response);

        this.connection.refresh(object);
    }

    private String sendPostRequestToFeedly(String apiUrl, String urlParameters) {
        try {
            String url = this.getBaseUrl() + apiUrl;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "jfeedly");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nPOST to: " + url);
            System.out.println("parameters : " + urlParameters);
            System.out.println("\nResponse Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String serverResponse = response.toString();

            //print response
            System.out.println(serverResponse);

            return serverResponse;
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private String getAuthenticationUrl() {
        return this.getBaseUrl() + "/v3/auth/auth?response_type=code&client_id=" + this.clientId + "&redirect_uri=http://localhost&scope=https://cloud.feedly.com/subscriptions";
    }

    private String getBaseUrl() {
        return "http://" + this.basename + ".feedly.com";
    }

    public static JFeedly createSandboxHandler(String apiSecretKey) {
        return new JFeedly("sandbox", "sandbox", apiSecretKey);
    }

    public static JFeedly createHandler(String clientId, String apiSecretKey) {
        return new JFeedly("cloud", clientId, apiSecretKey);
    }
}
