package de.kasoki.jfeedly;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Example {
    public static void main(String[] args) {
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream("settings.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFeedly feedly = JFeedly.createSandboxHandler(prop.getProperty("secret_api_key"));

        feedly.authenticate();
    }
}
