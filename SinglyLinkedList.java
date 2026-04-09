import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SinglyLinkedList<T> {
    static class Node<T> {
        T value;
        Node<T> next;
        Node(T v){ value = v; next = null; }
    }
    public Node<T> head;
    public int size;

    public SinglyLinkedList() { head = null; size = 0; }

    public void insert(T v) { Node<T> n = new Node<>(v); n.next = head; head = n; size++; }
    public T find(Predicate<T> pred) {
        Node<T> cur = head;
        while (cur != null) {
            if (pred.test(cur.value)) return cur.value;
            cur = cur.next;
        }
        return null;
    }

    public T remove(Predicate<T> pred) {
        Node<T> cur = head; Node<T> prev = null;
        while (cur != null) {
            if (pred.test(cur.value)) {
                if (prev == null) head = cur.next; else prev.next = cur.next;
                size--; return cur.value;
            }
            prev = cur; cur = cur.next;
        }
        return null;
    }

    public List<T> toList() {
        List<T> out = new ArrayList<>();
        Node<T> cur = head;
        while (cur != null) { out.add(cur.value); cur = cur.next; }
        return out;
    }
}
