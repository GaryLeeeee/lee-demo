package hashmap2;

public interface MyMap<K,V> {
    V get(Object key);
    V put(K key, V value);
    V remove(Object key);
    void clear();
    int size();
    boolean equals(Object o);
    int hashCode();
    interface Entry<K,V>{
        K getKey();
        V getValue();
        V setValue(V value);
    }
}
