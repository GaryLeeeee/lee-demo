package hashmap;

/**
 * Created by GaryLee on 2019-01-21 10:43.
 * Map接口
 */
public interface MyMap<K,V> {
    int size();
    boolean isEmpty();
    V get(Object key);
    V put(K key,V value);
    V remove(Object key);
    void clear();
    boolean equals(Object o);
    int hashCode();
    interface Entry<K,V>{
        K getKey();
        V getValue();
        V setValue(V value);
    }
}
