import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ComplaintManager {
    private final HashTable byId; // id->Complaint
    private final HashTable duplicates; // summaryKey -> List<String> of ids
    private final SinglyLinkedList<Complaint> registryList; // store complaints as linked list
    private final MinHeap heap;
    private final CircularQueue queue;
    private final DoublyLinkedList history;
    private final ArrayStack undoStack;
    private final List<Complaint> all; // array snapshot for sorting/report

    public ComplaintManager() {
        byId = new HashTable(1024);
        duplicates = new HashTable(2048);
        registryList = new SinglyLinkedList<>();
        heap = new MinHeap();
        queue = new CircularQueue(8192);
        history = new DoublyLinkedList();
        undoStack = new ArrayStack();
        all = new ArrayList<>();
    }

  
    @SuppressWarnings("unchecked")
    public String addComplaint(Complaint c) {
        Object existing = duplicates.get(c.summaryKey());
        if (existing != null) {
            List<String> list = (List<String>) existing;
            list.add(c.id);
            duplicates.put(c.summaryKey(), list);
            byId.put(c.id, c);
            registryList.insert(c);
            all.add(c);
            undoStack.push(new UndoEntry("add_dup", c.id));
            return "duplicate";
        } else {
            List<String> list = new ArrayList<>(); list.add(c.id);
            duplicates.put(c.summaryKey(), list);
            byId.put(c.id, c);
            registryList.insert(c);
            all.add(c);
            heap.push(c);
            queue.enqueue(c);
            undoStack.push(new UndoEntry("add", c.id));
            return "added";
        }
    }

    public Complaint searchById(String id) {
        Object o = byId.get(id);
        return o == null ? null : (Complaint) o;
    }

    public List<Complaint> listAllComplaints() { return registryList.toList(); }

    public Complaint processNextPriority() {
        Complaint c = heap.pop();
        if (c != null) {
            history.append(c);
            undoStack.push(new UndoEntry("process", c.id));
            return c;
        }
        return null;
    }

    public Complaint processNextFIFO() {
        Complaint c = queue.dequeue();
        if (c != null) {
            history.append(c);
            undoStack.push(new UndoEntry("process", c.id));
            return c;
        }
        return null;
    }

    public Complaint peekUrgent() { return heap.peek(); }

    public List<Complaint> heapContents() { return heap.toList(); }

    public Complaint[] queueSnapshot() { return queue.snapshot(); }

    @SuppressWarnings("unchecked")
    public String undoLast() {
        Object o = undoStack.pop();
        if (o==null) return "nothing to undo";
        UndoEntry e = (UndoEntry) o;
        if ("add".equals(e.action) || "add_dup".equals(e.action)) {
            Object rem = byId.remove(e.cid);
            if (rem == null) return "not found";
            Complaint comp = (Complaint) rem;
            // remove from all list
            all.removeIf(x -> x.id.equals(e.cid));
            // remove from duplicates map
            List<String> lst = (List<String>) duplicates.get(comp.summaryKey());
            if (lst!=null) { lst.remove(e.cid); if (lst.isEmpty()) duplicates.remove(comp.summaryKey()); else duplicates.put(comp.summaryKey(), lst); }
            // remove from registryList
            registryList.remove(c -> ((Complaint)c).id.equals(e.cid));
            return "removed " + e.cid;
        } else if ("process".equals(e.action)) {
            Object maybe = byId.get(e.cid);
            if (maybe == null) return "cannot revert process";
            Complaint comp = (Complaint) maybe;
            heap.push(comp); // push back into priority heap
            return "reverted process " + e.cid;
        }
        return "unknown undo";
    }

    

    public List<Complaint> reportByArea(String algorithm) {
        Comparator<Complaint> cmp = Comparator.comparing(c -> c.area);
        return sortWithChoice(new ArrayList<>(all), cmp, algorithm);
    }

    public List<Complaint> reportBySeverity(String algorithm) {
        Comparator<Complaint> cmp = Comparator.comparingInt(c -> c.severity);
        return sortWithChoice(new ArrayList<>(all), cmp, algorithm);
    }

    public List<Complaint> reportByTime(String algorithm) {
        Comparator<Complaint> cmp = Comparator.comparingLong(c -> c.timestamp);
        return sortWithChoice(new ArrayList<>(all), cmp, algorithm);
    }

    private List<Complaint> sortWithChoice(List<Complaint> arr, Comparator<Complaint> cmp, String algorithm) {
        if ("merge".equalsIgnoreCase(algorithm)) return Sorts.mergeSort(arr, cmp);
        if ("quick".equalsIgnoreCase(algorithm)) { Sorts.quickSort(arr, cmp); return arr; }
        if ("insertion".equalsIgnoreCase(algorithm)) { Sorts.insertionSort(arr, cmp); return arr; }
        if ("selection".equalsIgnoreCase(algorithm)) { Sorts.selectionSort(arr, cmp); return arr; }
        // default
        return Sorts.mergeSort(arr, cmp);
    }

    public List<Complaint> registryInReverse() {
        List<Complaint> out = new ArrayList<>();
        traverseReverse(registryList.head, out);
        return out;
    }
    private void traverseReverse(SinglyLinkedList.Node<Complaint> node, List<Complaint> out) {
        if (node == null) return;
        traverseReverse(node.next, out);
        out.add(node.value);
    }

      public void saveToFile(String filename) throws IOException {
        try (BufferedWriter bw=new BufferedWriter(new FileWriter(filename))) {
            for (Complaint c : all) { bw.write(c.serializeLine()); bw.newLine(); }
        }
    }

    public void loadFromFile(String filename) throws IOException {
        // clear
        for (String k : byId.keys()) byId.remove(k);
        for (String k : duplicates.keys()) duplicates.remove(k);
        while (heap.size() > 0) heap.pop();
        while (!queue.isEmpty()) queue.dequeue();
        while (!undoStack.isEmpty()) undoStack.pop();
     
        all.clear();
        // read file and rebuild
        try (BufferedReader br=new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line=br.readLine())!=null) {
                Complaint c = Complaint.deserializeLine(line);
                if (c==null) continue;
                byId.put(c.id, c);
                Object exists = duplicates.get(c.summaryKey());
                if (exists!=null) {
                    @SuppressWarnings("unchecked")
                    List<String> lst=(List<String>) exists; lst.add(c.id); duplicates.put(c.summaryKey(), lst);
                } else {
                    List<String> lst=new ArrayList<>(); lst.add(c.id); duplicates.put(c.summaryKey(), lst);
                }
                registryList.insert(c);
                all.add(c);
                heap.push(c);
                queue.enqueue(c);
            }
        }
    }

    public List<Complaint> pendingQueueList() {
        List<Complaint> out=new ArrayList<>();
        for (Complaint c : queue.snapshot()) if (c!=null) out.add(c);
        return out;
    }

    private static class UndoEntry { String action; String cid; UndoEntry(String a, String id){ action=a; cid=id; } }
}
