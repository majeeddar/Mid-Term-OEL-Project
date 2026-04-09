public class CircularQueue {
    private Complaint[] data;
    private int head, tail, size;

    public CircularQueue(int capacity) { data = new Complaint[capacity]; head=tail=size=0; }

    public boolean enqueue(Complaint c) {
        if (size==data.length) return false;
        data[tail]=c; tail=(tail+1)%data.length; size++; return true;
    }

    public Complaint dequeue() {
        if (size==0) return null;
        Complaint c=data[head]; data[head]=null; head=(head+1)%data.length; size--; return c;
    }

    public boolean isEmpty() { return size==0; }
    public int size() { return size; }
    public Complaint[] snapshot() { return data.clone(); }
}
