import java.util.ArrayList;
import java.util.List;


public class HashTable {
    private final int capacity;
    private final SinglyLinkedList<Object>[] buckets;

    @SuppressWarnings("unchecked")
    public HashTable(int capacity) {
        this.capacity = capacity;
        buckets = new SinglyLinkedList[capacity];
        for (int i=0;i<capacity;i++) buckets[i] = new SinglyLinkedList<>();
    }

    private int hash(String key) {
        long h = 2166136261L;
        for (char c : key.toCharArray()) { h ^= c; h *= 16777619; h &= 0xffffffffL; }
        return (int)(h % capacity);
    }

    public void put(String key, Object value) {
        int idx = hash(key);
        SinglyLinkedList<Object> bucket = buckets[idx];
        Object found = bucket.find(o -> ((MapEntry)o).key.equals(key));
        if (found != null) { ((MapEntry)found).value = value; }
        else bucket.insert(new MapEntry(key,value));
    }

    public Object get(String key) {
        int idx = hash(key);
        SinglyLinkedList<Object> bucket = buckets[idx];
        Object found = bucket.find(o -> ((MapEntry)o).key.equals(key));
        return found == null ? null : ((MapEntry)found).value;
    }

    public Object remove(String key) {
        int idx = hash(key);
        SinglyLinkedList<Object> bucket = buckets[idx];
        Object removed = bucket.remove(o -> ((MapEntry)o).key.equals(key));
        return removed == null ? null : ((MapEntry)removed).value;
    }

    public List<String> keys() {
        List<String> out = new ArrayList<>();
        for (int i=0;i<capacity;i++)
            for (Object o : buckets[i].toList()) out.add(((MapEntry)o).key);
        return out;
    }

    private static class MapEntry {
        String key; Object value;
        MapEntry(String k, Object v){ key=k; value=v; }
    }
}
