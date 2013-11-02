package de.kasoki.jfeedly;

import de.kasoki.jfeedly.model.Categories;
import de.kasoki.jfeedly.model.Category;
import de.kasoki.jfeedly.model.Profile;

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
    }
}
