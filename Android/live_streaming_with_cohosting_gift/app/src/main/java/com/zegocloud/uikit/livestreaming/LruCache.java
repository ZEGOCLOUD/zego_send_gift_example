package com.zegocloud.uikit.livestreaming;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public class LruCache<A, B> extends LinkedHashMap<A, B> {
    public interface RemovalListener<A, B> {
        void onRemoval(A key, B value);
    }
    private final int maxEntries;
    private RemovalListener<A, B> listener;
    public LruCache(final int maxEntries, final RemovalListener<A, B> listener) {
        super(maxEntries + 1, 1.0f, true);
        this.maxEntries = maxEntries;
        this.listener = listener;
    }

    /**
     * Returns <tt>true</tt> if this <code>LruCache</code> has more entries than the maximum specified when it was
     * created.
     *
     * <p>
     * This method <em>does not</em> modify the underlying <code>Map</code>; it relies on the implementation of
     * <code>LinkedHashMap</code> to do that, but that behavior is documented in the JavaDoc for
     * <code>LinkedHashMap</code>.
     * </p>
     *
     * @param eldest
     *            the <code>Entry</code> in question; this implementation doesn't care what it is, since the
     *            implementation is only dependent on the size of the cache
     * @return <tt>true</tt> if the oldest
     * @see java.util.LinkedHashMap#removeEldestEntry(Map.Entry)
     */
    @Override
    protected boolean removeEldestEntry(final Map.Entry<A, B> eldest) {
        if (super.size() > maxEntries) {
            if (listener != null) {
                listener.onRemoval(eldest.getKey(), eldest.getValue());
            }
            return true;
        }
        return false;
    }
}