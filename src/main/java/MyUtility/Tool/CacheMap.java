package MyUtility.Tool;

import java.util.HashMap;

public abstract class CacheMap<K, V> extends HashMap<K, V> {
    @Override
    public V get(Object key) {
        if(!this.containsKey(key)){
            V value = this.cache((K)key);
            this.put((K)key, value);
            return value;
        }
        return super.get(key);
    }

    abstract public V cache(K key);
}
