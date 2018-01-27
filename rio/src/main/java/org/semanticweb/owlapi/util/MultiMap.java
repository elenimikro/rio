package org.semanticweb.owlapi.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @param <Key> key type
 * @param <Value> value type
 * @author ignazio palmisano
 */
public class MultiMap<Key, Value> implements Serializable {

    private static final long serialVersionUID = 30406L;
    private static final int DEFAULT_INITIAL_CAPACITY = 5;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private final Map<Key, Collection<Value>> map;
    private int size = 0;

    /** default constructor */
    public MultiMap() {
        map = createMap();
    }


    /**
     * @param key key
     * @param value value
     * @return true if an insertion occurs
     */
    public boolean put(Key key, Value value) {
        Collection<Value> set = this.map.get(key);
        if (set == null) {
            set = createCollection();
            this.map.put(key, set);
        }
        boolean toReturn = set.add(value);
        if (toReturn) {
            size = -1;
        }
        return toReturn;
    }

    protected Collection<Value> createCollection() {
        return new HashSet<>();
    }


    protected Map<Key, Collection<Value>> createMap() {
        return new HashMap<>();
    }

    /**
     * set an entry to a set of values
     * 
     * @param key key
     * @param values values
     */
    public void setEntry(Key key, Collection<Value> values) {
        this.map.put(key, values);
        this.size = -1;
    }

    /**
     * returns a mutable set of values connected to the key; if no value is connected, returns an
     * immutable empty set.
     * 
     * @param key key
     * @return the set of values connected with the key
     */
    public Collection<Value> get(Key key) {
        final Collection<Value> collection = this.map.get(key);
        if (collection != null) {
            return collection;
        }
        return Collections.emptyList();
    }

    /** @return the set of keys */
    public Set<Key> keySet() {
        return this.map.keySet();
    }

    /** @return all values in the map */
    public Set<Value> getAllValues() {
        Set<Value> toReturn = CollectionFactory.createSet();
        for (Collection<Value> s : this.map.values()) {
            toReturn.addAll(s);
        }
        return toReturn;
    }

    /**
     * removes the set of values connected to the key.
     * 
     * @param key key
     * @return true if removal occurs
     */
    public boolean remove(Key key) {
        if (this.map.remove(key) != null) {
            size = -1;
            return true;
        }
        return false;
    }

    /**
     * removes the value connected to the key; if there is more than one value connected to the key,
     * only one is removed.
     * 
     * @param key key
     * @param value value
     * @return true if removal occurs
     */
    public boolean remove(Key key, Value value) {
        Collection<Value> c = this.map.get(key);
        if (c != null) {
            boolean toReturn = c.remove(value);
            // if false, no change was actually made - skip the rest
            if (!toReturn) {
                return false;
            }
            size = -1;
            if (c.isEmpty()) {
                this.map.remove(key);
            }
            return true;
        }
        return false;
    }

    /** @return the size of the multimap (sum of all the sizes of the sets) */
    public int size() {
        if (size < 0) {
            size = getAllValues().size();
        }
        return this.size;
    }

    /**
     * @param k key
     * @param v value
     * @return true if the pairing (k, v) is in the map (set equality for v)
     */
    public boolean contains(Key k, Value v) {
        final Collection<Value> collection = this.map.get(k);
        if (collection == null) {
            return false;
        }
        return collection.contains(v);
    }

    /**
     * @param k the key
     * @return true if k is a key for the map
     */
    public boolean containsKey(Key k) {
        return this.map.containsKey(k);
    }

    /**
     * @param v value
     * @return true if v is a value for a key in the map
     */
    public boolean containsValue(Value v) {
        for (Collection<Value> c : map.values()) {
            if (c.contains(v)) {
                return true;
            }
        }
        return false;
    }

    /** clear map */
    public void clear() {
        this.map.clear();
        this.size = 0;
    }

    @Override
    public String toString() {
        return "MultiMap " + size() + "\n" + map.toString();// .replace(",",
                                                            // "\n");
    }

    /**
     * add all entries from other map.
     * 
     * @param otherMap map to add
     */
    public void putAll(MultiMap<Key, Value> otherMap) {
        for (Key k : otherMap.keySet()) {
            putAll(k, otherMap.get(k));
        }
    }

    /**
     * @param k key
     * @param v all entries to add
     */
    public void putAll(Key k, Collection<Value> v) {
        Collection<Value> set = map.get(k);
        if (set == null) {
            set = createCollection();
            setEntry(k, set);
        }
        set.addAll(v);
        size = -1;
    }

    /** @return true if all value collections are equal */
    public boolean isValueSetsEqual() {
        if (map.size() < 2) {
            return true;
        }
        List<Collection<Value>> list = new ArrayList<>(map.values());
        for (int i = 1; i < list.size(); i++) {
            if (!list.get(0).equals(list.get(i))) {
                return false;
            }
        }
        return true;
    }
}
