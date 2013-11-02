package de.kasoki.jfeedly;

import de.kasoki.jfeedly.components.BrowserFrame;
import de.kasoki.jfeedly.components.OnAuthenticatedListener;

public class JFeedly {

    public static void main(String[] args) {
        BrowserFrame frame = new BrowserFrame("JFeedly example", "http://sandbox.feedly.com/v3/auth/auth?response_type=code&client_id=sandbox&redirect_uri=http://localhost&scope=https://cloud.feedly.com/subscriptions");

        frame.setOnAuthenticatedListener(new OnAuthenticatedListener() {
            @Override
            public void onSignedIn(String code) {
                System.out.println("Code: " + code);
            }
        });

        frame.setVisible(true);
    }
}
