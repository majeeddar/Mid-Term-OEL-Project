import java.util.ArrayList;
import java.util.List;

public class DoublyLinkedList {
    static class Node {
        Complaint value;
        Node prev, next;
        Node(Complaint v) { value = v; prev = next = null; }
    }
    private Node head, tail, cursor;

    public DoublyLinkedList() { head = tail = cursor = null; }

    public void append(Complaint c) {
        Node n = new Node(c);
        if (head == null) { head = tail = n; } else { tail.next = n; n.prev = tail; tail = n; }
        cursor = n;
    }

    public Complaint moveBack() { if (cursor != null && cursor.prev != null) { cursor = cursor.prev; return cursor.value; } return null; }
    public Complaint moveForward() { if (cursor != null && cursor.next != null) { cursor = cursor.next; return cursor.value; } return null; }

    public List<Complaint> toList() {
        List<Complaint> out = new ArrayList<>();
        Node cur = head;
        while (cur != null) { out.add(cur.value); cur = cur.next; }
        return out;
    }
}
