package de.kasoki.jfeedly.helper;

import java.util.Date;

public class CachedType<T> {
    private T type;
    private Date creationDate;

    public static final long ONE_MINUTE = 60000;

    private static long refreshTime = 10 * ONE_MINUTE;

    public CachedType() {
        this(null);
    }

    public CachedType(T type) {
        this.set(type);
    }

    public void set(T type) {
        this.type = type;
        this.creationDate = new Date();
    }

    public boolean isEmpty() {
        return this.type == null;
    }

    public Date getRefreshDate() {
        return new Date(creationDate.getTime() + refreshTime);
    }

    public boolean isExpired() {
        Date currentTime = new Date();

        return currentTime.after(this.getRefreshDate());
    }

    public T get() {
        return this.type;
    }

    public static void setRefreshTime(long refreshTime) {
        CachedType.refreshTime = refreshTime;
    }
}
