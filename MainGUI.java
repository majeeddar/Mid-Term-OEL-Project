import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainGUI {
    private JFrame frame;
    private ComplaintManager manager;
    private JTextArea logArea;

    public MainGUI() {
        manager = new ComplaintManager();
        build();
    }

    private void build() {
        frame = new JFrame("KCCMS - Karachi City Complaint Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100,700);
        frame.setLayout(new BorderLayout());
    
        JLabel title = new JLabel("KCCRS - Karachi City Complaint Management System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        frame.add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        JPanel regPanel = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(7,1,4,4));
        JTextField tfCitizen = new JTextField();
        JTextField tfArea = new JTextField();
        JTextField tfType = new JTextField();
        JComboBox<String> cbSeverity = new JComboBox<>(new String[]{"Critical","High","Medium","Low"});
        JTextArea taDetails = new JTextArea(4,20);
        form.add(labeled("Citizen Name:", tfCitizen));
        form.add(labeled("Area (Korangi, Saddar, Clifton, Gulshan etc.):", tfArea));
        form.add(labeled("Type (Water/Garbage/Electricity/Gas/Traffic):", tfType));
        form.add(labeled("Severity:", cbSeverity));
        form.add(new JScrollPane(taDetails));
        JButton btnAdd = new JButton("Register Complaint");
        btnAdd.addActionListener(e -> {
            try {
                String citizen = tfCitizen.getText().trim();
                String area = tfArea.getText().trim();
                String type = tfType.getText().trim();
                int sev = cbSeverity.getSelectedIndex() + 1; // 1..4
                String details = taDetails.getText().trim();
                if (citizen.isEmpty() || area.isEmpty() || type.isEmpty() || details.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Fill all fields.");
                    return;
                }
                Complaint c = new Complaint(citizen, area, type, sev, details);
                String res = manager.addComplaint(c);
                log("Registered: " + c.toString() + " -> " + res);
                tfCitizen.setText(""); tfArea.setText(""); tfType.setText(""); taDetails.setText("");
            } catch(Exception ex) { JOptionPane.showMessageDialog(frame, "Error: "+ex.getMessage()); }
        });
        form.add(btnAdd);
        regPanel.add(form, BorderLayout.NORTH);
        tabs.addTab("Register Complaint", regPanel);

        
        JPanel procPanel = new JPanel(new GridLayout(8,1,6,6));
        JButton btnProcessPriority = new JButton("Process Next (Priority)");
        btnProcessPriority.addActionListener(e -> {
            Complaint c = manager.processNextPriority();
            if (c==null) log("No urgent complaints.");
            else log("Processed(priority): " + c.toString());
        });
        JButton btnProcessFIFO = new JButton("Process Next (FIFO)");
        btnProcessFIFO.addActionListener(e -> {
            Complaint c = manager.processNextFIFO();
            if (c==null) log("No pending in queue.");
            else log("Processed(FIFO): " + c.toString());
        });
        JButton btnPeek = new JButton("Peek Next Urgent");
        btnPeek.addActionListener(e -> {
            Complaint c = manager.peekUrgent();
            log("Next urgent: " + (c==null? "none": c.toString()));
        });
        JButton btnShowHeap = new JButton("Show Heap Contents");
        btnShowHeap.addActionListener(e -> {
            List<Complaint> list = manager.heapContents();
            if (list.isEmpty()) log("Heap empty");
            else { log("Heap contents:"); for (Complaint c : list) log("  " + c.toString()); }
        });
        JButton btnShowQueue = new JButton("Show Pending Queue");
        btnShowQueue.addActionListener(e -> {
            List<Complaint> list = manager.pendingQueueList();
            if (list.isEmpty()) log("Queue empty");
            else { log("Pending queue:"); for (Complaint c : list) log("  "+c.toString()); }
        });
        JButton btnUndo = new JButton("Undo Last Operation");
        btnUndo.addActionListener(e -> { log("Undo: " + manager.undoLast()); });

        procPanel.add(btnProcessPriority); procPanel.add(btnProcessFIFO); procPanel.add(btnPeek);
        procPanel.add(btnShowHeap); procPanel.add(btnShowQueue); procPanel.add(btnUndo);
        tabs.addTab("Processing", procPanel);

        JPanel rptPanel = new JPanel(new GridLayout(6,1,6,6));
        JComboBox<String> cbAlgo = new JComboBox<>(new String[]{"merge","quick","insertion","selection"});
        JButton btnArea = new JButton("Report: Sort by Area");
        btnArea.addActionListener(e -> {
            List<Complaint> r = manager.reportByArea((String)cbAlgo.getSelectedItem());
            showReportDialog("Area Report ("+cbAlgo.getSelectedItem()+")", r);
        });
        JButton btnSev = new JButton("Report: Sort by Severity");
        btnSev.addActionListener(e -> {
            List<Complaint> r = manager.reportBySeverity((String)cbAlgo.getSelectedItem());
            showReportDialog("Severity Report ("+cbAlgo.getSelectedItem()+")", r);
        });
        JButton btnTime = new JButton("Report: Sort by Time");
        btnTime.addActionListener(e -> {
            List<Complaint> r = manager.reportByTime((String)cbAlgo.getSelectedItem());
            showReportDialog("Time Report ("+cbAlgo.getSelectedItem()+")", r);
        });
        rptPanel.add(new JLabel("Choose sorting algorithm:")); rptPanel.add(cbAlgo);
        rptPanel.add(btnArea); rptPanel.add(btnSev); rptPanel.add(btnTime);
        tabs.addTab("Reports", rptPanel);

        JPanel regView = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new GridLayout(1,3,6,6));
        JTextField tfSearchId = new JTextField();
        JButton btnSearch = new JButton("Search by ID");
        btnSearch.addActionListener(e -> {
            Complaint c = manager.searchById(tfSearchId.getText().trim());
            log("Search result: " + (c==null? "not found": c.toString()));
        });
        JButton btnListAll = new JButton("List All Complaints");
        btnListAll.addActionListener(e -> {
            List<Complaint> list = manager.listAllComplaints();
            if (list.isEmpty()) log("No registered complaints");
            else { log("All complaints:"); for (Complaint c : list) log("  " + c.toString()); }
        });
        top.add(tfSearchId); top.add(btnSearch); top.add(btnListAll);
        regView.add(top, BorderLayout.NORTH);

        
        JButton btnReverse = new JButton("Display Registry in Reverse (recursion)");
        btnReverse.addActionListener(e -> {
            List<Complaint> rev = manager.registryInReverse();
            if (rev.isEmpty()) log("Registry empty");
            else { log("Registry reverse:"); for (Complaint c : rev) log("  "+c.toString()); }
        });
        regView.add(btnReverse, BorderLayout.CENTER);
        tabs.addTab("Registry & Search", regView);

        
        JPanel persist = new JPanel(new GridLayout(4,1,6,6));
        JButton btnSave = new JButton("Save to kccrs_data.txt");
        btnSave.addActionListener(e -> {
            try { manager.saveToFile("kccrs_data.txt"); log("Saved to kccrs_data.txt"); } catch(Exception ex){ log("Save failed: "+ex.getMessage()); }
        });
        JButton btnLoad = new JButton("Load from kccrs_data.txt");
        btnLoad.addActionListener(e -> {
            try { manager.loadFromFile("kccrs_data.txt"); log("Loaded from kccrs_data.txt"); } catch(Exception ex){ log("Load failed: "+ex.getMessage()); }
        });
        JButton btnSample = new JButton("Load Sample Data");
        btnSample.addActionListener(e -> {
            loadSample();
        });
        persist.add(btnSave); persist.add(btnLoad); persist.add(btnSample);
        tabs.addTab("Persistence", persist);

        frame.add(tabs, BorderLayout.WEST);

        
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane sp = new JScrollPane(logArea);
        frame.add(sp, BorderLayout.CENTER);

    
        JTextArea help = new JTextArea();
        help.setEditable(false);
        help.setText("KCCRS - Complexity summary:\n"
            + "HashTable put/get/remove: avg O(1), worst O(n)\n"
            + "SinglyLinkedList insert O(1), find/remove O(n)\n"
            + "MinHeap push/pop O(log n)\n"
            + "CircularQueue enqueue/dequeue O(1)\n"
            + "Sorts: merge O(n log n), quick avg O(n log n)\n"
            + "Undo: push/pop O(1), undo removal may be O(n)\n");
        frame.add(new JScrollPane(help), BorderLayout.EAST);

        frame.setVisible(true);
        log("KCCRS ready. Load Sample Data to begin or register new complaints.");
    }

    private JPanel labeled(String label, java.awt.Component comp) {
        JPanel p=new JPanel(new BorderLayout()); p.add(new JLabel(label), BorderLayout.NORTH); p.add(comp, BorderLayout.CENTER); return p;
    }

    private void loadSample() {
        manager.addComplaint(new Complaint("Ali","Nazimabad","water",2,"No water in sector C"));
        manager.addComplaint(new Complaint("Zara","Clifton","electricity",1,"Power outage at hospital"));
        manager.addComplaint(new Complaint("Farhan","Korangi","garbage",3,"Overflowing dustbins"));
        manager.addComplaint(new Complaint("Ayesha","Saddar","traffic",2,"Major traffic jam at main chowrangi"));
        manager.addComplaint(new Complaint("Bilal","Clifton","electricity",1,"Power outage at hospital")); // duplicate
        log("Sample data loaded.");
    }

    private void showReportDialog(String title, List<Complaint> list) {
        if (list.isEmpty()) { log(title + ": No records"); return; }
        StringBuilder sb = new StringBuilder();
        sb.append(title).append(" (").append(list.size()).append(")\n\n");
        for (Complaint c : list) sb.append(c.toString()).append(" :: ").append(c.details).append("\n");
        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(900,500));
        JOptionPane.showMessageDialog(frame, sp, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void log(String s) { logArea.append(s + "\n"); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}






