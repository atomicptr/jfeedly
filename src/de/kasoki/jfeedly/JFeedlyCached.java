package de.kasoki.jfeedly;

import de.kasoki.jfeedly.helper.CachedType;
import de.kasoki.jfeedly.model.*;

import java.util.HashMap;

public class JFeedlyCached extends JFeedly {

    private CachedType<Profile> cachedProfile;
    private CachedType<Categories> cachedCategories;
    private CachedType<Subscriptions> cachedSubscriptions;
    private CachedType<Tags> cachedTags;
    private HashMap<String, CachedType<Entries>> cachedEntries;
    private HashMap<String, CachedType<Feed>> cachedFeed;
    private HashMap<String, CachedType<Integer>> cachedUnreadCount;
    private CachedType<String> cachedOPML;

    private JFeedlyCached(String basename, String clientId, String apiSecretKey) {
        super(basename, clientId, apiSecretKey);

        cachedProfile = new CachedType<Profile>();
        cachedCategories = new CachedType<Categories>();
        cachedSubscriptions = new CachedType<Subscriptions>();
        cachedTags = new CachedType<Tags>();
        cachedEntries = new HashMap<String, CachedType<Entries>>();
        cachedFeed = new HashMap<String, CachedType<Feed>>();
        cachedUnreadCount = new HashMap<String, CachedType<Integer>>();
        cachedOPML = new CachedType<String>();
    }

    @Override
    public Profile getProfile() {
        if(cachedProfile.isEmpty() || cachedProfile.isExpired()) {
            cachedProfile.set(super.getProfile());
        }

        return cachedProfile.get();
    }

    @Override
    public Categories getCategories() {
        if(cachedCategories.isEmpty() || cachedCategories.isExpired()) {
            cachedCategories.set(super.getCategories());
        }

        return cachedCategories.get();
    }

    @Override
    public Subscriptions getSubscriptions() {
        if(cachedSubscriptions.isEmpty() || cachedSubscriptions.isExpired()) {
            cachedSubscriptions.set(super.getSubscriptions());
        }

        return cachedSubscriptions.get();
    }

    @Override
    public Tags getTags() {
        if(cachedTags.isEmpty() || cachedTags.isExpired()) {
            cachedTags.set(super.getTags());
        }

        return cachedTags.get();
    }

    /**
     * Returns articles
     * @param id All articles grouped by one ID. May be a subscription, feed, tag or category id
     * @param unreadOnly List only the unread entries
     * @param showNewest Newest first?
     * @param number Maximum number of entries
     * @return A bunch of articles
     */
    @Override
    public Entries getEntriesFor(String id, boolean unreadOnly, boolean showNewest, int number) {
        if(cachedEntries.containsKey(id)) {
            CachedType<Entries> entries = cachedEntries.get(id);

            if(entries.isEmpty() || entries.isExpired()) {
                entries.set(super.getEntriesFor(id, unreadOnly, showNewest, number));
            }
        } else {
            CachedType<Entries> entries = new CachedType<Entries>(
                    super.getEntriesFor(id, unreadOnly, showNewest, number));

            cachedEntries.put(id, entries);
        }

        return cachedEntries.get(id).get();
    }

    /** Get a Feed specified by an ID */
    @Override
    public Feed getFeedById(String feedId) {
        if(cachedFeed.containsKey(feedId)) {
            CachedType<Feed> feed = cachedFeed.get(feedId);

            if(feed.isEmpty() || feed.isExpired()) {
                feed.set(super.getFeedById(feedId));
            }
        } else {
            CachedType<Feed> feed = new CachedType<Feed>(super.getFeedById(feedId));

            cachedFeed.put(feedId, feed);
        }

        return cachedFeed.get(feedId).get();
    }

    /** Returns the number of unread articles for an ID (may be a feed, subscription, category or tag */
    @Override
    protected int getCountOfUnreadArticles(String id) {
        if(cachedUnreadCount.containsKey(id)) {
            CachedType<Integer> unreadCount = cachedUnreadCount.get(id);

            if(unreadCount.isEmpty() || unreadCount.isExpired()) {
                unreadCount.set(super.getCountOfUnreadArticles(id));
            }
        } else {
            CachedType<Integer> unreadCount = new CachedType<Integer>(super.getCountOfUnreadArticles(id));

            cachedUnreadCount.put(id, unreadCount);
        }

        return cachedUnreadCount.get(id).get();
    }

    /**
     * Export the users subscriptions as OPML
     * @return A String which contains a XML/OPML files content.
     */
    @Override
    public String exportOPML() {
        if(cachedOPML.isEmpty() || cachedOPML.isExpired()) {
            cachedOPML.set(super.exportOPML());
        }

        return cachedOPML.get();
    }

    /**
     * Create a cached handler for sandbox usage.
     * @param apiSecretKey Your secret api key. If you have none please contact Feedly.
     * @return A jfeedly api handler
     */
    public static JFeedlyCached createCachedSandboxHandler(String apiSecretKey) {
        return new JFeedlyCached("sandbox", "sandbox", apiSecretKey);
    }

    /**
     * Create a cached handler for production usage
     * @param clientId Your client id.
     * @param apiSecretKey Your secret api key.
     * @return A jfeedly api handler
     */
    public static JFeedlyCached createCachedHandler(String clientId, String apiSecretKey) {
        return new JFeedlyCached("cloud", clientId, apiSecretKey);
    }
}
