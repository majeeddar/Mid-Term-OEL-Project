import java.util.ArrayList;
import java.util.List;


public class MinHeap {
    private final List<Complaint> heap = new ArrayList<>();

    private int cmp(Complaint a, Complaint b) {
        if (a.severity != b.severity) return Integer.compare(a.severity, b.severity);
        return Long.compare(a.timestamp, b.timestamp);
    }

    public void push(Complaint c) {
        heap.add(c);
        siftUp(heap.size()-1);
    }

    public Complaint pop() {
        if (heap.isEmpty()) return null;
        Complaint top = heap.get(0);
        Complaint last = heap.remove(heap.size()-1);
        if (!heap.isEmpty()) { heap.set(0,last); siftDown(0); }
        return top;
    }

    public Complaint peek() { return heap.isEmpty() ? null : heap.get(0); }

    public List<Complaint> toList() { return new ArrayList<>(heap); }

    public int size() { return heap.size(); }

    private void siftUp(int i) {
        while (i>0) {
            int p=(i-1)/2;
            if (cmp(heap.get(p), heap.get(i))<=0) break;
            swap(p,i); i=p;
        }
    }

    private void siftDown(int i) {
        int n=heap.size();
        while (true) {
            int l=2*i+1, r=2*i+2, s=i;
            if (l<n && cmp(heap.get(l), heap.get(s))<0) s=l;
            if (r<n && cmp(heap.get(r), heap.get(s))<0) s=r;
            if (s==i) break;
            swap(i,s); i=s;
        }
    }

    private void swap(int i,int j){ Complaint t=heap.get(i); heap.set(i, heap.get(j)); heap.set(j,t); }
}
