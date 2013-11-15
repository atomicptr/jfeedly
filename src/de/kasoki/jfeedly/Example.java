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
package de.kasoki.jfeedly;

import de.kasoki.jfeedly.components.OnAuthenticatedListener;
import de.kasoki.jfeedly.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Example {

    private static OnAuthenticatedListener listener = new OnAuthenticatedListener() {
        @Override
        public void onAuthenticated(JFeedly feedly) {
            System.out.println("JFeedly is now Authenticated");

            System.out.println("JFeedly is cached? " + ((feedly instanceof JFeedlyCached) ? "True" : "False"));

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

            ArrayList<Category> cList = new ArrayList<Category>();

            cList.add(categories.get(0));
            cList.add(categories.get(1));
            cList.add(categories.get(2));

            // get first subscription
            Subscription s = subscriptions.get(0);

            // add subscription
            //feedly.subscribe("http://kasoki.de/rss", "Kasokis Blog", cList);

            // update subscription
            //s.setTitle("OH MAI GOSH");
            //s.update(feedly);

            // get tags
            Tags tags = feedly.getTags();

            System.out.println("\nTags:");

            for(Tag t : tags) {
                System.out.println("* " + t.getId() + (t.getLabel() != null ? ", Label: " + t.getLabel() : ""));
            }

            // search
            ArrayList<Feed> feeds = feedly.searchFeeds("android");

            System.out.println("\nFeeds:");

            for(Feed f : feeds) {
                System.out.println("* " + f.getTitle() + " (" + f.getDescription() + ") Num: " +
                        f.getNumberOfSubscribers());
            }

            // get all entries

            System.out.println("\nEntries:");

            Entries entries = feedly.getEntries(20);

            for(Entry entry : entries) {
                System.out.println("* " + entry.getTitle() + " by " + entry.getAuthor());
            }

            // get count of all unread articles
            System.out.println("Number of all unread articles: " +
                    feedly.getCountOfUnreadArticles(Category.getGlobalAllCategory(profile)));

            // get count of unread articles from subscription 0
            System.out.println(subscriptions.get(0).getTitle() + ": " +
                    feedly.getCountOfUnreadArticles(subscriptions.get(0)));

            // mark category 1 as read
            categories.get(1).markAsRead(feedly);

            // get OPML subscriptions
            System.out.println(feedly.exportOPML());

            // search in feeds (pro required)
            System.out.println(feedly.searchInFeeds(entries.get(0).getId(), "a"));
        }
    };

    public static void main(String[] args) {
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream("settings.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String secretApiKey = prop.getProperty("secret_api_key");

        JFeedly feedly = JFeedly.createCachcedSandboxHandler(secretApiKey);

        feedly.setVerbose(false);

        feedly.setOnAuthenticatedListener(listener);

        feedly.authenticate();
    }
}
