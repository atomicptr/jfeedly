package de.kasoki.jfeedly;

import de.kasoki.jfeedly.model.*;

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

        String secretApiKey = prop.getProperty("secret_api_key");

        JFeedly feedly = JFeedly.createSandboxHandler(secretApiKey);

        feedly.setVerbose(false);

        feedly.authenticate();

        // get profile
        Profile profile = feedly.getProfile();

        System.out.println("Hello, " + profile.getGivenName() + " " + profile.getFamilyName());

        // get categories
        Categories categories = feedly.getCategories();

        System.out.println("\nThese are your categories:");

        for(Category c : categories) {
            System.out.println("* " + c.getLabel());
        }

        // get subscriptions
        Subscriptions subscriptions = feedly.getSubscriptions();

        System.out.println("\nThese are your subscriptions:");

        for(Subscription s : subscriptions) {
            System.out.println("* " + s.getTitle() + " - Categories: " + s.getCategoryIds().size());
        }
    }
}
