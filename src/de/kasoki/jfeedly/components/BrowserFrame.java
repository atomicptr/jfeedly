package de.kasoki.jfeedly.components;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class BrowserFrame extends JFrame {

    private String url;
    private JFXPanel panel;
    private WebView webView;
    private Runnable checkIfSignedIn;
    private volatile boolean isSignedIn = false;
    private OnAuthenticatedListener listener;

    public BrowserFrame(String title, String url) {
        this.url = url;

        this.setTitle(title);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        panel = new JFXPanel();

        this.add(panel);

        // create web component in JavaFX thread
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setupWebComponent();
            }
        });
    }

    public void setOnAuthenticatedListener(OnAuthenticatedListener listener) {
        this.listener = listener;
    }

    private void setupWebComponent() {
        this.webView = new WebView();
        Group group = new Group();
        Scene scene = new Scene(group);
        this.panel.setScene(scene);

        group.getChildren().add(this.webView);
        this.getWebEngine().load(url);

        this.getWebEngine().setJavaScriptEnabled(true);

        // setup checkIfSignedIn runnable
        checkIfSignedIn = new Runnable() {

            @Override
            public void run() {
                while(!BrowserFrame.this.isSignedIn()) {
                    if(BrowserFrame.this.isUrlValid()) {
                        BrowserFrame.this.isSignedIn = true;

                        String code = BrowserFrame.this.getCodeFromUrl();

                        // call back to listener
                        listener.onSignedIn(code);

                        // dispose window
                        BrowserFrame.this.setVisible(false);
                    }
                }
            }
        };

        new Thread(checkIfSignedIn).start();
    }

    private WebEngine getWebEngine() {
        return this.webView.getEngine();
    }

    private boolean isUrlValid() {
        String regex = "^http://localhost/\\?code=(.)*";

        return this.getUrl().matches(regex);
    }

    private String getUrl() {
        return this.getWebEngine().getLocation();
    }

    private String getCodeFromUrl() {
        try {
            URL url = new URL(this.getUrl());

            String query = url.getQuery();

            String[] params = query.split("&");

            for (String param : params) {
                int index = param.indexOf("=");

                String key = URLDecoder.decode(param.substring(0, index), "UTF-8");
                String value = URLDecoder.decode(param.substring(index + 1), "UTF-8");

                if(key.equals("code")) {
                    return value;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isSignedIn() {
        return this.isSignedIn;
    }
}
